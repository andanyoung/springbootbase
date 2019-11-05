package cn.andyoung.base.controller;

import cn.andyoung.base.common.beans.ResultBean;
import cn.andyoung.base.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Test {

  @Autowired private TestService testService = new TestService();

  @GetMapping("/add")
  public ResultBean<Boolean> add() {
    return new ResultBean<Boolean>(testService.add());
  }
}
