package com.saturn.jdbc;

import java.util.List;
import com.saturn.jdbc.exception.DataAccessException;

public interface JdbcQuerierExtend {

    public <T> List<T> queryForListExclude(final String sql, final Class<T> clazz, final Object[] objs, final String... excludeFields) throws DataAccessException;

    public <T> List<T> queryForListExclude(final String sql, final Class<T> clazz, final String... excludeFields) throws DataAccessException;

    public <T> List<T> queryForListInclude(final String sql, final Class<T> clazz, final Object[] objs, final String... includeFields) throws DataAccessException;

    public <T> List<T> queryForListInclude(final String sql, final Class<T> clazz, final String... includeFields) throws DataAccessException;

    public <T> T queryForObjectExclude(final String sql, Class<T> clazz, Object[] objs, final String... excludeFields) throws DataAccessException;

    public <T> T queryForObjectExclude(final String sql, final Class<T> clazz, final String... excludeFields) throws DataAccessException;

    public <T> T queryForObjectInclude(final String sql, Class<T> clazz, Object[] objs, final String... includeFields) throws DataAccessException;

    public <T> T queryForObjectInclude(final String sql, final Class<T> clazz, final String... includeFields) throws DataAccessException;
}
