package com.saturn.jdbc.exception;

public class InvalidQueryForObjectException extends DataAccessException {

    private static final long serialVersionUID = 1L;

    public InvalidQueryForObjectException(String msg) {
        super(msg);
    }

    public InvalidQueryForObjectException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
