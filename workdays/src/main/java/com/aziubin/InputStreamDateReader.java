package com.aziubin;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.Scanner;

/**
 * todo throw DateReaderException for input ,, 
 * todo number, null?
 */
class InputStreamDateReader extends abstractJsonStreamSupport implements DateReader {
    boolean isBof = true;
    boolean isEof = false;
    Scanner scanner;

    InputStreamDateReader(InputStream stream) throws Exception {
        scanner = new Scanner(stream);
        scanner.useDelimiter(JSON_SEPARATOR_PATTERN);
    }

    private void readJsonArrayStart() throws DateReaderException {
        scanner.useDelimiter(JSON_NON_WORD_PATTERN);
        if (scanner.hasNext()) {
            String entry = scanner.next();
            if (!JSON_ARRAY_START.equals(entry)) {
                throw new DateReaderException("Unexpected token in JSON array, expected " + JSON_ARRAY_START);
            }
        }
        scanner.useDelimiter(JSON_SEPARATOR_PATTERN);
    }

    @Override
    public LocalDate read() throws DateReaderException {
        if (!isEof) {
            if (!scanner.hasNext()) {
                throw new DateReaderException("Missing end of JSON array.");
            } else {
                if (isBof) {
                    isBof = false;
                    readJsonArrayStart();
                }

                String entry = scanner.next();
                if (JSON_ARRAY_END.equals(entry)) {
                    isEof = true;
                    return null;
                } else {
//                    if (entry.length() < 3) {
//                        throw new DateReaderException("JSON token is too short.");
//                    }
//                    CharSequence unquoted = entry.subSequence(1, entry.length() - 1); 
//                    return LocalDate.parse(unquoted, DATE_TIME_FORMATTER);
                    return LocalDate.parse(entry, DATE_TIME_FORMATTER);
                }
            }
        } else {
            throw new DateReaderException("Attempt to read over the end of JSON array.");
        }
    }

}
