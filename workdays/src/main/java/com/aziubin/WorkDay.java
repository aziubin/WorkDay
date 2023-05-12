package com.aziubin;

import java.io.IOException;
import java.time.LocalDate;

/**
 * Interface representing the following functionality:<br>
 * calculate the number of workdays between two given dates (inclusive);<br>
 * set/add holidays<br>
 * ability to read/write holidays from/to JSON file via DateReader and
 * DateWriter interfaces.
 */
public interface WorkDay {
    long getWorkdays(LocalDate startDate, LocalDate endDate);
    boolean addHoliday(LocalDate startDate);
    long load(DateReader reader) throws DateReaderException;
    long save(DateWriter writer) throws IOException;

}
