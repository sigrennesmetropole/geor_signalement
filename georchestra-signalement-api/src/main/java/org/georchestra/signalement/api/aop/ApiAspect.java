package org.georchestra.signalement.api.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Aspect
@Component
public class ApiAspect {

    private static final Logger LOG = LoggerFactory.getLogger(ApiAspect.class);

    /**
     * Pour chaque entrée dans un controller
     */
    @Pointcut("execution(* org.georchestra.signalement.api.controller.*.*(..))")
    public void businessMethods() {
        // Laisser le contructeur vide
    }


    @Around("businessMethods()")
    public Object profile(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        Object output = pjp.proceed();
        long elapsedTime = System.currentTimeMillis() - start;
        LOG.info(String.format("%s - %s %s", elapsedTime, pjp.getSignature().getDeclaringTypeName(), pjp.getSignature().getName()));
        return output;
    }

}
