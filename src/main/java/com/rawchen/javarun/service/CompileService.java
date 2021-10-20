package com.rawchen.javarun.service;

import com.rawchen.javarun.vo.ResultResponse;

/**
 * @author RawChen
 * @date 2021/9/26 22:33
 * @desc JAVA编译器service接口
 */
public interface CompileService {

	/**
	 * 编译，并获取编译后的class类
	 *
	 * @param javaSource JAVA代码
	 */
	void compile(String javaSource) throws Exception;

	/**
	 * 执行MAIN方法
	 *
	 * @param clazz 编译后的CLASS
	 * @return 执行结果
	 */
	ResultResponse excuteMainMethod(Class clazz) throws Exception;

	/**
	 * 执行MAIN方法
	 *
	 * @param clazz 编译后的CLASS
	 * @param args  运行参数数组
	 * @return 执行结果
	 */
	ResultResponse excuteMainMethod(Class clazz, String[] args) throws Exception;

	/**
	 * 执行MAIN方法
	 *
	 * @param timeLimit 时间限制
	 */
	ResultResponse excuteMainMethod(Class clazz, Long timeLimit) throws Exception;

	/**
	 * 执行MAIN方法
	 *
	 * @param timeLimit 时间限制
	 * @param args      运行参数数组
	 */
	ResultResponse excuteMainMethod(Class clazz, Long timeLimit, String[] args) throws Exception;

	/**
	 * 运行Main.class
	 *
	 * @return
	 */
	ResultResponse run(long executeTimeLimit, String executeArgs) throws Exception;

}
