package com.consoul.movesary.bootstraps;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.junit.jupiter.api.Assertions.*;

class DataLoaderTests {
    
    LocalDate testNextMonday(LocalDate today) {
        LocalDate nextMonday = DataLoader.nextMonday(today);

        assertEquals("Monday", nextMonday.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH));
        assertTrue(nextMonday.isAfter(today));
        assertTrue(DAYS.between(today, nextMonday) <= 7);
        return nextMonday;
    }

    @Test
    void whenTodayIsSet_thenReturnNextMonday(){
        // given
        LocalDate today = LocalDate.now();

        // when // then
        testNextMonday(today);
    }

    @Test
    void whenMondayIsSet_thenReturnNextMonday() {
        LocalDate today = LocalDate.of(2020, Month.DECEMBER, 28);

        LocalDate nextMondayInNextYear = testNextMonday(today);
        assertEquals(2021, nextMondayInNextYear.getYear());
    }

    @Test
    void whenSundayIsSet_thenReturnNextMonday() {
        LocalDate today = LocalDate.of(2020, Month.DECEMBER, 27);

        testNextMonday(today);
    }

    @Test
    void whenMaxLocalDateIsSetToReturnNextMonday_thenExceptionThrown() {
	   Exception exception = assertThrows(DateTimeException.class, () ->  DataLoader.nextMonday(LocalDate.MAX));

        String expectedMessage = "Invalid value for Year (valid values -999999999 - 999999999): 1000000000";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        assertEquals(DateTimeException.class, exception.getClass());
    }
}

