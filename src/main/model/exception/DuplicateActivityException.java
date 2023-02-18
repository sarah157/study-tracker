package model.exception;

public class DuplicateActivityException extends InvalidActivityException {
    public DuplicateActivityException(String msg) {
        super(msg);
    }
}
