package ir.co.sadad.departuretaxapi.aspects;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LoggingAspect {

    @Before("execution(* ir.co.sadad.departuretaxapi.services.providers.*.*(..))")
    public void beforeAllServiceMethods(JoinPoint joinPoint) {
        log.warn("********** started executing: " + joinPoint.getSignature().getName() +
                " with method param: " + Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning(pointcut = "execution(* ir.co.sadad.departuretaxapi.services.providers..*(..))", returning = "result")
    public void afterAllServiceMethods(JoinPoint joinPoint, Object result) {
        log.warn("********** completed executing: " + joinPoint.getSignature().getName() +
                " with return value: " + result);
    }

    @AfterThrowing(pointcut = "execution(* ir.co.sadad.departuretaxapi.services.providers..*(..))", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        log.error("Exception in {}.{}() with cause = {}", joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(), e.getMessage() != null ? e.getMessage() : "NULL");
    }
}