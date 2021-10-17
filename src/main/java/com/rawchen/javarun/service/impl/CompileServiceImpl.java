package com.rawchen.javarun.service.impl;

import com.rawchen.javarun.config.Constants;
import com.rawchen.javarun.enums.ResultTypeEnum;
import com.rawchen.javarun.exception.CompileException;
import com.rawchen.javarun.service.CompileService;
import com.rawchen.javarun.vo.ResultResponse;
import com.rawchen.javarun.vo.StringObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.tools.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;


/**
 * 本类实际在用方法只有compile编译文本内容为class文件，其它方法是之前存在的想法，
 * 通过类加载器加载主方法发现基础操作没问题。线程有问题。
 * 所以换了一个Runtime的exec方法执行，相当于java Main
 */
@Service
public class CompileServiceImpl implements CompileService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	Constants constants = new Constants();

	/**
	 * 编译代码保存为class文件
	 *
	 * @param javaSource JAVA代码
	 * @throws Exception
	 */
	@Override
	public void compile(String javaSource) throws Exception {

		DiagnosticCollector<JavaFileObject> diagnosticsCollector = new DiagnosticCollector<>();
		JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
		StandardJavaFileManager standardFileManager = javaCompiler.getStandardFileManager(diagnosticsCollector, null, null);
		StringObject so = new StringObject(Constants.CLASS_NAME, javaSource);

		File file = new File(Constants.CLASS_PATH);
		if (file.exists()) {
			if (!file.isDirectory()) {
				file.mkdir();
			}
		} else {
			file.mkdir();
		}

		Iterable<String> options = Arrays.asList("-d", Constants.CLASS_PATH);
		Iterable<? extends JavaFileObject> files = Collections.singletonList((JavaFileObject) so);

		JavaCompiler.CompilationTask task = javaCompiler.getTask(null, standardFileManager, diagnosticsCollector, options, null, files);
		Boolean result = task.call();

		//编译诊断信息
		StringBuilder sb = new StringBuilder();
		List<Diagnostic<? extends JavaFileObject>> diagnostics = diagnosticsCollector.getDiagnostics();
		for (Diagnostic diagnostic : diagnostics) {
			sb.append(diagnostic.toString()).append("\r\n");
		}

		if (result) {
			logger.info("Compiled java source success!");
		} else {
			logger.info("Compiled java source fail!");
			throw new CompileException(sb.toString());
		}
		//用自定义classLoader加载这个class
//		ClassLoaderUtil classClassLoader = new ClassLoaderUtil(getClass().getClassLoader());
//		Class<?> clazz = classClassLoader.loadClass(CLASS_NAME);
//		return clazz;
	}

	public int executeCommandLine(final String commandLine, final long timeout)
			throws IOException, InterruptedException, TimeoutException {
		Runtime runtime = Runtime.getRuntime();
		Process process = runtime.exec(commandLine);
		/* Set up process I/O. */
		Worker worker = new Worker(process);
		worker.start();
		try {
			worker.join(timeout);
			if (worker.exit != null)
				return worker.exit;
			else
				throw new TimeoutException();
		} catch(InterruptedException ex) {
			worker.interrupt();
			Thread.currentThread().interrupt();
			throw ex;
		} finally {
			process.destroyForcibly();
		}
	}

	private static class Worker extends Thread {
		private final Process process;
		private Integer exit;
		private Worker(Process process) {
			this.process = process;
		}
		public void run() {
			try {
				exit = process.waitFor();
			} catch (InterruptedException ignore) {
				return;
			}
		}
	}

	@Override
	public ResultResponse excuteMainMethod(Class clazz) throws Exception {
		return excuteMainMethodWithClass(clazz, new String[]{});
	}

	@Override
	public ResultResponse excuteMainMethod(Class clazz, String[] args) throws Exception {
		return excuteMainMethodWithClass(clazz, args);
	}

	@Override
	public ResultResponse excuteMainMethod(Class clazz, Long timeLimit) throws Exception {
		return excuteMainMethod(clazz, timeLimit, new String[]{});
	}

	/**
	 * 执行类中main方法，带参数超时
	 *
	 * @param clazz
	 * @param timeLimit 时间限制
	 * @param args      运行参数数组
	 * @return
	 * @throws Exception
	 */
	@Override
	public ResultResponse excuteMainMethod(Class clazz, Long timeLimit, String[] args) throws Exception {
		//使用线程池供多用户并发使用时池化线程
		final ExecutorService executorService = Executors.newFixedThreadPool(10);
		Callable<ResultResponse> mainMethodExcuteCallable = new Callable<ResultResponse>() {
			@Override
			public ResultResponse call() throws Exception {
				return excuteMainMethodWithClass(clazz, args);
			}
		};
		FutureTask<ResultResponse> futureTask = new FutureTask<ResultResponse>(mainMethodExcuteCallable);
		//提交到线程池中去执行
		executorService.submit(futureTask);
		ResultResponse resultResponse = null;
			try {
				resultResponse = futureTask.get(timeLimit, TimeUnit.MILLISECONDS);
				System.out.println(resultResponse);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			} catch (TimeoutException e) {
				futureTask.cancel(true);//超时，就取消
				e.printStackTrace();
				throw new RuntimeException("运行超时，限定时间 " + timeLimit + " ms");
			}
		executorService.shutdown();
		while(true) {
			if (executorService.isTerminated()) {
				System.out.println("所有的子线程都结束了！");
				break;
			}
			Thread.sleep(1000);
		}
		//遇到问题：类加载器执行类方法时，如果带有new Thread()多个子线程怎么统一回调返回结果和输出。
		//java.jsrun.net中使用Thread.currentThread().getName()知道采用的main线程，也就是可以把该类放到主程序启动。
		return resultResponse;
	}

	/**
	 * 执行类中main方法
	 *
	 * @param clazz
	 * @param args
	 * @return
	 * @throws Exception
	 */
	private ResultResponse excuteMainMethodWithClass(Class clazz, String[] args) throws Exception {
		//将输出结果保存在Stream
		ByteArrayOutputStream baoStream = new ByteArrayOutputStream(1024);
		PrintStream cacheStream = new PrintStream(baoStream);
		PrintStream oldStream = System.out;
		System.setOut(cacheStream);

		//执行main方法
		Method method = clazz.getMethod(Constants.EXECUTE_MAIN_METHOD_NAME, String[].class);
		method.invoke(null, (Object) args);
		Long startTime = System.currentTimeMillis();
		Long endTime = System.currentTimeMillis();

		//将输出打印到控制台
		System.setOut(oldStream);
		ResultResponse resultResponse = new ResultResponse();
		resultResponse.setExecuteResult(baoStream.toString());
		resultResponse.setExecuteDurationTime(endTime - startTime);
		resultResponse.setResultTypeEnum(ResultTypeEnum.ok);
		resultResponse.setMessage("OK");
		return resultResponse;
	}
}
