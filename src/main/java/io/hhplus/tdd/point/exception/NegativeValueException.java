package io.hhplus.tdd.point.exception;

public class NegativeValueException extends IllegalArgumentException {
    public NegativeValueException(String message) {
        super(message);
    }
}
