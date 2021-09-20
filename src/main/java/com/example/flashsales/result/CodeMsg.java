package com.example.flashsales.result;

import java.text.spi.CollatorProvider;

public class CodeMsg {

    private int code;
    private String msg;

    // General code
    public static  CodeMsg SUCCESS = new CodeMsg(0, "success");
    public static  CodeMsg SERVER_ERROR = new CodeMsg(1, "server error");

    // Login code
    public static CodeMsg SESSION_ERROR = new CodeMsg(500210, "Session does not exist or is expired");
    public static CodeMsg PASSWORD_EMPTY = new CodeMsg(500211, "Password cannot be empty");
    public static CodeMsg MOBILE_EMPTY = new CodeMsg(500212, "Mobile number cannot be empty");
    public static CodeMsg MOBILE_ERROR = new CodeMsg(500213, "Mobile number format is incorrect");
    public static CodeMsg MOBILE_NOT_EXIST  = new CodeMsg(500214, "Mobile number does not exist");
    public static CodeMsg PASSWORD_ERROR  = new CodeMsg(500214, "Password is not correct");


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
