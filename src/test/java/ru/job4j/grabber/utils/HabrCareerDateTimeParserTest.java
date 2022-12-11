package ru.job4j.grabber.utils;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class HabrCareerDateTimeParserTest {

    @Test
    void checkParserFromIsoOffsetDateTimeToLocalDateTime() {
        String parse = "2022-12-09T22:27:17+03:00";
        LocalDateTime actual = new HabrCareerDateTimeParser().parse(parse);
        String expected = "2022-12-09T22:27:17";
        assertThat(actual).isEqualTo(expected);
    }
}
