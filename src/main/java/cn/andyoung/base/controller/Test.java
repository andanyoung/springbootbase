package cn.andyoung.base.controller;

import cn.andyoung.base.common.annotation.AccessLimit;
import cn.andyoung.base.common.beans.ResultBean;
import cn.andyoung.base.service.TestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Test {
  private static final Logger logger = LoggerFactory.getLogger(Test.class);
  @Autowired private TestService testService = new TestService();

  @AccessLimit(seconds = 1, maxCount = 2)
  @GetMapping("/add")
  public ResultBean<Boolean> add() {
    logger.info("add");
    return new ResultBean<Boolean>(testService.add());
  }
}
