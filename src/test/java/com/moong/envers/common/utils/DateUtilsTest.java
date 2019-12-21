package com.moong.envers.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

@Slf4j
public class DateUtilsTest {
    private final int MIN_YEAR = 1;
    private final int MAX_YEAR = 9999;

    @Test
    void testMin() {
        LocalDate localDate = LocalDate.of(MIN_YEAR,1,1);
        ZonedDateTime zonedDateTime = localDate.atStartOfDay(ZoneId.systemDefault());
        Instant instant = Instant.from(zonedDateTime);

        Date date = Date.from(instant);
        log.info("date {}", date);
    }

    @Test
    void testMax() {
        LocalDate localDate = LocalDate.of(MAX_YEAR, 12, 31);
        Instant instant = localDate.atStartOfDay(ZoneId.systemDefault())
                .toInstant();
        Date date = Date.from(instant);
        log.info("date {}", date);
    }

    @Test
    void testOf() {
        LocalDate localDate = LocalDate.of(2019, 12, 31);
        Instant instant = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Date date =  Date.from(instant);
        log.info("date {} ", date);
    }
}
