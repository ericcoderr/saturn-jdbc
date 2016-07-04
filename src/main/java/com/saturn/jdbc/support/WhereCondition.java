package com.saturn.jdbc.support;

public class WhereCondition {

    public static final String PH = "#placeholder#";
    private String condition = "=";
    // 必需
    private String fieldName;
    private String valueTemplate;

    public WhereCondition(String fieldName, String condition, String valueTemplate) {
        this.fieldName = fieldName;
        if (condition != null) {
            this.condition = condition;
        }
        this.valueTemplate = valueTemplate;
    }

    public WhereCondition(String fieldName) {
        this.fieldName = fieldName;
    }

    protected String getCondition() {
        return condition;
    }

    protected String getFieldName() {
        return fieldName;
    }

    protected String getValueTemplate() {
        return valueTemplate;
    }

    protected void setCondition(String condition) {
        this.condition = condition;
    }

    protected void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    protected void setValueTemplate(String valueTemplate) {
        this.valueTemplate = valueTemplate;
    }
}
