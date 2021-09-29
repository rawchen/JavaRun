package com.rawchen.javarun.exception;

/**
 * @author RawChen
 * @date 2021/9/26 22:33
 * @desc 编译异常
 */
public class CompileException extends RuntimeException {
	public CompileException(String message) {
		super(message);
	}
}
