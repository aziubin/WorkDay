package com.aziubin;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.EnumSet;
import java.security.SecureRandom;
import org.junit.jupiter.api.Test;

/**
 * Unit test for workday library.
 */
public class WorkDayTestCase 
{
    private static final int NUMBER_OF_HOLIDAYS = 1089;
    private static final int NUMBER_OF_RANDOM_TEST = 1000;

    @Test
    public void eachDayHoliday () throws DateReaderException
    {
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < NUMBER_OF_RANDOM_TEST; i++) {
            try {
                LocalDate holidayStartDate = RandomDateReader.getRandomDate(random);
                LocalDate holidayFinishDate = RandomDateReader.getRandomDate(random);
                if (holidayStartDate.isAfter(holidayFinishDate)) {
                    LocalDate date = holidayStartDate;
                    holidayStartDate = holidayFinishDate;
                    holidayFinishDate = date;
                }
                DateReader reader = new SequentialDateReader(holidayStartDate, holidayFinishDate);
                TreeSetWorkDay workDay = new TreeSetWorkDay(reader);
                assertEquals(0, workDay.getWorkdays(holidayStartDate, holidayFinishDate));
            } catch (DateTimeException e) {
            }
        }
    }

    @Test
    public void eachDayWeekEnd () throws DateReaderException
    {
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < NUMBER_OF_RANDOM_TEST; i++) {
            try {
                LocalDate holidayStartDate = RandomDateReader.getRandomDate(random);
                LocalDate holidayFinishDate = RandomDateReader.getRandomDate(random);
                if (holidayStartDate.isAfter(holidayFinishDate)) {
                    LocalDate date = holidayStartDate;
                    holidayStartDate = holidayFinishDate;
                    holidayFinishDate = date;
                }
                DateReader reader = new SequentialDateReader(holidayStartDate, holidayFinishDate);
                TreeSetWorkDay workDay = new TreeSetWorkDay(reader, EnumSet.allOf(DayOfWeek.class));

                LocalDate rangeStartDate = RandomDateReader.getRandomDate(random);
                LocalDate rangeFinishDate = RandomDateReader.getRandomDate(random);

                assertEquals(0, workDay.getWorkdays(holidayStartDate, holidayFinishDate));
            } catch (DateTimeException e) {
            }
        }
    }

    @Test
    public void RandomSaveLoad() throws DateReaderException, IOException
    {
        DateReader reader = new RandomDateReader(NUMBER_OF_HOLIDAYS);
        TreeSetWorkDay workDaySave = new TreeSetWorkDay(reader);

        ByteArrayOutputStream oStream = new ByteArrayOutputStream(NUMBER_OF_HOLIDAYS * 14);
        DateWriter writer = new StreamDateWriter(oStream);
        workDaySave.save(writer);

        ByteArrayInputStream iStream = new ByteArrayInputStream(oStream.toByteArray());
        DateReader streamDateReader = new InputStreamDateReader(iStream);
        TreeSetWorkDay workDayLoad = new TreeSetWorkDay(streamDateReader);
        assertEquals(workDaySave.holidaySet, workDayLoad.holidaySet);
    }

    @Test
    public void jsonReader() throws DateReaderException, IOException
    {
        String jsonStr = "\r\n"
                + "[ \"2001-12-21\",\"2000-12-23\"\r\n"
                + "\r\n"
                + ",\"2015-12-24\"\r\n"
                + ",\"2000-11-25\"\r\n"
                + "\t            ,   \"2000-12-26\"\r\n"
                + "]\r\n"
                + "";

        InputStream iStream = new ByteArrayInputStream( jsonStr.getBytes() );
        DateReader dateReader = new InputStreamDateReader(iStream);
        assertEquals(LocalDate.of(2001, 12, 21), dateReader.read());
        assertEquals(LocalDate.of(2000, 12, 23), dateReader.read());
        assertEquals(LocalDate.of(2015, 12, 24), dateReader.read());
        assertEquals(LocalDate.of(2000, 11, 25), dateReader.read());
        assertEquals(LocalDate.of(2000, 12, 26), dateReader.read());
    }

    @Test
    public void jsonWriter() throws DateReaderException, IOException
    {
        ByteArrayOutputStream oStream = new ByteArrayOutputStream();
        DateWriter writer = new StreamDateWriter(oStream);
        writer.write(LocalDate.of(2001, 12, 21));
        writer.write(LocalDate.of(2015, 12, 21));
        writer.write(LocalDate.of(2003, 12, 21));
        writer.write(LocalDate.of(2011, 12, 21));
        writer.write(LocalDate.of(2001, 12, 21));
        writer.setEnd();
        String jsonStr = oStream.toString();
        assertEquals("[\"2001-12-21\",\"2015-12-21\",\"2003-12-21\",\"2011-12-21\",\"2001-12-21\"]", jsonStr);
    }

}
