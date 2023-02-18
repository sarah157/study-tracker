package model.exception;

public class InvalidActivityException extends RuntimeException {
    public InvalidActivityException(String msg) {
        super(msg);
    }
}
