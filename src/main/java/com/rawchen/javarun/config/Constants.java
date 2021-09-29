package com.rawchen.javarun.config;

import java.io.File;

/**
 * @author RawChen
 * @date 2021/9/26 22:33
 * @desc 配置常量
 */
public class Constants {

	private Constants() {
	}

	public static final String className = "Main";
	public static final String classPath = System.getProperty("user.dir") + File.separator + "src" + File.separator;
	public static final String executeMainMethodName = "main";
}
