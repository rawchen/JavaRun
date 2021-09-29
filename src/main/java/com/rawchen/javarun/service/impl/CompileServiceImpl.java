package com.rawchen.javarun.service.impl;

import com.rawchen.javarun.config.Constants;
import com.rawchen.javarun.enums.ResultTypeEnum;
import com.rawchen.javarun.exception.CompileException;
import com.rawchen.javarun.service.ComplieService;
import com.rawchen.javarun.util.ClassLoaderUtil;
import com.rawchen.javarun.vo.ResultResponse;
import com.rawchen.javarun.vo.StringObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.tools.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

import static com.rawchen.javarun.config.Constants.className;
import static com.rawchen.javarun.config.Constants.classPath;

@Service
public class CompileServiceImpl implements ComplieService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * 编译代码保存为class文件
	 *
	 * @param javaSource JAVA代码
	 * @return
	 * @throws Exception
	 */
	@Override
	public Class complie(String javaSource) throws Exception {

		DiagnosticCollector<JavaFileObject> diagnosticsCollector = new DiagnosticCollector<>();
		JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
		StandardJavaFileManager standardFileManager = javaCompiler.getStandardFileManager(diagnosticsCollector, null, null);
		StringObject so = new StringObject(className, javaSource);

		File file = new File(classPath);
		if (file.exists()) {
			if (!file.isDirectory()) {
				file.mkdir();
			}
		} else {
			file.mkdir();
		}

		Iterable<String> options = Arrays.asList("-d", classPath);
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
			logger.info("Complied java source success!");
		} else {
			logger.info("Complied java source fail!");
			throw new CompileException(sb.toString());
		}
		//用自定义classLoader加载这个class
		ClassLoaderUtil classClassLoader = new ClassLoaderUtil(getClass().getClassLoader());
		Class<?> clazz = classClassLoader.loadClass(className);
		return clazz;
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

	@Override
	public ResultResponse excuteMainMethod(Class clazz, Long timeLimit, String[] args) throws Exception {
		final ExecutorService executorService = Executors.newFixedThreadPool(10);
		List<FutureTask<ResultResponse>> futureTaskList = new ArrayList<>();
		Callable<ResultResponse> mainMethodExcuteCallable = new Callable<ResultResponse>() {
			@Override
			public ResultResponse call() throws Exception {
				return excuteMainMethodWithClass(clazz, args);
			}
		};
		FutureTask<ResultResponse> futureTask = new FutureTask<ResultResponse>(mainMethodExcuteCallable);
		futureTaskList.add(futureTask);
		executorService.submit(futureTask);//提交到线程池中去执行
		//只里仅仅为了测试，这样写,把多线程当没有线程来用，意思一下
		ResultResponse resultResponse = null;
		FutureTask<ResultResponse> taskItem = futureTaskList.get(0);
//        for (FutureTask<ResultResponse> taskItem : futureTaskList) {
		try {
			resultResponse = taskItem.get(timeLimit, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			taskItem.cancel(true);//超时，就取消
			e.printStackTrace();
			throw new RuntimeException("运行超时，限定时间 " + timeLimit + " ms");
		}
//        }
		return resultResponse;
	}

	private ResultResponse excuteMainMethodWithClass(Class clazz, String[] args) throws Exception {
		ByteArrayOutputStream baoStream = new ByteArrayOutputStream(1024);
		PrintStream cacheStream = new PrintStream(baoStream);
		PrintStream oldStream = System.out;
		System.setOut(cacheStream);//将输出结果保持到baoStream中，以便后面用
		//执行main方法
		Method method = clazz.getMethod(Constants.executeMainMethodName, String[].class);

		Long startTime = System.currentTimeMillis();//开始时间

		method.invoke(null, (Object) args);

		Long endTime = System.currentTimeMillis();//结束时间

		System.setOut(oldStream);//将输出打印到控制台
		String reusltInfo = baoStream.toString();
		ResultResponse resultResponse = new ResultResponse();
		resultResponse.setExecuteResult(reusltInfo);
		System.out.println(reusltInfo);
		resultResponse.setExecuteDurationTime(endTime - startTime);
		resultResponse.setResultTypeEnum(ResultTypeEnum.ok);
		resultResponse.setMessage("OK");
		return resultResponse;
	}

	public static void main(String[] args) {
		String code = "public class Main {\n" +
				"\tpublic static void main(String[] args){\n" +
				"\t\tSystem.out.println(\"Hello World!\");\n" +
				"\t}\n" +
				"}";
		System.out.println(code);
		CompileServiceImpl javaComplieService = new CompileServiceImpl();
		try {
			Class clazz = javaComplieService.complie(code);
			ResultResponse resultResponse = javaComplieService.excuteMainMethod(clazz);
			System.out.println("-->" + resultResponse.getExecuteResult());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
