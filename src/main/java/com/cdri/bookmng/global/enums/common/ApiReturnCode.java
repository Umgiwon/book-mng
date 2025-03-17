package com.cdri.bookmng.global.enums.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ApiReturnCode {

    /************** 2XX **************/
    NO_DATA("데이터가 없습니다.", 204),

    /************** 4XX **************/
    BAD_REQUEST_TEXT("잘못된 요청 본문 형식입니다.", 400),
    NO_DATA_ERROR("데이터가 없습니다.", 404),
    NO_URL_ERROR("잘못된 URL 입니다.", 404),
    METHOD_NOT_ALLOWED("해당 요청에 대해 허용되지 않은 HTTP 메서드입니다. (Method Not Allowed)", 405),
    UNSUPPORTED_MEDIA_TYPE("지원하지 않는 미디어 타입입니다.", 415),

    /* book */
    BOOK_CATEGORY_ESSENTIAL_ERROR("최소 한개의 카테고리가 필요합니다.", 400),
    BOOK_TITLE_CONFLICT_ERROR("중복된 제목입니다.", 409),

    /************** 5XX **************/
    SERVER_ERROR("서버에서 오류가 발생했습니다.", 500)
    ;

    private final String message;
    private final int code;
}
