package com.sonin.core.annotation;

import java.lang.annotation.*;

/**
 * @author sonin
 * @date 2021/10/3 13:04
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface BeanAnno {

    String targetFieldName() default "";

    String[] targetFieldNames() default {};

}
