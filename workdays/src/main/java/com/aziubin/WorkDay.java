package com.aziubin;

import java.io.IOException;
import java.time.LocalDate;

public interface WorkDay {
    long getWorkdays(LocalDate startDate, LocalDate endDate);
    long getWorkdays(LocalDate startDate);
    boolean addHoliday(LocalDate startDate);
    long load(DateReader reader) throws DateReaderException;
    long save(DateWriter writer) throws IOException;

}
