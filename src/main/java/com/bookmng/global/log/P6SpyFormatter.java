package com.bookmng.global.log;

import com.p6spy.engine.logging.Category;
import com.p6spy.engine.spy.P6SpyOptions;
import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import jakarta.annotation.PostConstruct;
import org.hibernate.engine.jdbc.internal.FormatStyle;
import org.springframework.context.annotation.Configuration;

import java.util.Locale;

/**
 * * Query Logging formatter를 설정하는 파일
 */
@Configuration
public class P6SpyFormatter implements MessageFormattingStrategy {

    @PostConstruct
    public void init() {
        P6SpyOptions.getActiveInstance().setLogMessageFormat(this.getClass().getName());
    }

    @Override
    public String formatMessage(int connectionId, String now, long elapsed, String category, String prepared, String sql, String url) {
        if (sql == null || sql.trim().isEmpty()) {
            return "";
        }

        String formattedSql = formatSql(category, sql);
        return String.format("[%s] | %d ms | %s", category, elapsed, formattedSql);
    }

    private String formatSql(String category, String sql) {
        if (Category.STATEMENT.getName().equals(category)) {
            String trimmedLowerSql = sql.trim().toLowerCase(Locale.ROOT);
            if (trimmedLowerSql.startsWith("create") ||
                    trimmedLowerSql.startsWith("alter") ||
                    trimmedLowerSql.startsWith("comment")) {
                return FormatStyle.DDL.getFormatter().format(sql);
            } else {
                return FormatStyle.BASIC.getFormatter().format(sql);
            }
        }
        return sql;
    }
}
