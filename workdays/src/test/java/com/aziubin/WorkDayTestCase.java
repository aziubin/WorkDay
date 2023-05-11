package com.aziubin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Unit test for workday library.
 */
public class WorkDayTestCase 
{
    private static final int NUMBER_OF_HOLIDAYS = 1089;

    @Test
    public void testRandomSaveLoad() throws DateReaderException, IOException
    {
        DateReader reader = new RandomDateReader(NUMBER_OF_HOLIDAYS);
        TreeSetWorkDay workDaySave = new TreeSetWorkDay(reader);

        ByteArrayOutputStream oStream = new ByteArrayOutputStream(NUMBER_OF_HOLIDAYS * 14);
        DateWriter writer = new StreamDateWriter(oStream);
        workDaySave.save(writer);

        ByteArrayInputStream iStream = new ByteArrayInputStream(oStream.toByteArray());
        DateReader streamDateReader = new InputStreamDateReader(iStream);
        TreeSetWorkDay workDayLoad = new TreeSetWorkDay(streamDateReader);
        assertTrue(workDayLoad.holidaySet.equals(workDaySave.holidaySet));
    }

    @Test
    public void testJsonReader() throws DateReaderException, IOException
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
        assertEquals(dateReader.read(), LocalDate.of(2001, 12, 21));
        assertEquals(dateReader.read(), LocalDate.of(2000, 12, 23));
        assertEquals(dateReader.read(), LocalDate.of(2015, 12, 24));
        assertEquals(dateReader.read(), LocalDate.of(2000, 11, 25));
        assertEquals(dateReader.read(), LocalDate.of(2000, 12, 26));
    }

    @Test
    public void testJsonWriter() throws DateReaderException, IOException
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
        assertEquals(jsonStr, "[\"2001-12-21\",\"2015-12-21\",\"2003-12-21\",\"2011-12-21\",\"2001-12-21\"]");
    }

    @Test
    public void shouldAnswerWithTrue()
    {
        assertTrue( true );
    }

}
