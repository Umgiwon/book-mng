package com.cdri.bookmng.global.filter;

import jakarta.servlet.ServletException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

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
