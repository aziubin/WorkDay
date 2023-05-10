package com.aziubin;

import java.time.LocalDate;

public interface DateReader {
    LocalDate read() throws DateReaderException;

}
