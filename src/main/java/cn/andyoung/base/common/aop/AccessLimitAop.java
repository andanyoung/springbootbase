package cn.andyoung.base.common.aop;

import cn.andyoung.base.common.annotation.AccessLimit;
import cn.andyoung.base.common.beans.ResultBean;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class AccessLimitAop {
  private static final Logger logger = LoggerFactory.getLogger(AccessLimitAop.class);
  private static final String RedisKEY_AccessLimit = "map_AccessLimit";
  @Autowired private ValueOperations valueOperation;

  // 切入点
  @Pointcut("execution(public * cn.andyoung.base.controller.*.*(..))")
  private void pointcut() {}

  @Around("pointcut()")
  public Object handlerControllerMethod(ProceedingJoinPoint pjp) throws Throwable {
    String className = pjp.getTarget().getClass().getSimpleName();
    String methodName = pjp.getSignature().getName();
    Object[] args = pjp.getArgs();
    Class<?> classTarget = pjp.getTarget().getClass();
    Class<?>[] par = ((MethodSignature) pjp.getSignature()).getParameterTypes();
    Method objMethod = classTarget.getMethod(methodName, par);
    logger.info(objMethod.toString());
    if (noAccessLimit(objMethod)) {
      return pjp.proceed();
    } else {

      // 超出访问次数,抛出错误
      RuntimeException re = new RuntimeException();
      ResultBean<?> result = new ResultBean();
      result.setMsg("访问太频繁");
      result.setCode(ResultBean.ACCESS_LIMIT_REACHED);
      return result;
    }
  }

  // ** 处理接口访问限制
  private boolean noAccessLimit(Method objMethod) {

    // 获取方法中的注解,看是否有该注解
    AccessLimit accessLimit = objMethod.getAnnotation(AccessLimit.class);
    System.out.println(accessLimit);
    if (accessLimit == null) {
      return true;
    }
    int seconds = accessLimit.seconds();
    int maxCount = accessLimit.maxCount();
    boolean login = accessLimit.needLogin();
    HttpServletRequest request =
        ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    String key = RedisKEY_AccessLimit + "::" + request.getRequestURI();
    // 如果需要登录
    if (login) {
      // 获取登录的session进行判断
      // .....
      // 这里假设用户是1,项目中是动态获取的userId
      key += "::" + "12";
    }
    Integer count = (Integer) valueOperation.get(key);
    // 从redis中获取用户访问的次数
    if (count == null) {
      // 第一次访问
      valueOperation.set(key, 1, seconds * 60, TimeUnit.SECONDS);
    } else if (count < maxCount) {
      // 加1
      valueOperation.increment(key, 1);
    } else {
      return false;
    }

    return true;
  }
}
