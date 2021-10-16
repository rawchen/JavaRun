package com.rawchen.javarun.vo;

import com.rawchen.javarun.enums.ResultTypeEnum;

import java.io.Serializable;

/**
 * @author RawChen
 * @date 2021/9/26 22:33
 * @desc 响应结果
 */
public class ResultResponse implements Serializable {
	//执行结果
	private String executeResult;
	//状态类型
	private ResultTypeEnum resultTypeEnum;
	//执行时间
	private Long executeDurationTime = -1L;

	private String message;

	public String getExecuteResult() {
		return executeResult;
	}


	public static ResultResponse Build(ResultTypeEnum resultTypeEnum) {
		ResultResponse resultResponse = new ResultResponse();
		resultResponse.setResultTypeEnum(resultTypeEnum);
		return resultResponse;
	}

	public static ResultResponse Build(ResultTypeEnum resultTypeEnum, String message) {
		ResultResponse resultResponse = Build(resultTypeEnum);
		resultResponse.setMessage(message);
		return resultResponse;
	}


	public void setExecuteResult(String executeResult) {
		this.executeResult = executeResult;
	}

	public Integer getResultTypeEnum() {
		return resultTypeEnum.getTypeCode();
	}

	public void setResultTypeEnum(ResultTypeEnum resultTypeEnum) {
		this.resultTypeEnum = resultTypeEnum;
	}

	public Long getExecuteDurationTime() {
		return executeDurationTime;
	}

	public void setExecuteDurationTime(Long executeDurationTime) {
		this.executeDurationTime = executeDurationTime;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "ResultResponse{" +
				"executeResult='" + executeResult + '\'' +
				", resultTypeEnum=" + resultTypeEnum +
				", executeDurationTime=" + executeDurationTime +
				", message='" + message + '\'' +
				'}';
	}
}
