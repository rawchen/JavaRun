package com.rawchen.javarun.util;

import java.io.File;

/**
 * @author RawChen
 * @since 2021-10-19 11:00
 */
public class FileUtil {

	public static boolean deleteClassFileForDir(String path) {
		File file = new File(path);
		if (!file.exists()) {//判断是否待删除目录是否存在
//			System.err.println("目录不存在！");
			return false;
		}

		String[] content = file.list();//取得当前目录下所有文件和文件夹
		for (String name : content) {
			File temp = new File(path, name);
			if (!temp.isDirectory()) {//判断是否是目录

				if (name.endsWith(".class")) {
					if (!temp.delete()) {//直接删除文件
//						System.err.println("文件删除失败：" + name);
					}
				} else {
//					System.out.println("文件不用被删除：" + name);
				}
			} else {//是目录
//				deleteClassFileForDir(temp.getAbsolutePath());//递归调用，删除目录里的内容
//				temp.delete();//删除空目录
			}
		}
		return true;
	}
}
