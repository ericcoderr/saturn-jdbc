package com.saturn.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface RowCallbackHandler {

    public void processRow(ResultSet rs) throws SQLException;
}
