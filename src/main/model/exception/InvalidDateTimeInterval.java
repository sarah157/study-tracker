package model.exception;

public class InvalidDateTimeInterval extends RuntimeException {
    public InvalidDateTimeInterval() {
        super("start datetime must be before end datetime");
    }
}

