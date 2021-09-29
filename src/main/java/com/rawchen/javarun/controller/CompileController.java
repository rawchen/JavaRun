package com.rawchen.javarun.controller;

import com.rawchen.javarun.enums.ResultTypeEnum;
import com.rawchen.javarun.exception.CompileException;
import com.rawchen.javarun.service.ComplieService;
import com.rawchen.javarun.util.StringUtil;
import com.rawchen.javarun.vo.ResultResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author RawChen
 * @date 2021/9/26 22:33
 * @desc JAVA编译器controller
 */
@CrossOrigin
@Controller
public class CompileController {

    @Resource
    private ComplieService javaComplieService;

    /**
     * 编译运行控制
     * @param javaSource JAVA代码
     * @return 编译结果
     */
    @ResponseBody
    @RequestMapping(value = "/complie")
    public ResultResponse complie(String javaSource,
            @RequestParam(value = "excuteTimeLimit", required = false) Long excuteTimeLimit,
            @RequestParam(value = "excuteArgs", required = false) String excuteArgs) {
        try {
            if (StringUtil.isEmpty(javaSource)) {
                return ResultResponse.Build(ResultTypeEnum.error, "代码不能为空！");
            }

            if (StringUtil.isDangerous(javaSource)) {
                return ResultResponse.Build(ResultTypeEnum.error, "代码似乎在做什么哦！");
            }

            //如果大于10秒则设为10秒
            if (excuteTimeLimit != null && excuteTimeLimit >= 10000L) {
                return ResultResponse.Build(ResultTypeEnum.error, "超时时间不能大于10s！");
            }

            Class clazz = javaComplieService.complie(javaSource);
            String[] args = StringUtil.getInputArgs(excuteArgs);
            if (null == excuteTimeLimit && null == args) {
                //无参数 无时限
                return javaComplieService.excuteMainMethod(clazz);
            } else if (null == excuteTimeLimit) {
                //有参数 无时限
                return javaComplieService.excuteMainMethod(clazz, args);
            } else if (null == args) {
                //无参数 有时限
                if (excuteTimeLimit <= 0) {
                    return ResultResponse.Build(ResultTypeEnum.error, "超时时间不能小于1ms！");
                }
                return javaComplieService.excuteMainMethod(clazz, excuteTimeLimit);
            } else {
                //有参数 有时限
                if (excuteTimeLimit <= 0) {
                    return ResultResponse.Build(ResultTypeEnum.error, "超时时间不能小于1ms！");
                }
                return javaComplieService.excuteMainMethod(clazz, excuteTimeLimit, args);
            }
        } catch (CompileException e) {
            e.printStackTrace();
            return ResultResponse.Build(ResultTypeEnum.error, "编译错误！" + e.getMessage());
        } catch (RuntimeException e) {
            System.gc();
            return ResultResponse.Build(ResultTypeEnum.error, "运行错误！" + e.getMessage());
        } catch (Exception e) {
            System.gc();
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw, true));
            return ResultResponse.Build(ResultTypeEnum.error, "运行错误！" + e.getCause());
        }
    }

    /**
     * 主页
     * @return
     */
    @GetMapping(value = {"/", "/index"})
    public String index() {
        return "index";
    }
}
