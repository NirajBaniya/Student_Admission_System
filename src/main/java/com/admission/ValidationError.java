package com.admission;

public class ValidationError {

	private boolean hasError = false;
	private String msg;
	
	public ValidationError(String errorMessage) {
		this.msg = errorMessage;
		this.hasError = true;
	}
	
	public ValidationError() {
	}
	
	public boolean getHasError() {
		return hasError;
	}
	public void setHasError(boolean hasError) {
		this.hasError = hasError;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
	
}
