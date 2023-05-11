package com.aziubin;

/**
 * Custom exception representing inconsistent states of date reader.
 */
class DateReaderException extends Exception {
    private static final long serialVersionUID = -203910526200577225L;

    public DateReaderException() {
        super();
    }

    public DateReaderException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public DateReaderException(String message, Throwable cause) {
        super(message, cause);
    }

    public DateReaderException(String message) {
        super(message);
    }

    public DateReaderException(Throwable cause) {
        super(cause);
    }

}
