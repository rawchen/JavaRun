package com.rawchen.javarun.controller;

import com.rawchen.javarun.config.Constants;
import com.rawchen.javarun.enums.ResultTypeEnum;
import com.rawchen.javarun.exception.CompileException;
import com.rawchen.javarun.service.CompileService;
import com.rawchen.javarun.util.FileUtil;
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
	private CompileService service;

	/**
	 * 编译运行控制
	 *
	 * @param javaSource JAVA代码
	 * @return 编译结果
	 */
	@ResponseBody
	@RequestMapping(value = "/compileAndRun")
	public ResultResponse compileAndRun(String javaSource,
			@RequestParam(value = "executeTimeLimit", required = false, defaultValue = "10000")
					long executeTimeLimit,
			@RequestParam(value = "executeArgs", required = false)
					String executeArgs) {

		long start;
		long end;

		try {
			if (StringUtil.isEmpty(javaSource)) {
				return ResultResponse.Build(ResultTypeEnum.error, "代码不能为空！");
			}

			if (StringUtil.isLinuxAndDangerous(javaSource) || StringUtil.isLinuxAndIOOperation(javaSource)) {
				return ResultResponse.Build(ResultTypeEnum.error, "代码似乎在做什么哦，下方链接提供release的jar包。请用win执行！");
			}

			if (executeTimeLimit > 10000L) {
				return ResultResponse.Build(ResultTypeEnum.error, "超时时间不能大于10s！");
			}

			if (executeTimeLimit <= 0L) {
				return ResultResponse.Build(ResultTypeEnum.error, "超时时间不能小于1ms！");
			}

			//编译
			service.compile(javaSource);

			//重定向系统输出流
			ByteArrayOutputStream baoStream = new ByteArrayOutputStream(1024);
			PrintStream cacheStream = new PrintStream(baoStream);
			PrintStream oldStream = System.out;
			System.setOut(cacheStream);

			String[] strings;
			String s;
			Process process;
			BufferedReader bufferedReaderInput;
			BufferedReader bufferedReaderError;

			//不同系统使用不同命令执行
			if (StringUtil.isWinOs()) {
				strings = new String[]{"cmd", "/c", Constants.CLASS_PATH.substring(0, 2)
						+ "&&cd", Constants.CLASS_PATH, "&&java", Constants.MEM_ARGS, Constants.CLASS_NAME};
				//传参
				String[] args = executeArgs.trim().split(" ");
				String[] arrTemp = StringUtil.concat(strings, args);
				process = Runtime.getRuntime().exec(arrTemp);
			} else {
				strings = new String[]{"sh", "-c", "java "+ Constants.MEM_ARGS + " -classpath "
						+ Constants.CLASS_PATH + " " + Constants.CLASS_NAME + " " + executeArgs.trim()};
				process = Runtime.getRuntime().exec(strings);
			}

			start = System.currentTimeMillis();

			if (!process.waitFor(executeTimeLimit, TimeUnit.MILLISECONDS)) {
				//process.destroy();
				process.destroyForcibly();
				FileUtil.deleteClassFileForDir(Constants.CLASS_PATH);
				throw new TimeoutException("运行超时，限定时间 " + executeTimeLimit + " ms");
			}

			bufferedReaderInput = new BufferedReader(new InputStreamReader(process.getInputStream(), StringUtil.isWinOs()?"gbk":"utf-8"));
			while ((s = bufferedReaderInput.readLine()) != null) {
				System.out.println(s);
			}

			bufferedReaderError = new BufferedReader(new InputStreamReader(process.getErrorStream(), StringUtil.isWinOs()?"gbk":"utf-8"));
			while ((s = bufferedReaderError.readLine()) != null) {
				System.out.println(s);
			}

			end = System.currentTimeMillis();

			System.setOut(oldStream);
			ResultResponse result = new ResultResponse();
			result.setExecuteResult(baoStream.toString());
			result.setExecuteDurationTime(end - start);
			result.setResultTypeEnum(ResultTypeEnum.ok);
			result.setMessage("OK");

			bufferedReaderInput.close();
			bufferedReaderError.close();
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
			return ResultResponse.Build(ResultTypeEnum.error, "运行错误！" + e.getMessage());
		} finally {
			FileUtil.deleteClassFileForDir(Constants.CLASS_PATH);
		}
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

	public static void main(String[] args) {
		for (int i = 1; i <= 2000; i++) {
			System.out.println(i);
		}
	}
}
