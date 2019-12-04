package cn.andyoung.base.common.EnumType;

public enum ResultCode {
  SUCCESS(10000),
  SYSERROR(90000),
  ACCESS_LIMIT_REACHED(91000);

  private int code;

  ResultCode(int code) {
    this.code = code;
  }
}
