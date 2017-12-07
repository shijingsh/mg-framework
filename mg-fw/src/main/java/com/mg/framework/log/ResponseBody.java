package com.mg.framework.log;

/**
 * 封装接口相应对象
 */
public class ResponseBody<T> {
	public static int SUCCESS = 0;
	public static int ERROR = 1;
	//错误代码
	private int errorCode;
	//错误提示
    private String errorText;
    
    //成功时的提示
    private String successText;

    //返回对象
    private T data;

    public ResponseBody() {
    }

    public ResponseBody(int errorCode, String errorText, T data){
    	this.errorCode = errorCode;
    	this.errorText = errorText;
    	this.data = data;
    }
    
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorText() {
		return errorText;
	}
	public void setErrorText(String errorText) {
		this.errorText = errorText;
	}

	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}

    public String getSuccessText() {
		return successText;
	}

	public void setSuccessText(String successText) {
		this.successText = successText;
	}

	@Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ResponseBody{");
        sb.append("errorCode=").append(errorCode);
        sb.append(", errorText='").append(errorText).append('\'');
        sb.append(", data=").append(data);
        sb.append('}');
        return sb.toString();
    }
}
