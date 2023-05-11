package com.aziubin;

import java.io.IOException;
import java.time.LocalDate;

/**
 * Interface intended to extend date write functionality,
 * for example, write to a database.
 */
interface DateWriter {
    void write(LocalDate date) throws IOException;
    void commit() throws IOException;
}
