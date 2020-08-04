package ru.lanit;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class RespectAspect {

    @Around("execution(@ru.lanit.aspects.RequestInterceptor * *(..))")
    public Object aroundAspect(ProceedingJoinPoint proceedingJoinPoint) throws Throwable{
        long startTime = System.currentTimeMillis();
        System.out.println("Starting " + proceedingJoinPoint.getSignature().getName());

        proceedingJoinPoint.getArgs();
        proceedingJoinPoint.getSignature().getDeclaringTypeName();
        Object proceed = proceedingJoinPoint.proceed();

        System.out.println("Method execution completed");
        long endTime =  System.currentTimeMillis() - startTime;
        System.out.println("Execution completion time is --  " + endTime+" ms");
        return proceed;
    }
}