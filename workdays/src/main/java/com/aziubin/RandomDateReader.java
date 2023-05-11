package com.aziubin;

import java.security.SecureRandom;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

class RandomDateReader implements DateReader {
    private static final Logger logger = Logger.getLogger(RandomDateReader.class.getName());
    SecureRandom random;
    int count;

    RandomDateReader(int count) {
        random = new SecureRandom();
        this.count = count; 
    }

    @Override
    public LocalDate read() throws DateReaderException {
        if (--count < 0) {
            return null;
        }
        for (int tryNumber = 0 ; tryNumber < 10 ; ++tryNumber) {
            try {
                return LocalDate.of(2000 + random.nextInt(23), random.nextInt(12) + 1, random.nextInt(31) + 1); // todo 28
            } catch (DateTimeException e) {
                logger.log(Level.FINE, "Random data can not be used to create a correct date.");
            }
        }
        throw new DateReaderException("Predefined number of generation attempts exceeded.");
    }

}
