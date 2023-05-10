package com.aziubin;

import java.time.format.DateTimeFormatter;

abstract class AbstractJsonStreamSupport {
    final static String JSON_ARRAY_START = "[";
    final static String JSON_ARRAY_END = "]";

    final static char JSON_SEPARATOR = ',';
    final static char JSON_QUOTE = '"';
    final static String JSON_SEPARATOR_PATTERN = "[\t\r\n ,\"]+";
    final static String JSON_NON_WORD_PATTERN = "[\t\r\n ]+";

    static final String YYYY_MMM_DD = "yyyy-MM-dd"; // todo ISO 8601 compatible
    final static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(YYYY_MMM_DD);
}
