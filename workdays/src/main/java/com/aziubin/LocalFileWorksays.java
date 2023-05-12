package com.aziubin;

import java.io.OutputStream;
import java.io.InputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Demo implementation of Workdays interface to demonstrate local file save and load.
 * Typically this class will not be widely used, but its parent TreeSetWorksays. 
 */
public class LocalFileWorksays extends TreeSetWorksays implements Workdays {
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

}
