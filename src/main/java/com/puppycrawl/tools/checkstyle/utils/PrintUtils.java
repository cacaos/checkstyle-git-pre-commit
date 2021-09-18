package com.puppycrawl.tools.checkstyle.utils;

/**
 * @author Ming
 */
public class PrintUtils {


    public static void info(String str) {
        String pre = "[INFO-CHECKSTYLE-INIT] ";
        System.out.println(pre + str);
    }

    public static void err(String str) {
        String pre = "[ERROR-CHECKSTYLE-INIT] ";
        System.err.println(pre + str);
    }
}
