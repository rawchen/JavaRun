package com.rawchen.javarun.config;

import java.io.File;

/**
 * @author RawChen
 * @date 2021/9/26 22:33
 * @desc 配置常量
 */
public class Constants {

	public final static String CLASS_NAME = "Main";
	public final static String CLASS_PATH = System.getProperty("user.dir") + File.separator + "src" + File.separator;
	public final static String MEM_ARGS = "-Xmx200m";
	public final static String EXECUTE_MAIN_METHOD_NAME = "main";

	public Constants() {
	}
}
