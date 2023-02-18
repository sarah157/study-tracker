package model.exception;

public class EmptyNameException extends InvalidActivityException {
    public EmptyNameException(String msg) {
        super(msg);
    }
}
