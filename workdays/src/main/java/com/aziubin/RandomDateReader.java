package com.aziubin;

import java.security.SecureRandom;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Date reader implementation, which generates specified number of random dates for testing purposes.
 * Not each randomly generated date can be correct LocalDate, so more than one attempt can be necessary.
 */
class RandomDateReader implements DateReader {
    private static final Logger logger = Logger.getLogger(RandomDateReader.class.getName());
    SecureRandom random  = new SecureRandom();
    int count;

    RandomDateReader(int count) {
        this.count = count; 
    }
    
    public static LocalDate getRandomDate(SecureRandom random) {
        return LocalDate.of(2000 + random.nextInt(23), random.nextInt(12) + 1, random.nextInt(31) + 1); // todo 28
    }

    @Override
    public LocalDate read() throws DateReaderException {
        if (--count < 0) {
            return null;
        }
        for (int tryNumber = 0 ; tryNumber < 10 ; ++tryNumber) {
            try {
                return getRandomDate(random);
            } catch (DateTimeException e) {
                logger.log(Level.FINE, "Random data can not be used to create a correct date.");
            }
        }
        throw new DateReaderException("Predefined number of generation attempts exceeded.");
    }

}
