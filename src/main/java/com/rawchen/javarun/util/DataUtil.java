package com.rawchen.javarun.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author RawChen
 * @date 2023-08-02 16:28
 */
public class DataUtil {

	/**
	 * 文件后缀命名
	 */
	public static final String SUFFIX_FILE_TYPE = ".data";

	/**
	 * 文件新增文本
	 *
	 * @param formId
	 * @param fileName
	 * @return
	 */
	public static boolean setFormId(String formId, String fileName) {
		try {
			File file = DataUtil.getFileByFileName(fileName);
			FileWriter fw = new FileWriter(file, true);
			fw.append(formId);
			fw.append("\n");
			fw.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	/**
	 * 文件新增文本列表
	 *
	 * @param formIds
	 * @param fileName
	 * @return
	 */
	public static boolean setFormIds(List<String> formIds, String fileName) {
		try {
			File file = DataUtil.getFileByFileName(fileName);
			FileWriter fw = new FileWriter(file, true);
			if (formIds != null && formIds.size() > 0) {
				for (String formId : formIds) {
					fw.append(formId);
					fw.append("\n");
				}
			}
			fw.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	/**
	 * 文件中所有文本行
	 *
	 * @param fileName
	 * @return
	 */
	public static List<String> getIdsByFileName(String fileName) {
		List<String> list = new ArrayList<>();
		try {
			File file = DataUtil.getFileByFileName(fileName);
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			while ((line = br.readLine()) != null) {
				list.add(line);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return list;
	}

	/**
	 * 文件中所有文本行，去掉下划线后的字符
	 *
	 * @param fileName
	 * @return
	 */
	public static List<String> getIdsByFileNameFilterInstanceId(String fileName) {
		List<String> list = new ArrayList<>();
		try {
			File file = DataUtil.getFileByFileName(fileName);
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			while ((line = br.readLine()) != null) {
				int index = line.indexOf('_');
				if (index != -1) {
					line = line.substring(0, index);
				}
				list.add(line);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return list;
	}

	/**
	 * 文件中根据文本行内容匹配删除行
	 *
	 * @param formId
	 * @param fileName
	 * @return
	 */
	public static boolean removeByFileName(String formId, String fileName) {
		try {
			File file = DataUtil.getFileByFileName(fileName);
			BufferedReader br = new BufferedReader(new FileReader(file));
			StringBuilder sb = new StringBuilder();
			String line;

			while ((line = br.readLine()) != null) {
				if (!formId.equals(line)) {
					sb.append(line).append("\n");
				}
			}
			br.close();
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			bw.write(sb.toString());
			bw.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return true;

	}

	/**
	 * 根据文件名获取File，没有则创建
	 *
	 * @param fileName
	 * @return
	 */
	private static File getFileByFileName(String fileName) {
		try {
			File file = new File(System.getProperty("user.dir") + File.separator + fileName + SUFFIX_FILE_TYPE);
			if (!file.exists() || file.isDirectory()) {
				boolean result = file.createNewFile();
			}
			return file;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
