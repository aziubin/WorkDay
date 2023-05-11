package com.aziubin;

import java.time.LocalDate;

/**
 * Interface intended to extend date read functionality,
 * for example, read from a database.
 */
public interface DateReader {
    LocalDate read() throws DateReaderException;

}
