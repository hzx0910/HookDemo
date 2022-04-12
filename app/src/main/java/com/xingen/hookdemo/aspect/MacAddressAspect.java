package com.xingen.hookdemo.aspect;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public final class MacAddressAspect {
    public static final String TAG = "MacAddressAspect";

    /*** {@link com.xingen.hookdemo.util.PhoneInfos#getMacAddr() */
    @Pointcut("execution(* com.xingen.hookdemo.util.PhoneInfos.getMacAddr())")
    public final void point() {
    }

    /*** {@link com.xingen.hookdemo.util.PhoneInfos#getMacAddr1() */
    @Pointcut("execution(* com.xingen.hookdemo.util.PhoneInfos.getMacAddr1())")
    public final void point1() {
    }

    @Around("point() || point1()")
    public final Object aroundJoinPoint(ProceedingJoinPoint joinPoint) {
        System.out.println(TAG + " hook!");

        try {
            joinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            System.out.println(TAG + " ex " + throwable.getMessage());
        }
        return "hook_value";
    }
}
