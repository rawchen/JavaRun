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
import java.io.PrintWriter;
import java.io.StringWriter;
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
		try {
			if (StringUtil.isEmpty(javaSource)) {
				return ResultResponse.Build(ResultTypeEnum.error, "代码不能为空！");
			}

			if (StringUtil.isDangerousWithLinux(javaSource)) {
				return ResultResponse.Build(ResultTypeEnum.error, "代码似乎在做什么哦！");
			}

			if (executeTimeLimit > 10000L) {
				return ResultResponse.Build(ResultTypeEnum.error, "超时时间不能大于10s！");
			}

			if (executeTimeLimit <= 0L) {
				return ResultResponse.Build(ResultTypeEnum.error, "超时时间不能小于1ms！");
			}

			//编译
			service.compile(javaSource);
			//运行
			return service.run(executeTimeLimit, executeArgs);

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
}
