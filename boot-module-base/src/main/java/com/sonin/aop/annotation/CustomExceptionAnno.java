package com.sonin.aop.annotation;

import java.lang.annotation.*;

/**
 * @author sonin
 * @date 2021/9/23 19:08
 * <pre>
 * 对controller层统一实现try...catch...拦截
 * </pre>
 * @author sonin
 * @version V0.1, 2022年4月13日 下午4:33:21
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface CustomExceptionAnno {

	/**
	 * <pre>
	 * 异常描述
	 * </pre>
	 * @return
	 * @author sonin, 2022年4月13日 下午4:33:42
	 * @Description: TODO(这里描述这个方法的需求变更情况)
	 */
	String description() default "";

}

