package com.aziubin;

import java.io.IOException;
import java.time.LocalDate;

/**
 * Interface intended to extend date write functionality,
 * for example, write a sequence of dates to a database.
 */
interface DateWriter {

    /**
     * Save LocalDate instance one by one in sequential order.
     * @throws IOException
     */
    void write(LocalDate date) throws IOException;

    /**
     * Indicate the end of LocalDate sequence,
     * so some customized behavior is possible, depending from underlying media,
     * for example write the end of JSON array according to JSON syntax. 
     * @throws IOException
     */
    void commit() throws IOException;
}
