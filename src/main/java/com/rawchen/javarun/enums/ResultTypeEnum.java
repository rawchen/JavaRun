package com.rawchen.javarun.enums;


/**
 * @author RawChen
 * @date 2021/9/26 22:33
 * @desc 请求结果ENUM
 */
public enum ResultTypeEnum {
	ok(200), fail(400), error(500);
	private Integer typeCode;

	ResultTypeEnum(Integer code) {
		this.typeCode = code;
	}

	public Integer getTypeCode() {
		return typeCode;
	}
}
