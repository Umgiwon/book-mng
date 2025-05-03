package com.bookmng.global.domain.dto;

import com.bookmng.global.constant.ResponseMessageConst;
import com.bookmng.global.enums.common.SuccessHttpMethodCodeType;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * BaseResponse 반환값에 대한 처리 Class
 * 조회 및 트렌젝션 성공 시 success
 * 조회 데이터 없을 경우 noContent
 * 실패시엔 Exception 처리로 handle
 */
public class BaseResponseFactory {

    /**
     * 성공 시
     * @param data
     * @return
     * @param <T>
     */
    public static <T> BaseResponse<T> success(T data) {

        // 내용 없을 경우 noContent
        if(ObjectUtils.isEmpty(data)) {
            return noContent();
        }

        // 요청 method type 조회
        SuccessHttpMethodCodeType methodType = getSuccessHttpMethodCodeType();

        return baseResponseBuilder(methodType.getHttpStatus(), methodType.getMessage(), getSize(data), data, null);
    }

    /**
     * 성공 시(Paging 처리)
     * @param page
     * @return
     * @param <T>
     */
    public static <T> BaseResponse<List<T>> successWithPagination(Page<T> page) {
        List<T> content = page.getContent();

        // 내용 없을 경우 noContent
        if(ObjectUtils.isEmpty(content)) {
            return noContent();
        }

        return baseResponseBuilder(HttpStatus.OK.value(), ResponseMessageConst.SELECT_SUCCESS, content.size(), content, new Pagination(page));
    }

    /**
     * 데이터가 없을 경우에 대한 반환값
     * @return
     * @param <T>
     */
    public static <T> BaseResponse<T> noContent() {
        return baseResponseBuilder(HttpStatus.NO_CONTENT.value(), ResponseMessageConst.NO_CONTENT, 0, null, null);
    }

    /**
     * 공통 BaseResponse Builder
     * @param code
     * @param message
     * @param size
     * @param data
     * @param pagination
     * @return
     * @param <T>
     */
    private static <T> BaseResponse<T> baseResponseBuilder(int code, String message, int size, T data, Pagination pagination) {
        return BaseResponse.<T>builder()
                .timeStamp(LocalDateTime.now())
                .httpCode(code)
                .message(message)
                .dataSize(size)
                .data(data)
                .pagination(pagination)
                .build();
    }

    /**
     * * 데이터의 사이즈 꺼내서 return
     * 데이터 없을 경우 0
     * 컬렉션일 경우 컬렉션 사이즈
     * 페이지일 경우 페이지안의 컨텐츠 사이즈
     * 기타 1
     * @param data
     * @return
     */
    private static int getSize(Object data) {
        if(ObjectUtils.isEmpty(data)) return 0;
        if(data instanceof Collection<?>) return ((Collection<?>) data).size();
        if(data instanceof Page<?> page) return page.getContent().size();
        return 1;
    }

    /**
     * 요청 requestMethod에 맞는 Enum 반환
     * @return
     */
    private static SuccessHttpMethodCodeType getSuccessHttpMethodCodeType() {

        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();

        String requestMethod = request.getMethod();

        return SuccessHttpMethodCodeType.from(requestMethod);
    }
}
