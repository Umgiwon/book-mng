package com.cdri.bookmng.global.filter;

import com.cdri.bookmng.global.enums.common.ApiReturnCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
public class TransactionFilter implements Filter {

    private final ReadOnlyTx readOnlyTx;
    private final Set<String> readOnlyMap = new HashSet<>();

    public TransactionFilter(ReadOnlyTx readOnlyTx) {
        this.readOnlyTx = readOnlyTx;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpReq = (HttpServletRequest) request;
        HttpServletResponse httpRes = (HttpServletResponse) response;

        try {
            if (readOnlyRequest(httpReq)) {
                readOnlyTx.doInReadOnly(() -> chain.doFilter(request, response));
            } else {
                chain.doFilter(request, response);
            }
        } catch (ServletException | IOException e) {
            log.error("Error processing request", e);
            handleException(httpReq, httpRes, e);
        }
    }

    private boolean readOnlyRequest(HttpServletRequest httpReq) {
        String method = httpReq.getMethod();
        String uri = httpReq.getRequestURI();
        String key = method + " " + uri;
        return readOnlyMap.contains(key) || key.equals("GET /test/transaction");
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

    @Override
    public void init(FilterConfig filterConfig) {
        // No initialization needed
    }

    @Override
    public void destroy() {
        // No cleanup needed
    }
}
