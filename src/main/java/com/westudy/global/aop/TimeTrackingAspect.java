package com.westudy.global.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * {@link TrackTime} 어노테이션이 부여된 메서드의 실행 성능(시간)을 측정하는 Aspect입니다.
 */
@Aspect
@Component
@Slf4j
public class TimeTrackingAspect {

    @Around("@annotation(com.westudy.global.aop.TrackTime)")
    public Object trackTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - start;
            log.info("[Performance] {} execution time: {}ms", joinPoint.getSignature().toShortString(), executionTime);
            return result;
        } catch (Throwable throwable) {
            long executionTime = System.currentTimeMillis() - start;
            log.warn("[Performance Exception] {} failed in {}ms | Error: {}",
                    joinPoint.getSignature().toShortString(), executionTime, throwable.getMessage());
            throw throwable;
        }
    }
}
