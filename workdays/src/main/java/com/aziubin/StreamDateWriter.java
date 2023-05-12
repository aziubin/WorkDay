package com.aziubin;

import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.time.LocalDate;

/**
 * Minimalistic implementation of DateWriter interface,
 * which writes JSON array to the underlying stream.
 * Edge cases or error processing is not covered for the sake of simplicity.
 */
class StreamDateWriter extends AbstractJsonStreamSupport implements DateWriter {
    boolean isBof = true;
    boolean isEof = false;
    OutputStreamWriter writer;

    StreamDateWriter(OutputStream stream) {
        CharsetEncoder encoder = Charset.forName("UTF-8").newEncoder();
        writer = new OutputStreamWriter(stream, encoder);
    }

    private void writeJsonArrayStart() throws IOException {
        writer.write(JSON_ARRAY_START);
    }

    public void writeJsonArrayEnd() throws IOException  {
        writer.write(JSON_ARRAY_END);
    }

    @Override
    public void write(LocalDate date) throws IOException {
        if (isEof) {
            throw new EOFException("Attempt to write over the end of JSON array.");
        } else if (isBof) {
            isBof = false;
            writeJsonArrayStart();
        } else {
            writer.write(JSON_SEPARATOR);
        }
        writer.write(JSON_QUOTE);
        writer.write(date.format(DATE_TIME_FORMATTER));
        writer.write(JSON_QUOTE);
    }

    @Override
    public void commit() throws IOException {
        isEof = true;
        writeJsonArrayEnd();
        writer.flush();
    }

}
