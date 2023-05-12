package com.aziubin;

import java.time.LocalDate;

/**
 * Interface intended to extend date read functionality,
 * for example, read from a database.
 */
public interface DateReader {

    /**
     * @return subsequent LocalDate instance or null when instance cannot be read anymore. 
     * @throws DateReaderException
     */
    LocalDate read() throws DateReaderException;

}
