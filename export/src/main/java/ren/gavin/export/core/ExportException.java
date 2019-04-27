package ren.gavin.export.core;

public class ExportException extends RuntimeException {

    public ExportException() {}

    public ExportException(String message) {
        super(message);
    }

    public ExportException(Throwable throwable) {
        super(throwable);
    }

    public ExportException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
