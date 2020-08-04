package ru.lanit.aspects;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

@Aspect
public class RespectAspect {


//    @Pointcut("execution(@ru.lanit.aspects.RequestInterceptor * *(..))")
//    public void aspect(ProceedingJoinPoint proceedingJoinPoint) throws Throwable{
//        long startTime = System.currentTimeMillis();
//        System.out.println("Starting " + proceedingJoinPoint.getSignature().getName());
//
//        proceedingJoinPoint.getArgs();
//        proceedingJoinPoint.getSignature().getDeclaringTypeName();
//        Object proceed = proceedingJoinPoint.proceed();
//
//        System.out.println("Method execution completed");
//        long endTime =  System.currentTimeMillis() - startTime;
//        System.out.println("Execution completion time is --  " + endTime+" ms");
//    }

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
        Object o1 = objects[0];




        proceedingJoinPoint.getSignature().getDeclaringTypeName();
        Object proceed = proceedingJoinPoint.proceed();

        System.out.println("Method execution completed");
        long endTime = System.currentTimeMillis() - startTime;
        System.out.println("Execution completion time is --  " + endTime + " ms");
        return proceed;
    }
}