package com.example.hr.shared.infra.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;
import org.springframework.data.jdbc.core.convert.JdbcCustomConversions;

import java.time.YearMonth;
import java.util.List;

@Configuration
public class JdbcConfig extends AbstractJdbcConfiguration {
    @Override
    public JdbcCustomConversions jdbcCustomConversions() {
        return new JdbcCustomConversions(List.of(new YearMonthToStringConverter(), new StringToYearMonthConverter()));
    }

    static class YearMonthToStringConverter implements org.springframework.core.convert.converter.Converter<YearMonth, String> {
        @Override
        public String convert(YearMonth source) {
            return source.toString();
        }
    }

    static class StringToYearMonthConverter implements org.springframework.core.convert.converter.Converter<String, YearMonth> {
        @Override
        public YearMonth convert(String source) {
            return YearMonth.parse(source);
        }
    }
}
