package com.aziubin;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation allowing customization of weekend days,
 * which might be necessary for different cultures. 
 * Underneath binary tree data structure is used for optimal search
 * within holidays having complexity O(log(n)). 
 */
public class TreeSetWorkDay extends AbstractLoadableWorkDay {
    private static final Logger logger = Logger.getLogger(TreeSetWorkDay.class.getName());
    private static DayOfWeek[] dayOfWeekValues = DayOfWeek.values();

    /**
     * Customizes regular non-working days of the week, having Saturday and Sunday by default.
     * EnumSet is used to have best possible performance because verification is implemented
     * as fast bit mask operations.
     */
    protected Set<DayOfWeek> weekendSet = EnumSet.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);

    /**
     * Holidays to assume when calculating the number of working days in a range.
     * USe binary tree data structure for optimal performance.
     */
    TreeSet<LocalDate> holidaySet = new TreeSet<>();

    /**
     * Read holidays from a local JSON file.
     * @param filename
     * @throws FileNotFoundException
     * @throws IOException
     * @throws DateReaderException
     */
    public long load(String filename) throws FileNotFoundException, IOException, DateReaderException {
        File file = new File(filename);
        try (InputStream stream = new FileInputStream(file);) {
            DateReader reader = new StreamDateReader(stream);
            return load(reader);
        }
    }

    /**
     * Write holidays to a local JSON file.
     * @param filename
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public long save(String filename) throws FileNotFoundException, IOException {
        File file = new File(filename);
        try (OutputStream stream = new FileOutputStream(file);) {
            DateWriter writer = new StreamDateWriter(stream);
            return save(writer);
        }
    }

    TreeSetWorkDay() {
    }

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

    protected void load(Iterable<LocalDate> holidays) {
        for (LocalDate localDate : holidays) {
            addHoliday(localDate);
        }
    }

    @Override
    public boolean addHoliday(LocalDate date) {
        boolean result = false;
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        if (!weekendSet.contains(dayOfWeek)) {
            result = this.holidaySet.add(date);
        } else {
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "Holiday is ignored because it overlaps with non-working day of the week: " + date);
            }
        }
        return result;
    }

    @Override
    public long load(DateReader reader) throws DateReaderException {
        holidaySet.clear();
        return super.load(reader);
    }

    @Override
    public long save(DateWriter writer) throws IOException {
        int result = 0;
        for (LocalDate date : holidaySet) {
            writer.write(date);
            ++result;
        }
        writer.commit();
        return result;
    }

    /**
     * Enumerate days of incomplete week irrespective to weekends.
     * @param date the day of the week from which to start enumeration.
     * @param incompleteWeekDays the number of week days to include.
     *     Expected value is less than 7, but higher value can be OK too.
     * 
     * @return the set containing incompleteWeekDays entries of week days starting from date parameter
     */
    private static Set<DayOfWeek> getIncompleteWeek(LocalDate date, long incompleteWeekDays) {
        Set<DayOfWeek> result = EnumSet.noneOf(DayOfWeek.class);

        // Calculate getDayOfWeek() only once as it contains some heavy math.
        int dayIdx = date.getDayOfWeek().ordinal();

        // Avoid any conditions, allowing potential for loop unrolling.
        for (int i = 0; i < incompleteWeekDays; ++i) {
            result.add(dayOfWeekValues[(i + dayIdx) % dayOfWeekValues.length]);
        }
        return result;
    }

    /**
     * Usually non-working days of the week are Saturday and Sunday,
     * but depending from culture, customizable non-working days are possible.
     * @param startDate inclusive start date of the range
     * @param endDate inclusive end date of the range
     * 
     * @return the number of working days in a range specified by the startDate inclusive and endDate inclusive.
     */
    @Override
    public long getWorkdays(LocalDate startDate, LocalDate endDate) {
        long rangeDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;  // compensate excluded end date.
        if (0 > rangeDays) {
            throw new IllegalArgumentException("Start date of the range is after the end date.");
        }
        // Regular non working days of the week for all complete weeks in the range.
        long workDays = rangeDays/dayOfWeekValues.length * (dayOfWeekValues.length - weekendSet.size());

        // Days in the remaining incomplete week can be regular weekend days or regular working days.
        Set<DayOfWeek> incompleteWeek = getIncompleteWeek(startDate, rangeDays % dayOfWeekValues.length);
        incompleteWeek.removeAll(weekendSet);
        workDays += incompleteWeek.size();

        // Create a view of the date range portion of all stored holidays.
        // This does not duplicate the date entries underneath, so can be OK with huge volumes.
        Set rangeHolidaySet = holidaySet.subSet(startDate, true, endDate, true);  // binary search in a red–black tree log(n).
        long rangeHolidays = rangeHolidaySet.size();
        long result = workDays - rangeHolidays;
        return result;
    }

}

