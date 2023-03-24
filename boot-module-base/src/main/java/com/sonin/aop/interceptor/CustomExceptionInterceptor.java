package com.sonin.aop.interceptor;

import com.sonin.aop.annotation.CustomExceptionAnno;
import com.sonin.core.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
/**
 * <pre>
 * 自定义异常处理逻辑类
 * </pre>
 * @author sonin
 * @version V0.1, 2022年4月13日 下午4:34:22
 */
@Aspect
@Service
@Slf4j
public class CustomExceptionInterceptor {
    /**
     * <pre>
     * 将所有注解@CustomExceptionAnno作为切入点
     * </pre>
     * @author Li Yuanyuan, 2022年4月13日 下午4:35:21
     * @Description: TODO(这里描述这个方法的需求变更情况)
     */
    @Pointcut("@annotation(com.sonin.aop.annotation.CustomExceptionAnno)")
    public void doPointcut() {
    }

    @Before("doPointcut()")
    public void doBefore(JoinPoint joinPoint) throws Throwable {
        log.info("=====start=====");
        // 打印描述信息
        String description = this.getAspectLogDescription(joinPoint);
        log.info("annotation description: {}", description);
        // 打印请求入参
        log.info("Request Args: {}", joinPoint.getArgs());
    }

    @Around("doPointcut()")
    public <T> Result<T> doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Result<T> result;
        try {
            Object object = proceedingJoinPoint.proceed();
            if (object instanceof Result) {
                result = (Result<T>) object;
            } else {
                result = new Result<>();
                result.setMessage("返回类型错误!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("请求异常: {}", e.getMessage());
            result = new Result<>();
            result.error500(e.getMessage());
        }
        return result;
    }

    @After("doPointcut()")
    public void doAfter() {
        log.info("=====End=====");
    }

    private String getAspectLogDescription(JoinPoint joinPoint) throws Exception {
        String targetName = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        Object[] arguments = joinPoint.getArgs();
        Class targetClass = Class.forName(targetName);
        Method[] methods = targetClass.getMethods();
        StringBuilder descStringBuilder = new StringBuilder();
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                Class[] clazz = method.getParameterTypes();
                if (clazz.length == arguments.length) {
                    descStringBuilder.append(method.getAnnotation(CustomExceptionAnno.class).description());
                    break;
                }
            }
        }
        return descStringBuilder.toString();
    }

}
