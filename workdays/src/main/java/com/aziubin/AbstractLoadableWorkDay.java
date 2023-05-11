package com.aziubin;

import java.time.LocalDate;

public abstract class AbstractLoadableWorkDay implements WorkDay {

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
    public abstract boolean addHoliday(LocalDate date);

}
