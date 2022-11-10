package com.xingen.hookdemo.aspect;

import android.util.Log;
import android.view.View;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * 验证lambda表达式下
 * joinPoint.getThis()
 * joinPoint.getTarget()
 * 都是null
 */
@Aspect
public class ViewClickAspect {

    private static final String TAG = "ViewClickAspect";


    @Around("execution(void com..lambda*(android.view.View))")
    public void onViewClicked(ProceedingJoinPoint joinPoint) {
        try {
            Object[] args = joinPoint.getArgs();
            View view = getViewFromArgs(args);
            if (view == null) {
                joinPoint.proceed();
                return;
            }
            Log.d(TAG, "listener: " + joinPoint.getThis());
            joinPoint.proceed();

        } catch (Throwable e) {
            Log.v(TAG, "e ", e);
        }
    }

    private View getViewFromArgs(Object[] args) {
        if (args != null && args.length > 0) {
            Object arg = args[0];
            if (arg instanceof View) {
                return (View) arg;
            }
        }
        return null;
    }
}
