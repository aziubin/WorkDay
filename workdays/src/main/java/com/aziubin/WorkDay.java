package com.aziubin;

import java.io.IOException;
import java.time.LocalDate;

public interface WorkDay {
    int delta(LocalDate startDate, LocalDate endDate);
    int delta(LocalDate startDate);
    int load(DateReader reader) throws DateReaderException;
    int save(DateWriter writer) throws IOException;

}


