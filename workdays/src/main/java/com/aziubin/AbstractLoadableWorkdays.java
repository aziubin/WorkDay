package com.aziubin;

import java.time.LocalDate;

/**
 * Base class for workdays subclasses supporting loading of holiday dates.
 */
public abstract class AbstractLoadableWorkdays implements Workdays {

    @Override
    public long load(DateReader reader) throws DateReaderException {
        int result = 0;
        LocalDate date = reader.read();
        while (null != date) {
            if (addHoliday(date)) {
                ++result;
            }
            date = reader.read();
        }
        return result;
    }

    @Override
    public abstract boolean addHoliday(LocalDate holidayDate);

}
