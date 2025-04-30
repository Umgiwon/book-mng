package com.bookmng.global.filter;

import com.bookmng.global.enums.common.ApiReturnCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * * transaction 관리를 처리하는 filter (모든 GET 요청에 대해서 readOnly 처리)
 * * JPA 사용시 불필요한 flush를 줄여서 성능 샹항(readOnly = true 일 경우)
 * 특정 목적이 있는 경우 filter 차원에서 transaction을 관리하지만,
 * 기본적으로 서비스 계층에서 Transaction 설정하는게 안정적이고 유지보수가 쉽다.
 * filter 차원에서 transaction을 관리할 경우 AOP로 적용되는 @Transactional 과 경계가 중첩될 수 있으니 주의
 */
@Slf4j
//@Component /* 서비스 계층에서 사용하므로써 해당 filter 제외 */
@RequiredArgsConstructor
public class TransactionFilter implements Filter {

    private final ReadOnlyTx readOnlyTx;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpReq = (HttpServletRequest) request;
        HttpServletResponse httpRes = (HttpServletResponse) response;

        String method = httpReq.getMethod();

        try {
            if ("GET".equalsIgnoreCase(method)) { // GET 요청에 대해서 readOnly
                readOnlyTx.doInReadOnly(() -> chain.doFilter(request, response));
            } else {
                readOnlyTx.doInWrite(() -> chain.doFilter(request, response));
            }
        } catch (ServletException | IOException e) {
            log.error("Error processing request [{} {}]", method, httpReq.getRequestURI(), e);
            handleException(httpReq, httpRes, e);
        }
    }

    private void handleException(HttpServletRequest httpReq, HttpServletResponse httpRes, Exception e) throws IOException {
        Map<String, Object> mapBodyException = Map.of(
                "success", false,
                "path", httpReq.getServletPath(),
                "timestamp", LocalDateTime.now().toString(),
                "errorCode", ApiReturnCode.SERVER_ERROR.getCode(),
                "errorMessage", ApiReturnCode.SERVER_ERROR.getMessage()
        );
        httpRes.setContentType("application/json");
        httpRes.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        new ObjectMapper().writeValue(httpRes.getOutputStream(), mapBodyException);
    }
}
