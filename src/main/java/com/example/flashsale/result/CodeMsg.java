package com.example.flashsale.result;

public class CodeMsg {

    private int code;
    private String msg;

    // General code: 5001xx
    public static  CodeMsg SUCCESS = new CodeMsg(200, "success");
    public static  CodeMsg SERVER_ERROR = new CodeMsg(500100, "server error");
    public static  CodeMsg BIND_ERROR = new CodeMsg(500101, "Parameter error: %$");
    public static  CodeMsg REQUEST_ILLEGAL = new CodeMsg(500102, "Illegal request");
    public static  CodeMsg ACCESS_LIMIT_REACHED = new CodeMsg(500103, "Too many visits!");


    // Login code: 5002xx
    public static CodeMsg SESSION_ERROR = new CodeMsg(500200, "Session does not exist or is expired");
    public static CodeMsg PASSWORD_EMPTY = new CodeMsg(500201, "Password cannot be empty");
    public static CodeMsg MOBILE_EMPTY = new CodeMsg(500202, "Mobile number cannot be empty");
    public static CodeMsg MOBILE_ERROR = new CodeMsg(500203, "Mobile number format is incorrect");
    public static CodeMsg MOBILE_NOT_EXIST  = new CodeMsg(500204, "Mobile number does not exist");
    public static CodeMsg PASSWORD_ERROR  = new CodeMsg(500205, "Password is not correct");

    // Order code: 5003xx
    public static final CodeMsg ORDER_NOT_EXIT = new CodeMsg(500301, "Order does not exit");;


    // Flash Sale code: 5005xx
    public static CodeMsg FLASH_SALE_OUT_OF_STOCK  = new CodeMsg(500500, "Product is out of stock");
    public static CodeMsg FLASH_SALE_REPEAT  = new CodeMsg(500501, "Can't buy a same flash sale product multiple times");
    public static CodeMsg FLASH_SALE_FAILED  = new CodeMsg(500502, "Flash Sale Failed");


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

    public CodeMsg fillArgs(Object... args){
        int code = this.code;
        String message = String.format(this.msg, args);
        return new CodeMsg(code, message);
    }

}
