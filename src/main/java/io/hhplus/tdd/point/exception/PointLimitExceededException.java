package io.hhplus.tdd.point.exception;

public class PointLimitExceededException extends RuntimeException {
    public PointLimitExceededException(String message) {
        super(message);
    }
}
