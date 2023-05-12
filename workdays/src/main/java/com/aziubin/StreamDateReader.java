package com.aziubin;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.Scanner;

/**
 * Minimalistic implementation of DateReader interface,
 * which expects JSON array in underlying stream.
 * Edge cases or error processing is not covered for the sake of simplicity.
 */
class StreamDateReader extends AbstractJsonStreamSupport implements DateReader {
    boolean isBof = true;
    boolean isEof = false;
    Scanner scanner;

    StreamDateReader(InputStream stream) {
        scanner = new Scanner(stream);
        scanner.useDelimiter(JSON_SEPARATOR_PATTERN);
    }

    /**
     * Process the start of JSON array.
     * precondition: scanner.hasNext()
     */
    private void readJsonArrayStart() throws DateReaderException {
        scanner.useDelimiter(JSON_NON_WORD_PATTERN);
        String entry = scanner.next();
        if (!JSON_ARRAY_START.equals(entry)) {
            throw new DateReaderException("Unexpected token in JSON array, expected " + JSON_ARRAY_START);
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
                    return LocalDate.parse(entry, DATE_TIME_FORMATTER);
                }
            }
        } else {
            throw new DateReaderException("Attempt to read over the end of JSON array.");
        }
    }

}
