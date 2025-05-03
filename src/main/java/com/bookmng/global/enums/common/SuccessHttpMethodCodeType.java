package com.bookmng.global.enums.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.stream.Stream;

@RequiredArgsConstructor
@Getter
public enum SuccessHttpMethodCodeType {

    GET("GET", HttpStatus.OK.value(), "정상적으로 조회되었습니다."),
    POST("POST", HttpStatus.CREATED.value(), "정상적으로 등록되었습니다."),
    DELETE("DELETE", HttpStatus.OK.value(), "정상적으로 삭제되었습니다."),
    PATCH("PATCH", HttpStatus.OK.value(), "정상적으로 수정되었습니다.")
    ;

    private final String requestMethod;
    private final int httpStatus;
    private final String message;

    /**
     * requestMethod 코드를 받아서 해당하는 enum을 반환한다.
     * @param requestMethod
     * @return
     */
    public static SuccessHttpMethodCodeType from(String requestMethod) {
        return Stream.of(SuccessHttpMethodCodeType.values())
                .filter(httpMethodCode -> httpMethodCode.getRequestMethod().equals(requestMethod))
                .findFirst()
                .orElse(null);
    }
}
