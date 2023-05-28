package world.meta.sns.api.config.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class SnsAspect {

    @Around("execution(* world.meta.sns.controller..*.*(..))")
    public Object doLog(ProceedingJoinPoint joinPoint) {
        long start = System.currentTimeMillis();

        Object result;

        try {
            log.info("[@Controller Signature] = {}", joinPoint.getSignature());
            result = joinPoint.proceed();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

        long end = System.currentTimeMillis();
        log.info("[경과 시간] {} millis", end - start);

        return result;
    }
}
