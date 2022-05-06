package org.talon.ad.advice;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.talon.ad.exception.AdException;
import org.talon.ad.vo.CommonResponse;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Zelong
 * On 2022/5/1
 * global common exception handler, an augmentation to RestController
 **/
@RestControllerAdvice
public class GlobalExceptionAdvice {

    @ExceptionHandler(value = AdException.class)
    public CommonResponse<String> handlerAdException(HttpServletRequest req,
                                                     AdException ex) {
        CommonResponse<String> commonResponse = new CommonResponse<>(-1, "business error");
        commonResponse.setData(ex.getMessage());
        return commonResponse;
    }
}
