package com.aziubin;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TreeSetWorkDay extends AbstractLoadableWorkDay {
    private static final Logger logger = Logger.getLogger(TreeSetWorkDay.class.getName());

    /** Customizes the days, which are non-working days of the week, Saturday and Sunday by default. */
    protected Set<DayOfWeek> weekendSet = EnumSet.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
    TreeSet<LocalDate> holidaySet = new TreeSet<>();

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

    protected boolean addHoliday(LocalDate date) {
        boolean result = false;
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        if (!weekendSet.contains(dayOfWeek)) {
            result = this.holidaySet.add(date);
        } else {
            logger.log(Level.INFO, "Holiday is ignored because it overlaps with non-working day of the week: " + date);
        }
        return result;
    }

    public long save(DateWriter writer) throws IOException {
        int result = 0;
        for (LocalDate date : holidaySet) {
            writer.write(date);
            ++result;
        }
        writer.setEnd();
        return result;
    }

    /**
     * It is not expected a lot of computation here, so
     * optimization for the sake of clarity and simplicity.
     * getDayOfWeek can be heavy
     * @return
     */
    long getIncompleteWeekWorkDays(LocalDate date, long weekOffset) {
        Set<DayOfWeek> week = EnumSet.range(date.getDayOfWeek(), date.plusDays(weekOffset).getDayOfWeek());
        week.removeAll(weekendSet);
        return week.size();

//        int result = 0;
//        DayOfWeek.values()[date.getDayOfWeek().ordinal()];
//        vals[(this.ordinal() + 1) % vals.length];
//        
//        DayOfWeek.values();
//        for (int i = 0; i < weekOffset; ++i) {
//            DayOfWeek dayOfWeek = date.getDayOfWeek();
//            if (!weekendSet.contains(dayOfWeek)) {
//                ++result;
//            }
//            date.plusDays(1);
//        }
//        return result;
    }

    /**
     * Usually non-working days of the week are Saturday and Sunday.
     * Assume that depending from culture, customizable non-working days are possible.
     * @param startDate inclusive start date of the range
     * @param endDate inclusive end date of the range
     * 
     * @return the number of working days in a range specified by the startDate and endDate parameters.
     */
    @Override
    public long getWorkdays(LocalDate startDate, LocalDate endDate) {
        long rangeDays = ChronoUnit.DAYS.between(startDate, endDate);
        long workDays = rangeDays/7 * (7 - weekendSet.size()); // assume non working days of the week.
        //long partialWeekDays = getIncompleteWeekWorkDays(startDate, rangeDays % 7);

        Set<DayOfWeek> incompleteWeek = EnumSet.range(startDate.getDayOfWeek(), endDate.getDayOfWeek());
        if (incompleteWeek.size() != 7) {
            // partial week
            incompleteWeek.removeAll(weekendSet);
            long partialWeekWorkDays = incompleteWeek.size();
            workDays -= partialWeekWorkDays;
        }

        // Create a view of the date range portion of all stored holidays.
        // This does not duplicate date entries underneath, so can be OK with huge volumes.
        Set rangeHolidaySet = holidaySet.subSet(startDate, true, endDate, true);  // binary search in a red–black tree log(n).
        long rangeHolidays = rangeHolidaySet.size();
        long result = workDays - rangeHolidays;
        return result;
    }

    @Override
    public long getWorkdays(LocalDate startDate) {
        return getWorkdays(startDate, LocalDate.now());
    }

}
