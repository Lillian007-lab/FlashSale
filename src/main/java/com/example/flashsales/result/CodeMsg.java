package com.example.flashsales.result;

public class CodeMsg {

    private int code;
    private String msg;

    public static  CodeMsg success = new CodeMsg(0, "success");
    public static  CodeMsg server_error = new CodeMsg(1, "server error");

    public CodeMsg(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
