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

/**
 * Implementation allowing customization of weekend days,
 * which might be necessary for different cultures.
 * Underneath binary tree data structure is used for optimal search
 * within holidays having complexity O(log(n)). 
 */
public class TreeSetWorksays extends AbstractLoadableWorkdays implements Workdays {
    private static final Logger logger = Logger.getLogger(TreeSetWorksays.class.getName());
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

    TreeSetWorksays() {}

    /**
     * Constructs an instance of TreeSetWorksays assuming customized weekend days.
     * @param weekendSet customized set of weekend days.
     */
    TreeSetWorksays(Set<DayOfWeek> weekendSet) {
        this.weekendSet = weekendSet;
    }

    /**
     * Constructs an instance of TreeSetWorksays assuming holiday days
     * provided by the specified reader.
     * @param reader the reader, which provides holiday days.
     */
    TreeSetWorksays(DateReader reader) throws DateReaderException {
        load(reader);
    }

    /**
     * Constructs an instance of TreeSetWorksays assuming holiday days
     * provided by the specified reader and customized weekend days.
     * @param reader the reader, which provides holiday days.
     * @param weekendSet customized set of weekend days.
     */
    TreeSetWorksays(DateReader reader, Set<DayOfWeek> weekendSet) throws DateReaderException {
        this(weekendSet);
        load(reader);
    }

    @Override
    public boolean addHoliday(LocalDate holidayDate) {
        boolean result = false;
        DayOfWeek dayOfWeek = holidayDate.getDayOfWeek();
        if (!weekendSet.contains(dayOfWeek)) {
            result = this.holidaySet.add(holidayDate);
        } else {
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "Holiday is ignored because it overlaps with non-working day of the week: " + holidayDate);
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
        // Regular non-working days of the week for all complete weeks in the range.
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

