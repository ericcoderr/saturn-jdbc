package com.saturn.jdbc.support;

import java.lang.reflect.Field;

public abstract class AbstractFieldsIterator {

    private Object data;

    public AbstractFieldsIterator(Object data) {
        this.data = data;
    }

    public void run() {

        Field[] fields = data.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (ignore(field))
                continue;
            this.process(field);
        }
        this.afterIteratorDone();
    }

    public Object getData() {
        return data;
    }

    public abstract void process(Field field);

    public abstract boolean ignore(Field field);

    public abstract void afterIteratorDone();
}
