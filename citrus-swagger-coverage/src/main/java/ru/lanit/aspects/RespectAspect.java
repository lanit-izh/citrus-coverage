package ru.lanit.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

@Aspect
public class RespectAspect {

    @Before("execution(@ru.lanit.aspects.RequestInterceptor * *(..))")
    public Object before(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        return null;
    }

    @Around("execution(@ru.lanit.aspects.RequestInterceptor * *(..))")
    public Object aroundAspect(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        System.out.println("Starting " + proceedingJoinPoint.getSignature().getName());
        proceedingJoinPoint.getSignature().getDeclaringTypeName();
        Object[] objects = proceedingJoinPoint.getArgs();
        proceedingJoinPoint.getSignature().getDeclaringTypeName();
        Object proceed = proceedingJoinPoint.proceed();
        long endTime = System.currentTimeMillis() - startTime;
        return proceed;
    }
}