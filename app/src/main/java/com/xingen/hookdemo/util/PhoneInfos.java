package com.xingen.hookdemo.util;

/**
 * @author HeZX on 2022/4/12.
 */
public class PhoneInfos {

    public static String mac0() {
        return getMacAddr();
    }

    private static String getMacAddr() {
        return "02:00:00:00:00:00";
    }

    public static String mac1() {
        return getMacAddr1();
    }

    private static String getMacAddr1() {
        return "02:00:00:00:00:01";
    }
}
