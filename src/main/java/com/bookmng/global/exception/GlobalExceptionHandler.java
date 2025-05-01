package com.bookmng.global.exception;

import com.bookmng.global.enums.common.ApiReturnCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    /**
     * RuntimeException 발생시 처리 핸들러
     * @param ex
     * @return
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ExceptionMsg> handleRuntimeException(HttpServletRequest request, RuntimeException ex) {
        log.error(ex.getMessage(), ex);

        ExceptionMsg exceptionMsg = ExceptionMsg.builder()
                .success(false)
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .errorCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .errorMessage(String.format("%s [%s]", ApiReturnCode.SERVER_ERROR.getMessage(), ex.getMessage()))
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionMsg);
    }

    /**
     * IllegalAccessException 발생시 처리 핸들러
     * @param ex
     * @return
     */
    @ExceptionHandler(IllegalAccessException.class)
    public ResponseEntity<ExceptionMsg> handleIllegalAccessException(HttpServletRequest request, IllegalAccessException ex) {
        log.error(ex.getMessage(), ex);

        ExceptionMsg exceptionMsg = ExceptionMsg.builder()
                .success(false)
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .errorCode(HttpStatus.BAD_REQUEST.value())
                .errorMessage(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionMsg);
    }

    /**
     * request body의 argument validation 처리 핸들러 (@valid, @validated 어노테이션)
     * @param ex
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionMsg> handleMethodArgumentNotValidException(HttpServletRequest request, MethodArgumentNotValidException ex) {

        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> "[" + error.getField() + "] " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ExceptionMsg exceptionMsg = ExceptionMsg.builder()
                .success(false)
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .errorCode(HttpStatus.BAD_REQUEST.value())
                .errorMessage(errorMessage)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionMsg);
    }

    /**
     * Constraint Violdation Exception 처리 핸들러 (custom 어노테이션)
     * @param ex
     * @return
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionMsg> handleConstraintViolationException(HttpServletRequest request, ConstraintViolationException ex) {

        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();

        ExceptionMsg exceptionMsg = ExceptionMsg.builder()
                .success(false)
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .errorCode(HttpStatus.BAD_REQUEST.value())
                .errorMessage(violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(", ")))
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionMsg);
    }

    /**
     * parameter 데이터 type이 일치하지 않을 때 처리 핸들러 (IllegalArgumentException의 하위)
     * @param ex
     * @return
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ExceptionMsg> handleTypeMismatchException(HttpServletRequest request, MethodArgumentTypeMismatchException ex) {

        String errorMessage = String.format("'%s' 값은 잘못된 형식입니다. %s 타입이어야 합니다.",
                ex.getValue(), ex.getRequiredType().getSimpleName());

        ExceptionMsg exceptionMsg = ExceptionMsg.builder()
                .success(false)
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .errorCode(HttpStatus.BAD_REQUEST.value())
                .errorMessage(errorMessage)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionMsg);
    }

    /**
     * missing parameter 발생 시 처리 핸들러
     * @param ex
     * @return
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ExceptionMsg> handleMissingParams(HttpServletRequest request, MissingServletRequestParameterException ex) {

        ExceptionMsg exceptionMsg = ExceptionMsg.builder()
                .success(false)
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .errorCode(HttpStatus.BAD_REQUEST.value())
                .errorMessage(ex.getParameterName() + " 파라미터는 필수값입니다.")
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionMsg);
    }

    /**
     * BusinessException 발생 시 처리 핸들러
     * @param ex
     * @return
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ExceptionMsg> handleBusinessException(HttpServletRequest request, BusinessException ex) {

        ExceptionMsg exceptionMsg = ExceptionMsg.builder()
                .success(false)
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .errorCode(ex.getApiReturnCode().getCode())
                .errorMessage(ex.getApiReturnCode().getMessage())
                .build();

        return ResponseEntity.status(ex.getApiReturnCode().getCode()).body(exceptionMsg);
    }

    /**
     * NoHandlerFoundException 발생 시 처리 핸들러
     * @param request
     * @param ex
     * @return
     */
    @ExceptionHandler({NoHandlerFoundException.class})
    public ResponseEntity<ExceptionMsg> handleNoHandlerFoundException(HttpServletRequest request, Exception ex) {

        ExceptionMsg exceptionMsg = ExceptionMsg.builder()
                .success(false)
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .errorCode(ApiReturnCode.NO_URL_ERROR.getCode())
                .errorMessage(ApiReturnCode.NO_URL_ERROR.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionMsg);
    }

    /**
     * HttpRequestMethodNotSupportedException 발생 시 처리 핸들러
     * http 메소드 오류
     * @param request
     * @param ex
     * @return
     */
    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    public ResponseEntity<ExceptionMsg> handleHttpRequestMethodNotSupportedException(HttpServletRequest request, Exception ex) {

        ExceptionMsg exceptionMsg = ExceptionMsg.builder()
                .success(false)
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .errorCode(ApiReturnCode.METHOD_NOT_ALLOWED.getCode())
                .errorMessage(ApiReturnCode.METHOD_NOT_ALLOWED.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(exceptionMsg);
    }

    /**
     * HttpMessageNotReadableException 발생 시 처리 핸들러
     * 클라이언트가 요청 본문을 잘못 전달한 경우
     * @param request
     * @param ex
     * @return
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionMsg> handleHttpMessageNotReadableException(HttpServletRequest request, HttpMessageNotReadableException ex) {
        log.error(ex.getMessage(), ex);

        ExceptionMsg exceptionMsg = ExceptionMsg.builder()
                .success(false)
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .errorCode(ApiReturnCode.BAD_REQUEST_TEXT.getCode())
                .errorMessage(ApiReturnCode.BAD_REQUEST_TEXT.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionMsg);
    }

    /**
     * UnsupportedMediaTypeStatusException 발생 시 처리 핸들러
     * 서버가 지원하지 않는 미디어 타입일 경우
     * @param request
     * @param ex
     * @return
     */
    @ExceptionHandler(UnsupportedMediaTypeStatusException.class)
    public ResponseEntity<ExceptionMsg> handleUnsupportedMediaType(HttpServletRequest request, UnsupportedMediaTypeStatusException ex) {
        log.error(ex.getMessage(), ex);

        ExceptionMsg exceptionMsg = ExceptionMsg.builder()
                .success(false)
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .errorCode(ApiReturnCode.UNSUPPORTED_MEDIA_TYPE.getCode())
                .errorMessage(ApiReturnCode.UNSUPPORTED_MEDIA_TYPE.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(exceptionMsg);
    }

    /**
     * HttpMediaTypeNotSupportedException 발생 시 처리 핸들러
     * @param request
     * @param ex
     * @return
     */
    @ExceptionHandler({HttpMediaTypeNotSupportedException.class})
    public ResponseEntity<ExceptionMsg> handleHttpMediaTypeNotSupportedException(HttpServletRequest request, HttpMediaTypeNotSupportedException ex) {

        String unsupportedType = ex.getContentType() != null ? ex.getContentType().toString() : "null";
        String supportedTypes = ex.getSupportedMediaTypes().stream()
                .map(MediaType::toString)
                .collect(Collectors.joining(", "));

        String errorMessage = "지원되지 않는 Content-Type입니다: " + unsupportedType +
                ". 사용 가능한 타입: " + supportedTypes;

        ExceptionMsg exceptionMsg = ExceptionMsg.builder()
                .success(false)
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .errorCode(ApiReturnCode.UNSUPPORTED_MEDIA_TYPE.getCode())
                .errorMessage(errorMessage)
                .build();

        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(exceptionMsg);
    }
}
