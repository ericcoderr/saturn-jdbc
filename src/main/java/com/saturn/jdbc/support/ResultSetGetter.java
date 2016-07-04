package com.saturn.jdbc.support;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface ResultSetGetter {

    public Object get(ResultSet rs) throws SQLException;

    public Object get(ResultSet rs, Field field) throws SQLException;
}
