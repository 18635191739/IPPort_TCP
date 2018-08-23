package com.basic;

public class ErrorHandler {
    public static void DoWhenError(Exception ex,String str){
        String s = str == null? "出现错误":str;
        System.out.println(s);
        System.out.println("Errorlog:" + ex.toString());
        System.out.println("StackTrace:");
        ex.printStackTrace();

    }
}
