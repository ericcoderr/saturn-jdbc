package com.saturn.jdbc.exception;

public class InvalidSetPreparedStatementParamException extends DataAccessException {

    private static final long serialVersionUID = 1L;

    public InvalidSetPreparedStatementParamException(String msg) {
        super(msg);
    }

    public InvalidSetPreparedStatementParamException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
