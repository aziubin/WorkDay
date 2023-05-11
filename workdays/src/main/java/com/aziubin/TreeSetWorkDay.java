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
    private static DayOfWeek[] dayOfWeekValues = DayOfWeek.values();

    /** Customizes the days, which are regularly non-working days of the week, Saturday and Sunday by default. */
    protected Set<DayOfWeek> weekendSet = EnumSet.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);

    /** Holidays to assume when calculating the number of working days. */
    TreeSet<LocalDate> holidaySet = new TreeSet<>();

    TreeSetWorkDay(Set<DayOfWeek> weekendSet) {
        this.weekendSet = weekendSet;
    }

    TreeSetWorkDay(Iterable<LocalDate> holidays) {
        load(holidays);
    }

    TreeSetWorkDay(Iterable<LocalDate> holidays, Set<DayOfWeek> weekendSet) {
        this(weekendSet);
        load(holidays);
    }

    TreeSetWorkDay(DateReader reader) throws DateReaderException {
        load(reader);
    }

    TreeSetWorkDay(DateReader reader, Set<DayOfWeek> weekendSet) throws DateReaderException {
        this(weekendSet);
        load(reader);
    }

    protected void load (Iterable<LocalDate> holidays) {
        for (LocalDate localDate : holidays) {
            addHoliday(localDate);
        }
    }

    protected boolean addHoliday(LocalDate date) {
        boolean result = false;
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        if (!weekendSet.contains(dayOfWeek)) {
            result = this.holidaySet.add(date);
        } else {
            logger.log(Level.FINEST, "Holiday is ignored because it overlaps with non-working day of the week: " + date);
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
    private static Set<DayOfWeek> getIncompleteWeek(LocalDate date, long incompleteWeekDays) {
        Set<DayOfWeek> result = EnumSet.noneOf(DayOfWeek.class);
        int dayIndex = date.getDayOfWeek().ordinal();  // avoid additional getDayOfWeek() as it contains some heavy math.
        for (int i = 0; i < incompleteWeekDays; ++i) {
            result.add(dayOfWeekValues[(i + dayIndex) % dayOfWeekValues.length]);
        }
        return result;
    }

    /**
     * Usually non-working days of the week are Saturday and Sunday.
     * Assume that depending from culture, customizable non-working days are possible.
     * @param startDate inclusive start date of the range
     * @param endDate inclusive end date of the range
     * 
     * @return the number of working days in a range specified by the startDate inclusive and endDate inclusive.
     */
    @Override
    public long getWorkdays(LocalDate startDate, LocalDate endDate) {
        long rangeDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        if (0 > rangeDays) {
            throw new IllegalArgumentException("Start date of the range is after the end date.");
        }
        // Assume regular non working days of the week for all complete weeks;
        long workDays = rangeDays/dayOfWeekValues.length * (dayOfWeekValues.length - weekendSet.size());

        Set<DayOfWeek> incompleteWeek = getIncompleteWeek(startDate, rangeDays % dayOfWeekValues.length);
        incompleteWeek.removeAll(weekendSet);
        long partialWeekWorkDays = incompleteWeek.size();
        workDays += partialWeekWorkDays;

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
