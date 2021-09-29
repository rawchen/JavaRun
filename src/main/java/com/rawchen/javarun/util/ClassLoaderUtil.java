package com.rawchen.javarun.util;


import com.rawchen.javarun.config.Constants;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;

/**
 * @author RawChen
 * @date 2021/9/26 14:21
 * @desc 自定义classLoader, 用来加载动态编译好的CLASS文件
 */
public class ClassLoaderUtil extends ClassLoader {
	private final String path = Constants.classPath;

	public ClassLoaderUtil(ClassLoader parent) {
		super(parent);
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		//这个classLoader的主要方法
		String classPath = name.replace(".", "\\") + ".class";//将包转为目录
		String classFile = path + classPath;//拼接完整的目录
		Class clazz = null;
		try {
			byte[] data = getClassFileBytes(classFile);
			clazz = defineClass(name, data, 0, data.length);
			if (null == clazz) {//如果在这个类加载器中都不能找到这个类的话，就真的找不到了
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return clazz;
	}

	private byte[] getClassFileBytes(String classFile) throws Exception {
		//采用NIO读取
		FileInputStream fis = new FileInputStream(classFile);
		FileChannel fileC = fis.getChannel();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		WritableByteChannel outC = Channels.newChannel(baos);
		ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
		while (true) {
			int i = fileC.read(buffer);
			if (i == 0 || i == -1) {
				break;
			}
			buffer.flip();
			outC.write(buffer);
			buffer.clear();
		}
		fis.close();
		return baos.toByteArray();
	}
}
