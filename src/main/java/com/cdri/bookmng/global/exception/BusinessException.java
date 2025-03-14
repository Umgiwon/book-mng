package com.cdri.bookmng.global.exception;

import com.cdri.bookmng.global.enums.common.ApiReturnCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BusinessException extends RuntimeException {

    private ApiReturnCode apiReturnCode;
}
