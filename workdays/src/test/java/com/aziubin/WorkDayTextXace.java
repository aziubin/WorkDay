package com.aziubin;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Unit test for simple App.
 */
public class WorkDayTextXace 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        SecureRandom r = new SecureRandom();
        List<LocalDate> holidays = new ArrayList<>(1000);
        for (int i = 0; i < 1000 + 88; ++i) {
            holidays.add(LocalDate.of(2000 + r.nextInt(23), r.nextInt(12) + 1, r.nextInt(28) + 1));
        }
        //WorkDayInspectable inspectable = new WorkDayInspector(Arrays.asList(new LocalDate[] {LocalDate.now(),}));
        TreeSetWorkDay inspectable = new TreeSetWorkDay(holidays);
        inspectable.delta(LocalDate.of(2011, 6, 15), LocalDate.of(2011, 6, 15));

        DateReader reader = new SecureRandomDateReader(1089);
        try {
            inspectable = new TreeSetWorkDay(reader);
        } catch (DateReaderException e) {
            e.printStackTrace();
        }
        inspectable.delta(LocalDate.of(2011, 6, 15), LocalDate.of(2011, 6, 15));

        try(InputStream iStream = new FileInputStream(new File("C:\\tmp\\holidays.json"));
            OutputStream ostream = new FileOutputStream(new File("C:\\tmp\\holidaysWrite.json"));) { // todo environment parameter
            reader = new InputStreamDateReader(iStream);
            inspectable = new TreeSetWorkDay(reader);
            inspectable.delta(LocalDate.of(2011, 6, 15), LocalDate.of(2011, 6, 15));
            DateWriter writer = new StreamDateWriter(ostream);
            inspectable.setWriter(writer);
            inspectable.save();

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        assertTrue( true );
    }
}
