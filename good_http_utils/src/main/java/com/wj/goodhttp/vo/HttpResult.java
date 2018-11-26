package com.wj.goodhttp.vo;

/**
 * 比如请求不成功，可能返回 {"ok":false, "errorcode":"usercode_invalid", "errormsg":"用户无效"}
 *                       {"ok":false, "errorcode":"usercode_invalid"}
 * 请求成功，返回  {"ok":false, "data":...}
 */
public class HttpResult<T> {
    private boolean ok;
    private String errorcode;
    private String errormsg;
    private T data;

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public String getErrormsg() {
        return errormsg;
    }

    public void setErrormsg(String errormsg) {
        this.errormsg = errormsg;
    }

    public String getErrorcode() {
        return errorcode;
    }

    public void setErrorcode(String errorcode) {
        this.errorcode = errorcode;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}
