package cn.andyoung.base.common.aop;

import cn.andyoung.base.common.beans.ResultBean;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/** 处理和包装异常 */
@Aspect
@Component
public class ControllerAOP {

  private static final Logger logger = LoggerFactory.getLogger(ControllerAOP.class);

  // 切入点
  @Pointcut("execution(public * cn.andyoung.base.controller.*.*(..))")
  private void pointcut() {}

  // 记录处理时间和异常
  @Around("pointcut()")
  public Object handlerControllerMethod(ProceedingJoinPoint pjp) {
    long startTime = System.currentTimeMillis();

    ResultBean<?> result;

    try {
      result = (ResultBean<?>) pjp.proceed();
      logger.info(
          pjp.getSignature() + "use time:" + (System.currentTimeMillis() - startTime) + "ms");
    } catch (Throwable e) {
      result = handlerException(pjp, e);
    }

    return result;
  }

  /** 封装异常信息，注意区分已知异常（自己抛出的）和未知异常 */
  private ResultBean<?> handlerException(ProceedingJoinPoint pjp, Throwable e) {
    ResultBean<?> result = new ResultBean();

    // 已知异常 -- 由应用层字
    if (e instanceof Exception) {
      result.setMsg(e.getLocalizedMessage());
      result.setCode(ResultBean.FAIL);
    }
    //    else if (e instanceof UnloginException) {
    //      result.setMsg("Unlogin");
    //      result.setCode(ResultBean.NO_LOGIN);
    //    }
    else {
      logger.error(pjp.getSignature() + " error ", e);
      // TODO 未知的异常，应该格外注意，可以发送邮件通知等
      result.setMsg(e.toString());
      result.setCode(ResultBean.FAIL);
    }

    return result;
  }
}
