package com.saturn.jdbc.support;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import com.saturn.jdbc.util.Extendable;

public abstract class SimpleFieldsIterator extends AbstractFieldsIterator {

    private String[] specialFieldNames;
    private boolean isInclude = false;

    public SimpleFieldsIterator(Object data) {
        super(data);
    }

    public SimpleFieldsIterator(Object data, boolean isInclude, String... specialFieldNames) {
        super(data);
        if (specialFieldNames == null || specialFieldNames.length == 0) {
            this.isInclude = false;
        } else {
            this.specialFieldNames = specialFieldNames;
            this.isInclude = isInclude;
        }
    }

    @Override
    public void afterIteratorDone() {
    }

    /**
     * <pre>
     * 忽略规则：
     * 1.如果是static方法,直接忽略
     * 2.如果是isInclude,除了 specialFieldNames不忽略,其他都忽略
     * 3.如果是isExclude,Extendable注释的忽略,specialFieldNames也忽略
     * </pre>
     */
    @Override
    public boolean ignore(Field field) {
        return ignore(field, isInclude, specialFieldNames);
    }

    public static boolean ignore(Field field, boolean isInclude, String[] specialFieldNames) {
        int mod = field.getModifiers();
        if (Modifier.isFinal(mod)) {
            return true;
        }
        if (Modifier.isStatic(mod)) {
            return true;
        }
        if (isInclude) {
            if (ReflectConvention.isNotContains(field.getName(), specialFieldNames)) {
                return true;
            }
        } else {
            if (field.getAnnotation(Extendable.class) != null) {
                return true;
            }
            if (ReflectConvention.isContains(field.getName(), specialFieldNames)) {
                return true;
            }
        }
        return false;
    }
}
