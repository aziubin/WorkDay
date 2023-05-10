package com.aziubin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TreeSetWorkDay implements WorkDay {
    Logger logger = Logger.getLogger("WorkDayInspector");
    protected Set<DayOfWeek> weekendSet = EnumSet.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);

    TreeSet<LocalDate> holidays = new TreeSet<>();

    DateReader reader;
    DateWriter writer;

    public DateReader getReader() {
        return reader;
    }

    public void setReader(DateReader reader) {
        this.reader = reader;
    }

    public DateWriter getWriter() {
        return writer;
    }

    public void setWriter(DateWriter writer) {
        this.writer = writer;
    }

    boolean addHoliday(LocalDate date) {
        boolean result = false;
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        //if (dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY) {
        if (!weekendSet.contains(dayOfWeek)) {
            result = this.holidays.add(date);
        } else {
            logger.log(Level.INFO, "Holiday is ignored because it overlaps with weekend: " + date);
        }
        return result;
    }

    public int load() throws DateReaderException {
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

    public int save() throws IOException {
        int result = 0;
        for (LocalDate date : holidays) {
            writer.write(date);
            ++result;
        }
        writer.setEnd();
        return result;
    }

    TreeSetWorkDay(Iterable<LocalDate> dates) {
        for (LocalDate localDate : dates) {
            addHoliday(localDate);
        }
        //  this.holidays.addAll(holidays);
    }

    TreeSetWorkDay(Iterable<LocalDate> holidays, Set<DayOfWeek> weekendSet) {
        this(holidays);
        this.weekendSet = weekendSet;
    }

    TreeSetWorkDay(DateReader reader) throws DateReaderException {
        this.reader = reader;
        load();
    }

    @Override
    public int delta(LocalDate startDate, LocalDate endDate) {
        int result = 0;
        LocalDate ceiling = holidays.ceiling(startDate);  // binary search in a red–black tree log(n).
        LocalDate floor = holidays.floor(endDate);
        LocalDate higherDate = holidays.higher(endDate);
        if (null == higherDate) {
            higherDate = holidays.last(); // todo NoSuchElementException
            //todo higherDate < startDate higherDate = startDate result ++  
        }

        //Set customHolidays1 = holidays.subSet(startDate, higherDate);  // 2011-06-15 2011-06-23 []
        Set customHolidays1 = holidays.subSet(startDate, true, endDate, true);  // binary search in a red–black tree log(n).
        result -= customHolidays1.size();
        //Set customHolidays2 = holidays.subSet(holidays.ceiling(startDate), holidays.floor(endDate)); // 2011-06-07 2011-06-23 [2011-06-07]
        return result;
    }

    @Override
    public int delta(LocalDate startDate) {
        return delta(startDate, LocalDate.now());
    }

}
