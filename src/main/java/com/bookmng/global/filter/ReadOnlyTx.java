package com.bookmng.global.filter;

import jakarta.servlet.ServletException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

/**
 * * 들어온 요청을 읽기 전용(readOnly = ture)으로 처리할지(readOnly = false)으로 처리할지 제어한다.
 * * GET일 경우 (readOnly = true) 처리
 */
@Service
public class ReadOnlyTx {

    @Transactional(readOnly = true)
    public void doInReadOnly(ExRunnable runnable) throws IOException, ServletException {
        runnable.run();
    }

    @Transactional(rollbackFor = {Exception.class, RuntimeException.class, Error.class})
    public void doInWrite(ExRunnable runnable) throws IOException, ServletException {
        runnable.run();
    }

    interface ExRunnable {
        void run() throws IOException, ServletException;
    }
}
