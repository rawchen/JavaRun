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
	 * 传入代码是否危险
	 *
	 * @param javaSource
	 * @return
	 */
	public static boolean isDangerousWithLinux(String javaSource) {
		javaSource = javaSource.trim();
		if (!StringUtil.isWinOs()) {
			return javaSource.contains("importjava.nio")
					|| javaSource.contains("importjava.io")
					|| javaSource.contains("java.io.File")
					|| javaSource.contains("java.nio.file.Files")
					|| javaSource.contains("Class.forName")
					|| javaSource.contains("System.getenv()")
					|| (javaSource.contains(".exec("));
		} else {
			return false;
		}
	}

	/**
	 * 参数格式化为字符数组
	 *
	 * @param executeArgsStr
	 * @return
	 */
	public static String[] getInputArgs(String executeArgsStr) {
		if (StringUtil.isEmpty(executeArgsStr)) {
			return null;
		} else {
			return executeArgsStr.trim().split("\\s+");
		}
	}

	/**
	 * 合并两数组
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	public static String[] concat(String[] a, String[] b) {
		String[] c= new String[a.length+b.length];
		System.arraycopy(a, 0, c, 0, a.length);
		System.arraycopy(b, 0, c, a.length, b.length);
		return c;
	}

	/**
	 * 是Win系统，不然就是linux
	 *
	 * @return
	 */
	public static boolean isWinOs() {
		String os = System.getProperty("os.name");
		return os.toLowerCase().startsWith("win");
	}
}
