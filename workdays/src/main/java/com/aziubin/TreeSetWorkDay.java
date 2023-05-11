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

public class TreeSetWorkDay extends AbstractLoadableWorkDay {
    private static final Logger logger = Logger.getLogger(TreeSetWorkDay.class.getName());

    protected Set<DayOfWeek> weekendSet = EnumSet.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
    TreeSet<LocalDate> holidaySet = new TreeSet<>();

    protected boolean addHoliday(LocalDate date) {
        boolean result = false;
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        //if (dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY) {
        if (!weekendSet.contains(dayOfWeek)) {
            result = this.holidaySet.add(date);
        } else {
            logger.log(Level.INFO, "Holiday is ignored because it overlaps with weekend: " + date);
        }
        return result;
    }

    public int save(DateWriter writer) throws IOException {
        int result = 0;
        for (LocalDate date : holidaySet) {
            writer.write(date);
            ++result;
        }
        writer.setEnd();
        return result;
    }

    TreeSetWorkDay(Set<DayOfWeek> weekendSet) {
        this.weekendSet = weekendSet;
    }

    TreeSetWorkDay(Iterable<LocalDate> holidays) {
        for (LocalDate localDate : holidays) {
            addHoliday(localDate);
        }
    }

    TreeSetWorkDay(Iterable<LocalDate> holidays, Set<DayOfWeek> weekendSet) {
        this(holidays);
        this.weekendSet = weekendSet;
    }

    TreeSetWorkDay(DateReader reader) throws DateReaderException {
        load(reader);
    }

    TreeSetWorkDay(DateReader reader, Set<DayOfWeek> weekendSet) throws DateReaderException {
        this(reader);
        this.weekendSet = weekendSet;
    }

    @Override
    public int delta(LocalDate startDate, LocalDate endDate) {
        int result = 0;
        LocalDate ceiling = holidaySet.ceiling(startDate);  // binary search in a red–black tree log(n).
        LocalDate floor = holidaySet.floor(endDate);
        LocalDate higherDate = holidaySet.higher(endDate);
        if (null == higherDate) {
            higherDate = holidaySet.last(); // todo NoSuchElementException
            //todo higherDate < startDate higherDate = startDate result ++  
        }

        //Set customHolidays1 = holidays.subSet(startDate, higherDate);  // 2011-06-15 2011-06-23 []
        Set customHolidays1 = holidaySet.subSet(startDate, true, endDate, true);  // binary search in a red–black tree log(n).
        result -= customHolidays1.size();
        //Set customHolidays2 = holidays.subSet(holidays.ceiling(startDate), holidays.floor(endDate)); // 2011-06-07 2011-06-23 [2011-06-07]
        return result;
    }

    @Override
    public int delta(LocalDate startDate) {
        return delta(startDate, LocalDate.now());
    }

}
