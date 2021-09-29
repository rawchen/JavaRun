package com.rawchen.javarun.util;

/**
 * @author RawChen
 * @date 2021/9/26 14:21
 */
public class StringUtil {

	/**
	 * 非空判断
	 *
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str) {
		return (str == null || "".equals(str));
	}

	/**
	 * 传入代码是否安全
	 *
	 * @param javaSource
	 * @return
	 */
	public static boolean isDangerous(String javaSource) {
		return javaSource.contains("exec(") &&
				javaSource.contains("Runtime") &&
				javaSource.contains("Process");
	}

	/**
	 * 参数格式化为字符数组
	 *
	 * @param excuteArgsStr
	 * @return
	 */
	public static String[] getInputArgs(String excuteArgsStr) {
		if (StringUtil.isEmpty(excuteArgsStr)) {
			return null;
		} else {
			return excuteArgsStr.trim().split("\\s+");
		}
	}
}
