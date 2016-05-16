package com.safeway.app.emju.mylist.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorVO {

	private String msg;
	private String code;
	
	public ErrorVO(String errorCode, String errorMsg) {
		msg = errorMsg;
		code = errorCode;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	@Override
	public String toString() {

		return new StringBuffer("{\"msg\":").append(msg).append(",\"code\":")
				.append(code).append("}").toString();
	}
}
