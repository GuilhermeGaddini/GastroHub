package com.fiap.GastroHub.shared.infra.beans;

import com.fiap.GastroHub.shared.infra.http.filters.RequestContext;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.logging.LogLevel;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Aspect
@Component
@Log4j2
public class LogAspect {
    private final RequestContext requestContext;

    public LogAspect(RequestContext requestContext) {
        this.requestContext = requestContext;
    }

    @Around("@annotation(LogBean)")
    public Object log(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        LogBean logBean = signature.getMethod().getAnnotation(LogBean.class);

        Map<String, Object> args = new LinkedHashMap<>();
        String[] names = signature.getParameterNames();
        for (int i = 0; i < names.length; i++) {
            args.put(names[i], joinPoint.getArgs()[i]);
        }

        if (logBean.level() == LogLevel.INFO) {
            log.info("[{} : {}][START - {}] PARAMS: {}", joinPoint.getTarget().getClass().getCanonicalName(),
                    joinPoint.getSignature().getName(), requestContext.getRequestId(), args);
        } else if (logBean.level() == LogLevel.DEBUG) {
            log.debug("[{} : {}][START - {}] PARAMS: {}", joinPoint.getTarget().getClass().getCanonicalName(),
                    joinPoint.getSignature().getName(), requestContext.getRequestId(), args);
        }

        Object proceed = joinPoint.proceed();

        if (logBean.level() == LogLevel.INFO) {
            log.info("[{} : {}][END - {}]", joinPoint.getTarget().getClass().getCanonicalName(),
                    joinPoint.getSignature().getName(), requestContext.getRequestId());
        } else if (logBean.level() == LogLevel.DEBUG) {
            log.debug("[{} : {}][END - {}]", joinPoint.getTarget().getClass().getCanonicalName(),
            joinPoint.getSignature().getName(), requestContext.getRequestId());
        }

        return proceed;
    }

    @AfterThrowing(value = "@annotation(LogBean)", throwing = "ex")
    public void logError(JoinPoint joinPoint, Exception ex) {
        log.error("[{} : {}][{} ERROR - {}]", joinPoint.getTarget().getClass().getCanonicalName(),
        joinPoint.getSignature().getName(), requestContext.getRequestId(), ex.getMessage());
    }
}
