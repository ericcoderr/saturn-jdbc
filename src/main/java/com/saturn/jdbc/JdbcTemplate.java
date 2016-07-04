package com.saturn.jdbc;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.saturn.jdbc.exception.InvalidQueryForObjectException;
import com.saturn.jdbc.support.ReflectConvention;
import com.saturn.jdbc.support.SimpleFieldsIterator;
import com.saturn.util.ReflectConventionUtil;
import com.saturn.util.StringTools;

public class JdbcTemplate extends BasicJdbcTemplate implements JdbcOperations {

    private static final Logger log = LoggerFactory.getLogger(JdbcTemplate.class);
    static boolean useQuestionmark = true;
    private String categoryName;

    public JdbcTemplate(DataSource dataSource) {
        super(dataSource);
    }

    public JdbcTemplate(DataSource dataSource, String categoryName) {
        super(dataSource);
        this.categoryName = categoryName;
    }

    private List<Object> buildQuerySql(final StringBuilder sql, Object data, boolean onlyCount, boolean isInclude, String... specialFieldName) {
        sql.append(onlyCount ? "select count(1) from " : "select * from ").append(getTableName(data)).append(" where 1=1");
        final List<Object> values = useQuestionmark ? new ArrayList<Object>(3) : null;
        new SimpleFieldsIterator(data, isInclude, specialFieldName) {

            @Override
            public void process(Field field) {
                Object obj = ReflectConvention.appendQueryConditionByField(this.getData(), field, sql, "=", null, useQuestionmark);
                if (obj != null && useQuestionmark)
                    values.add(obj);
            }
        }.run();
        return values;
    }

    @Override
    public <T> long countObject(T data) {
        return countObject(data, false, null);
    }

    private <T> long countObject(T data, boolean isInclude, String[] specialFieldName) {
        final StringBuilder sql = new StringBuilder(30);
        List<Object> values = this.buildQuerySql(sql, data, true, isInclude, specialFieldName);
        if (useQuestionmark) {
            return this.queryForLong(sql.toString(), values.toArray());
        }
        return this.queryForLong(sql.toString());
    }

    @Override
    public <T> long countObjectExclude(T data, String... excludeField) {
        return countObject(data, false, excludeField);
    }

    @Override
    public <T> long countObjectInclude(T data, String... includeField) {
        return countObject(data, true, includeField);
    }

    @Override
    public void deleteObject(Class<?> clazz, long... seqids) {
        if (seqids.length == 0)
            return;
        StringBuilder sql = new StringBuilder(30);
        sql.append("delete from ").append(getTableName(clazz)).append(" where seqid in(");
        for (int i = 0; i < seqids.length; i++) {
            sql.append(seqids[i]).append(",");
        }
        sql.deleteCharAt(sql.length() - 1);
        sql.append(")");
        this.execute(sql.toString());
    }

    @Override
    public void deleteObject(Object data) {
        deleteObject(data, false, null);
    }

    private void deleteObject(Object data, boolean isInclude, String[] specialFieldName) {
        final StringBuilder sql = new StringBuilder(30);
        sql.append("delete from ").append(getTableName(data)).append(" where 1=1");
        final List<Object> values = useQuestionmark ? new ArrayList<Object>(3) : null;
        new SimpleFieldsIterator(data, isInclude, specialFieldName) {

            @Override
            public void afterIteratorDone() {
                if (useQuestionmark) {
                    execute(sql.toString(), values.toArray());
                } else {
                    execute(sql.toString());
                }
            }

            @Override
            public void process(Field field) {
                Object obj = ReflectConvention.appendQueryConditionByField(this.getData(), field, sql, "=", null, useQuestionmark);
                if (obj != null && useQuestionmark)
                    values.add(obj);
            }
        }.run();
    }

    @Override
    public void deleteObjectExclude(Object data, String... excludeField) {
        deleteObject(data, false, excludeField);
    }

    @Override
    public void deleteObjectInclude(Object data, String... includeField) {
        deleteObject(data, true, includeField);
    }

    @Override
    public <T> T findObject(T data) {
        return findObject(data, false, null);
    }

    @SuppressWarnings("unchecked")
    private <T> T findObject(T data, boolean isInclude, String[] specialFieldName) {
        final StringBuilder sql = new StringBuilder(30);
        List<Object> values = this.buildQuerySql(sql, data, false, isInclude, specialFieldName);
        if (useQuestionmark) {
            return (T) this.queryForObject(sql.toString(), data.getClass(), values.toArray());
        }
        return (T) this.queryForObject(sql.toString(), data.getClass());
    }

    @Override
    public <T> T findObjectExclude(T data, String... excludeField) {
        return findObject(data, false, excludeField);
    }

    @Override
    public <T> T findObjectInclude(T data, String... includeField) {
        return findObject(data, true, includeField);
    }

    public String getCategoryName() {
        return categoryName;
    }

    @Override
    public <T> T getObject(Class<T> clazz, long seqid) {
        StringBuilder sql = new StringBuilder();
        sql.append("select * from ").append(getTableName(clazz)).append(" where seqid=").append(seqid);
        return this.queryForObject(sql.toString(), clazz);
    }

    public String getTableName(Class<?> clazz) {
        String tablename = clazz.getSimpleName().toLowerCase();
        if (StringTools.isEmpty(categoryName)) {
            return "`" + tablename + "`";
        }
        return "`" + categoryName + "`.`" + tablename + "`";
    }

    public String getTableName(Object data) {
        if (data instanceof ITableName)
            return ((ITableName) data).getTableName();
        String tablename = data.getClass().getSimpleName().toLowerCase();
        if (StringTools.isEmpty(categoryName)) {
            return "`" + tablename + "`";
        }
        return "`" + categoryName + "`.`" + tablename + "`";
    }

    @Override
    public long insertObject(Object data) {
        return insertObject(data, false, null);
    }

    private long insertObject(Object data, boolean isInclude, String[] specialFieldName) {
        final StringBuilder sql = new StringBuilder(30);
        sql.append("insert into ").append(getTableName(data)).append(" (");
        final StringBuilder valuesSql = new StringBuilder();
        valuesSql.append(" values(");
        final List<Object> values = useQuestionmark ? new ArrayList<Object>(3) : null;
        final long[] r = new long[1];
        new SimpleFieldsIterator(data, isInclude, specialFieldName) {

            @Override
            public void afterIteratorDone() {
                int lastindex = sql.length() - 1;
                int lastindex1 = valuesSql.length() - 1;
                if (sql.charAt(lastindex) != ',' || valuesSql.charAt(lastindex1) != ',') {
                    throw new InvalidQueryForObjectException("INSERT fail because the sql dont't set any values :\n\t" + sql.toString() + "\n\t" + valuesSql.toString());
                }
                sql.deleteCharAt(lastindex);
                sql.append(") ");
                valuesSql.deleteCharAt(lastindex1);
                valuesSql.append(")");
                sql.append(valuesSql);
                if (useQuestionmark) {
                    r[0] = insert(sql.toString(), values.toArray());
                } else {
                    r[0] = insert(sql.toString());
                }
            }

            @Override
            public void process(Field field) {
                Object obj = ReflectConvention.appendValuesByField(this.getData(), field, sql, valuesSql, useQuestionmark);
                if (obj != null && useQuestionmark)
                    values.add(obj);
            }
        }.run();
        return r[0];
    }

    @Override
    public long insertObjectExclude(Object data, String... excludeField) {
        return insertObject(data, false, excludeField);
    }

    @Override
    public long insertObjectInclude(Object data, String... includeField) {
        return insertObject(data, true, includeField);
    }

    @Override
    public void insertObjects(Collection<?> datas) {
        insertObjects(null, datas, false);
    }

    private void insertObjects(Map<Object, String[]> datas, Collection<?> datas1, boolean isInclude) {
        // if (useQuestionmark) {
        // ����datas����ʱ,ÿ��sql����ռλ����������ͬ,��˲���ʹ��batchUpdate(final String sql, final List<Object[]> args) ����������
        // throw new NestedRuntimeException("insertObjects dont't support useQuesttionmark pattern");
        // } else {
        boolean isInc = isInclude;
        boolean quick = false;
        if (datas1 != null) {
            if (datas1.isEmpty()) {
                return;
            }
            quick = true;
            isInc = false;
        } else {
            if (datas.isEmpty()) {
                return;
            }
        }
        final List<StringBuilder> sqls = new ArrayList<StringBuilder>();
        final List<StringBuilder> valuesSqls = new ArrayList<StringBuilder>();
        Iterator<?> it = quick ? datas1.iterator() : datas.keySet().iterator();
        while (it.hasNext()) {
            sqls.add(new StringBuilder(30).append("insert into ").append(getTableName(it.next())).append(" ("));
            valuesSqls.add(new StringBuilder(20).append(" values("));
        }
        Iterator<Map.Entry<Object, String[]>> it1 = quick ? null : datas.entrySet().iterator();
        it = quick ? datas1.iterator() : null;
        int len = quick ? datas1.size() : datas.size();
        for (int i = 0; i < len; i++) {
            String[] specialFieldName = null;
            Object dataTmp = null;
            if (quick) {
                dataTmp = it.next();
            } else {
                Map.Entry<Object, String[]> entry = it1.next();
                dataTmp = entry.getKey();
                specialFieldName = entry.getValue();
            }
            final Object data = dataTmp;
            final StringBuilder sql = sqls.get(i);
            final StringBuilder valuesSql = valuesSqls.get(i);
            new SimpleFieldsIterator(data, isInc, specialFieldName) {

                @Override
                public void afterIteratorDone() {
                    int lastindex = sql.length() - 1;
                    int lastindex1 = valuesSql.length() - 1;
                    if (sql.charAt(lastindex) != ',' || valuesSql.charAt(lastindex1) != ',') {
                        throw new InvalidQueryForObjectException("INSERT fail because the sql dont't set any values \n\t" + sql.toString() + "\n\t" + valuesSql.toString());
                    }
                    sql.deleteCharAt(lastindex);
                    sql.append(") ");
                    valuesSql.deleteCharAt(lastindex1);
                    valuesSql.append(")");
                    sql.append(valuesSql);
                }

                @Override
                public void process(Field field) {
                    ReflectConvention.appendValuesByField(this.getData(), field, sql, valuesSql, false);// ֱ�Ӳ�useQuestionmark
                }
            }.run();
        }
        String[] sqlsArray = new String[sqls.size()];
        for (int k = 0; k < sqlsArray.length; k++) {
            sqlsArray[k] = sqls.get(k).toString();
        }
        batchUpdate(sqlsArray);
        // }
    }

    @Override
    public void insertObjectsExclude(Map<Object, String[]> datas) {
        insertObjects(datas, null, false);
    }

    @Override
    public void insertObjectsInclude(Map<Object, String[]> datas) {
        insertObjects(datas, null, true);
    }

    @SuppressWarnings("unused")
    @Deprecated
    private void logExargs(String... exargs) {
        if (log.isDebugEnabled()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < exargs.length; i++) {
                sb.append(i + 1).append("[").append(exargs[i]).append("] ");
            }
            log.debug("exclude {} fields:{}", exargs.length, sb);
        }
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public void updateObject(Object data) {
        updateObject(data, false, null);
    }

    private void updateObject(Object data, boolean isInclude, String[] specialFieldName) {
        final StringBuilder sql = new StringBuilder(50);
        sql.append("update ").append(getTableName(data)).append(" set");
        final List<Object> values = useQuestionmark ? new ArrayList<Object>(3) : null;
        new SimpleFieldsIterator(data, isInclude, specialFieldName) {

            private int seqidValue;

            @Override
            public void afterIteratorDone() {
                int lastindex = sql.length() - 1;
                if (sql.charAt(lastindex) != ',') {
                    throw new InvalidQueryForObjectException("UPDATE fail because the sql dont't set any values :\n\t" + sql.toString());
                }
                if (seqidValue == -1) {
                    throw new InvalidQueryForObjectException("UPDATE fail because the sql dont't provide seqid :\n\t" + sql.toString());
                }
                sql.deleteCharAt(lastindex);
                sql.append(" where seqid=").append(seqidValue);
                if (useQuestionmark) {
                    execute(sql.toString(), values.toArray());
                } else {
                    execute(sql.toString());
                }
            }

            @Override
            public void process(Field field) {
                Object obj = ReflectConvention.appendUpdateValueByField(this.getData(), field, sql, useQuestionmark);
                if (obj != null && useQuestionmark)
                    values.add(obj);
                if (field.getName().equalsIgnoreCase("seqid")) {
                    try {
                        seqidValue = Integer.valueOf(ReflectConventionUtil.getValue(this.getData(), field).toString());
                    } catch (Exception e) {
                    }
                }
            }
        }.run();
    }

    @Override
    public void updateObjectsExclude(Map<Object, String[]> datas) {
        updateObjects(datas, null, false);
    }

    @Override
    public void updateObjectExclude(Object data, String... excludeField) {
        updateObject(data, false, excludeField);
    }

    @Override
    public void updateObjectsInclude(Map<Object, String[]> datas) {
        updateObjects(datas, null, true);
    }

    @Override
    public void updateObjectInclude(Object data, String... includeField) {
        updateObject(data, true, includeField);
    }

    @Override
    public void updateObjects(Collection<?> datas) {
        updateObjects(null, datas, false);
    }

    private void updateObjects(Map<Object, String[]> datas, Collection<?> datas1, boolean isInclude) {
        boolean isInc = isInclude;
        boolean quick = false;
        int len = -1;
        Iterator<?> iterData = null;
        Iterator<?> iterData1 = null;
        Iterator<Map.Entry<Object, String[]>> iterMap = null;
        if (datas1 != null) {
            if (datas1.isEmpty()) {
                return;
            }
            quick = true;
            isInc = false;
            len = datas1.size();
            iterData = datas1.iterator();
            iterData1 = datas1.iterator();
        } else {
            if (datas.isEmpty()) {
                return;
            }
            quick = false;
            len = datas.size();
            iterData = datas.keySet().iterator();
            iterMap = datas.entrySet().iterator();
        }
        final List<StringBuilder> sqls = new ArrayList<StringBuilder>();
        while (iterData.hasNext()) {
            sqls.add(new StringBuilder(50).append("update ").append(getTableName(iterData.next())).append(" set"));
        }
        for (int i = 0; i < len; i++) {
            String[] specialFieldName = null;
            Object dataTmp = null;
            if (quick && null != iterData1) {
                dataTmp = iterData1.next();
            } else if (null != iterMap) {
                Map.Entry<Object, String[]> entry = iterMap.next();
                dataTmp = entry.getKey();
                specialFieldName = entry.getValue();
            }
            final Object data = dataTmp;
            final StringBuilder sql = sqls.get(i);
            new SimpleFieldsIterator(data, isInc, specialFieldName) {

                private int seqidValue;

                @Override
                public void afterIteratorDone() {
                    int lastindex = sql.length() - 1;
                    if (sql.charAt(lastindex) != ',') {
                        throw new InvalidQueryForObjectException("UPDATE fail because the sql dont't set any values :\n\t" + sql.toString());
                    }
                    if (seqidValue == -1) {
                        throw new InvalidQueryForObjectException("UPDATE fail because the sql provide seqid :\n\t" + sql.toString());
                    }
                    sql.deleteCharAt(lastindex);
                    sql.append(" where seqid=").append(seqidValue);
                }

                @Override
                public void process(Field field) {
                    ReflectConvention.appendUpdateValueByField(this.getData(), field, sql, useQuestionmark);
                    if (field.getName().equalsIgnoreCase("seqid")) {
                        try {
                            seqidValue = Integer.valueOf(ReflectConventionUtil.getValue(this.getData(), field).toString());
                        } catch (Exception e) {
                        }
                    }
                }
            }.run();
        }
        String[] sqlsArray = new String[sqls.size()];
        for (int i = 0; i < sqlsArray.length; i++) {
            sqlsArray[i] = sqls.get(i).toString();
        }
        batchUpdate(sqlsArray);
        // }
    }
}
