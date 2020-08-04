package ru.lanit.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class InterceptorRequest {

    @Around("execution(* ru.lanit.aspects.InterceptorRequest.*(..))")
    public Object request(ProceedingJoinPoint proceedingJoinPoint) {

        long startTime = System.currentTimeMillis();
        System.out.println("Starting " + proceedingJoinPoint.getSignature().getName());
        Object proceed = null;
        try {
            proceed = proceedingJoinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        System.out.println("Method execution completed");
        long endTime = System.currentTimeMillis() - startTime;
        System.out.println("Execution completion time is --  " + endTime + " ms");
        return proceed;
    }
}
