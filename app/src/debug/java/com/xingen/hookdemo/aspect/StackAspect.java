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
 *
 * @see android.os.Handler#sendEmptyMessage(int)
 * @see android.os.Handler#post(Runnable)
 * @see java.util.concurrent.Executor#execute(Runnable)
 * <p>
 * StartActivity(Intent) -> Activity.onCreate()
 * <p>
 * Handler.sendEmptyMessage(int)｜Handler.sendMessage(msg) -> Handler.handleMessage
 * Handler.sendEmptyMessageAtTime(int,long)｜Handler.sendMessageAtTime(msg,long) -> Handler.handleMessage
 * Handler.sendEmptyMessageDelayed(int,long)｜Handler.sendMessageDelayed(msg,long) -> Handler.handleMessage
 * <p>
 * Handler.post(Runnable) ->  -> Runnable.run()
 * new Thread(Runnable) -> Runnable.run()
 * java.util.concurrent.Executor.execute(Runnable)  -> Runnable.run()
 */
@Aspect
public final class StackAspect {
    public static final String TAG = "StackAspect";

    /**
     * 采集过程
     **/
    public static ConcurrentHashMap<Message, StackTraceElement[]> msgMap = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<Runnable, StackTraceElement[]> runnableMap = new ConcurrentHashMap<>();

    /**
     * 结果缓存
     **/
    public static ThreadLocal<StackTraceElement[]> stacks = new ThreadLocal<>();


    @Around("call(* android.os.Handler.post(..))")
    public final Object aroundPost(ProceedingJoinPoint joinPoint) throws Throwable {
        Log.v(TAG, " hook Post ! " + joinPoint.getThis().getClass().getName());
        runnableMap.put((Runnable) joinPoint.getArgs()[0], Thread.currentThread().getStackTrace());
        return joinPoint.proceed(proxyRunnable(joinPoint.getArgs()));
    }

    @Around("call(* java.util.concurrent.Executor.execute(..))")
    public final Object aroundExecute(ProceedingJoinPoint joinPoint) throws Throwable {
        Log.v(TAG, " hook Execute ! " + joinPoint.getThis().getClass().getName());
        runnableMap.put((Runnable) joinPoint.getArgs()[0], Thread.currentThread().getStackTrace());
        return joinPoint.proceed(proxyRunnable(joinPoint.getArgs()));
    }

    private final Object[] proxyRunnable(Object[] args) {
        if (args == null) return null;
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof Runnable) {
                Runnable runnable = (Runnable) args[i];
                RunnableHandler handler = new RunnableHandler(runnable);
                ClassLoader loader = runnable.getClass().getClassLoader();
                Class<?>[] interfaces = runnable.getClass().getInterfaces();
                args[i] = Proxy.newProxyInstance(loader, interfaces, handler);
            }
        }
        return args;
    }

    private static class RunnableHandler implements InvocationHandler {
        private final Runnable run;

        public RunnableHandler(Runnable run) {
            this.run = run;
        }

        public void before() {
            Log.v(TAG, " before run ! " + run);
            stacks.set(runnableMap.get(run));
            runnableMap.remove(run);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            before();
            Object obj = method.invoke(run, args);
            after();
            return obj;
        }

        public void after() {
            Log.v(TAG, " after run ! " + run);
            stacks.set(null);
        }
    }

    @Keep
    public static void printSource() {
        if (stacks.get() != null)
            Log.v(TAG, printStackTrace(false, Thread.currentThread().getStackTrace()) +
                    printStackTrace(true, stacks.get()));

        else Log.v(TAG, "Source not found", new Exception());
    }

    private static String printStackTrace(boolean previous, StackTraceElement[] trace) {
        StringBuilder builder = new StringBuilder(previous ? " \nPrevious Thread" : " \nCurrent Thread");
        for (int i = 0; i < trace.length; i++) {
            if (i == 0 || i == 1) continue;
            if (i == 2 && previous) continue;
            builder.append("\nat " + trace[i]);
        }
        return builder.toString();
    }
}
