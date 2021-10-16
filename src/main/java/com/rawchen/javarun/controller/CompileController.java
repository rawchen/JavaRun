package com.rawchen.javarun.controller;

import com.rawchen.javarun.config.Constants;
import com.rawchen.javarun.enums.ResultTypeEnum;
import com.rawchen.javarun.exception.CompileException;
import com.rawchen.javarun.service.CompileService;
import com.rawchen.javarun.util.StringUtil;
import com.rawchen.javarun.vo.ResultResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author RawChen
 * @date 2021/9/26 22:33
 * @desc JAVA编译器controller
 */
@CrossOrigin
@Controller
public class CompileController {

	@Resource
	private CompileService javaCompileService;

	/**
	 * 编译运行控制
	 *
	 * @param javaSource JAVA代码
	 * @return 编译结果
	 */
	@ResponseBody
	@RequestMapping(value = "/compileAndRun")
	public ResultResponse compileAndRun(String javaSource,
										@RequestParam(value = "executeTimeLimit", required = false, defaultValue = "10000") Long executeTimeLimit,
										@RequestParam(value = "executeArgs", required = false) String executeArgs) {
		try {
			if (StringUtil.isEmpty(javaSource)) {
				return ResultResponse.Build(ResultTypeEnum.error, "代码不能为空！");
			}

			if (StringUtil.isDangerous(javaSource)) {
				return ResultResponse.Build(ResultTypeEnum.error, "代码似乎在做什么哦！");
			}

			if (executeTimeLimit != null && executeTimeLimit > 10000L) {
				return ResultResponse.Build(ResultTypeEnum.error, "超时时间不能大于10s！");
			}

			if (executeTimeLimit != null && executeTimeLimit <= 0L) {
				return ResultResponse.Build(ResultTypeEnum.error, "超时时间不能小于1ms！");
			}

			//编译
			javaCompileService.compile(javaSource);

//			Class clazz = Class clazz = javaCompileService.complie(javaSource);
//			String[] args = StringUtil.getInputArgs(executeArgs);

//			if (null == executeTimeLimit && null == args) {
//				//无参数 无时限
//				return javaCompileService.excuteMainMethod(clazz, executeTimeLimit);
//			} else if (null == executeTimeLimit) {
//				//有参数 无时限
//				return javaCompileService.excuteMainMethod(clazz, args);
//			} else if (null == args) {
//				//无参数 有时限
//				if (executeTimeLimit <= 0) {
//					return ResultResponse.Build(ResultTypeEnum.error, "超时时间不能小于1ms！");
//				}
//				return javaCompileService.excuteMainMethod(clazz, executeTimeLimit);
//			} else {
//				//有参数 有时限
//				if (executeTimeLimit <= 0) {
//					return ResultResponse.Build(ResultTypeEnum.error, "超时时间不能小于1ms！");
//				}
//				return javaCompileService.excuteMainMethod(clazz, executeTimeLimit, args);
//			}
//		} catch (CompileException e) {
//			e.printStackTrace();
//			return ResultResponse.Build(ResultTypeEnum.error, "编译错误！" + e.getMessage());
//		} catch (RuntimeException e) {
//			System.gc();
//			return ResultResponse.Build(ResultTypeEnum.error, "运行错误！" + e.getMessage());
//		} catch (Exception e) {
//			System.gc();
//			StringWriter sw = new StringWriter();
//			e.printStackTrace(new PrintWriter(sw, true));
//			return ResultResponse.Build(ResultTypeEnum.error, "运行错误！" + e.getCause());
//		}

			//判断是win还是linux
			boolean isWin = false;
			String os = System.getProperty("os.name");
			if (os.toLowerCase().startsWith("win")) {
				isWin = true;
				System.out.println(os + " 是Win啦！");
			}

			long start;
			long end;

			ByteArrayOutputStream baoStream = new ByteArrayOutputStream(1024);
			PrintStream cacheStream = new PrintStream(baoStream);
			PrintStream oldStream = System.out;
			System.setOut(cacheStream);


			String s;
			String[] strings;
//			System.out.println(Constants.classPath + Constants.className);
			start = System.currentTimeMillis();

			//不同系统使用不同命令执行
			if (isWin) {
				strings = new String[]{"cmd", "/c", "E:&&cd", Constants.classPath, "&&java", Constants.className};
			} else {
				strings = new String[]{"sh", "-c", "cd", Constants.classPath, "&&java", Constants.className};
			}

			String[] args = executeArgs.trim().split(" ");
			String[] arrTemp = StringUtil.concat(strings, args);
			Process process = Runtime.getRuntime().exec(arrTemp);


			if (!process.waitFor(executeTimeLimit, TimeUnit.MILLISECONDS)) {
//				process.destroy();
				process.destroyForcibly();
				throw new TimeoutException("运行超时，限定时间 " + executeTimeLimit + " ms");
			}

			end = System.currentTimeMillis();

			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(), "gbk"));
			while ((s = bufferedReader.readLine()) != null) {
				System.out.println(s);
			}

			BufferedReader bufferedReader02 = new BufferedReader(new InputStreamReader(process.getErrorStream(), "gbk"));
			while ((s = bufferedReader02.readLine()) != null) {
				System.out.println(s);
			}
			System.setOut(oldStream);
			ResultResponse result = new ResultResponse();
			result.setExecuteResult(baoStream.toString());
			result.setExecuteDurationTime(end - start);
			result.setResultTypeEnum(ResultTypeEnum.ok);
			result.setMessage("OK");

			return result;

		} catch (CompileException e) {
			e.printStackTrace();
			return ResultResponse.Build(ResultTypeEnum.error, "编译错误！" + e.getMessage());
		} catch (TimeoutException e) {
			return ResultResponse.Build(ResultTypeEnum.error, "运行超时，限定时间 " + executeTimeLimit + " ms");
		} catch (Exception e) {
			System.gc();
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw, true));
			return ResultResponse.Build(ResultTypeEnum.error, "运行错误！" + e.getCause());
		}
	}

	public ResultResponse test(String javaSource,
				@RequestParam(value = "executeTimeLimit", required = false, defaultValue = "10000") Long executeTimeLimit,
				@RequestParam(value = "executeArgs", required = false) String executeArgs)
			throws IOException, InterruptedException, TimeoutException {

		javaCompileService.executeCommandLine("123", executeTimeLimit);
		return new ResultResponse();
	}

	/**
	 * 主页
	 *
	 * @return
	 */
	@GetMapping(value = {"/", "/index"})
	public String index() {
		return "index";
	}

	public static void main(String[] args) throws Exception {
		String s;
		System.out.println(Constants.classPath + Constants.className);
		Process process = Runtime.getRuntime().exec(new String[]{"cmd", "/c", "E:&&cd", Constants.classPath, "&&java", Constants.className});
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(), "gbk"));
		while ((s = bufferedReader.readLine()) != null)
			System.out.println(s);
		process.waitFor();
	}
}
