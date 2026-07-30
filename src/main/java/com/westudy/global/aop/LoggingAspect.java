package com.westudy.global.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * 컨트롤러(Controller / RestController) 진입 시의 요청과 응답을 기록하는 Aspect입니다.
 */
@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *) || within(@org.springframework.stereotype.Controller *)")
    public void controllerPointcut() {}

    @Around("controllerPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = null;
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            request = attributes.getRequest();
        }

        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String args = Arrays.toString(joinPoint.getArgs());

        if (request != null) {
            log.info("[HTTP Request] {} {} | Controller: {}.{}() | Args: {}",
                    request.getMethod(), request.getRequestURI(), className, methodName, args);
        } else {
            log.info("[Request] {}.{}() | Args: {}", className, methodName, args);
        }

        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - start;
            log.info("[HTTP Response] {}.{}() took {}ms | Result: {}", className, methodName, executionTime, result);
            return result;
        } catch (Throwable throwable) {
            long executionTime = System.currentTimeMillis() - start;
            log.error("[HTTP Exception] {}.{}() failed in {}ms | Error: {}",
                    className, methodName, executionTime, throwable.getMessage());
            throw throwable;
        }
    }
}
