package com.aziubin;

import java.io.IOException;
import java.time.LocalDate;

/**
 * Workdays interface representing the following core functionality:<br>
 * 1. Calculate the number of workdays between two given dates (inclusive);<br>
 * 2. Set and add holidays<br>
 * 3. Read and write holidays from/to JSON file via DateReader and
 *    DateWriter interfaces.
 */
public interface Workdays {

    /**
     * @param startDate inclusive start date of the days range
     * @param endDate inclusive end date of the days range
     * 
     * @return the number of workdays in specified range excluding holidays. 
     */
    long getWorkdays(LocalDate startDate, LocalDate endDate);

    /**
     * @param holidayDate the date representing non-working day because of holiday.
     * 
     * @return true as an indication that a new holiday date is added
     * and assumed for further calculations.
     */
    boolean addHoliday(LocalDate holidayDate);

    /**
     * @param reader the instance of reader interface, which acts as a source of holiday entries.
     * 
     * @return the number of holiday entries successfully loaded and assumed for further calculations.
     * @throws DateReaderException
     */
    long load(DateReader reader) throws DateReaderException;

    /**
     * @param writer the instance of writer interface, which acts as a target to save holiday entries.
     * 
     * @return the number of holiday successfully saved to the writer, which is usually
     * the total number of existing holidays.
     * @throws IOException
     */
    long save(DateWriter writer) throws IOException;

}
