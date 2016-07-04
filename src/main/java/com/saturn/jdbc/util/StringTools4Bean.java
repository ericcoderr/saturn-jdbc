package com.saturn.jdbc.util;

import com.saturn.jdbc.support.JdbcUtils;

public class StringTools4Bean {

    public static String listingObject(Object data) {
        if (data == null) {
            return "";
        }
        if (JdbcUtils.isSimpleClass(data.getClass())) {
            return data.getClass().getSimpleName() + "[" + data + "]";
        }
        return listingString(data, true);
    }

    private static String listingString(Object data, boolean snapped) {
        StringBuilder sb = new StringBuilder(100);
        sb.append(data.getClass().getSimpleName()).append("[");
        try {
            boolean flag = false;
            boolean isstring = true;
            Object obj = null;
            String str = "";
            for (java.lang.reflect.Method m : data.getClass().getDeclaredMethods()) {
                if ((m.getName().startsWith("get") || m.getName().startsWith("is")) && m.getParameterTypes().length == 0) {
                    int l = m.getName().startsWith("get") ? 3 : 2;
                    obj = m.invoke(data);
                    if (snapped && obj == null)
                        continue;
                    isstring = obj instanceof String;
                    if (!isstring && snapped) {
                        if (obj instanceof Number && ((Number) obj).intValue() == 0)
                            continue;
                        if (obj instanceof Boolean && ((Boolean) obj) == false)
                            continue;
                    }
                    str = isstring ? ("\"" + obj + "\"") : String.valueOf(obj);
                    if (flag)
                        sb.append(", ");
                    sb.append(m.getName().substring(l).toLowerCase()).append("=").append(str);
                    flag = true;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        sb.append("]");
        return sb.toString();
    }
}
