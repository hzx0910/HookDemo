package com.xingen.hookdemo.aspect;


import android.os.Message;
import android.support.annotation.Keep;
import android.util.Log;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 跨线程方法栈回溯
 * <p>
 * 占位空方法
 */
public final class StackAspect {

    @Keep
    public static void printSource() {

    }
}
