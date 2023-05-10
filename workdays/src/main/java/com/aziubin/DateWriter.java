package com.aziubin;

import java.io.IOException;
import java.time.LocalDate;

interface DateWriter {
    void write(LocalDate date) throws IOException;
    void setEnd() throws IOException;
}
