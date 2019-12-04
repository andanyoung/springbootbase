package cn.andyoung.base.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AccessLimit {

  // 几秒内
  int seconds();
  // seconds秒内最大的访问次数
  int maxCount();

  // 需不需要登录
  boolean needLogin() default true;
}
