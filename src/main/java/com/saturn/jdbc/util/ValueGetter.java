package com.saturn.jdbc.util;

public interface ValueGetter<T> {

    /**
     * 用于将需要添加的值从复杂类型里面get出来
     */
    public Object getValue(T e);
}