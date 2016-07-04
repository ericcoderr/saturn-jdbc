package com.saturn.jdbc.support;

import java.util.Arrays;

public class QueryKey {

    private Class<?> clazz;
    private String[] args;
    private boolean isInclude;

    public QueryKey(Class<?> clazz, String[] args, boolean isInclude) {
        this.clazz = clazz;
        this.args = args;
        this.isInclude = isInclude;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public String[] getArgs() {
        return args;
    }

    public boolean isInclude() {
        return isInclude;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(args);
        result = prime * result + ((clazz == null) ? 0 : clazz.hashCode());
        result = prime * result + (isInclude ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        QueryKey other = (QueryKey) obj;
        if (!Arrays.equals(args, other.args))
            return false;
        if (clazz == null) {
            if (other.clazz != null)
                return false;
        } else if (!clazz.equals(other.clazz))
            return false;
        if (isInclude != other.isInclude)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "QueryKey[" + clazz.getSimpleName() + (isInclude ? ".include." : ".exclude.") + Arrays.toString(args) + "]";
    }
}
