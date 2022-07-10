package com.atguigu.yygh.common.result;

import io.swagger.models.auth.In;

public interface ResultCode {
    
    Integer SUCCESS = 20000;
    Integer ERROR = 20001;
    Integer ERROR_SELECT=20002;
    Integer ERROR_DELETE=20003;
    Integer ERROR_STATUS=20004;
    Integer ERROR_UPDATE=20005;
    Integer ERROR_ADD=20006;
}