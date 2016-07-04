package com.saturn.jdbc.exception;

public abstract class DataAccessException extends NestedRuntimeException {

    private static final long serialVersionUID = 1L;

    public DataAccessException(String msg) {
        super(msg);
    }

    public DataAccessException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
