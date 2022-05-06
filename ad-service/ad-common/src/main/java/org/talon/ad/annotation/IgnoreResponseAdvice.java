package org.talon.ad.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})   // this annot can be applied to class or method
@Retention(RetentionPolicy.RUNTIME)   // effective only during runtime
public @interface IgnoreResponseAdvice {

}
