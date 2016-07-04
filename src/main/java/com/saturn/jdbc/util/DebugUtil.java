package com.saturn.jdbc.util;
public class DebugUtil {

    /**
     * 合并带?的语句
     */
    public static String merge(String sql, Object... args) {
        if (args == null) {
            return sql;
        }
        String[] sqls = (sql + " ").split("\\?");
        if (args.length == sqls.length - 1) {
            StringBuilder exsql = new StringBuilder();
            int i;
            for (i = 0; i < args.length; i++) {
                exsql.append(sqls[i]);
                if (args[i] instanceof String) {
                    String src = args[i].toString().replace("'", "\\'").replace("\r", "").replace("\n", "").trim();
                    String append = src;
                    if (src.length() > 120) {
                        append = src.substring(0, 50) + "...(" + args[i].toString().length() + ")..." + src.substring(src.length() - 50, src.length());
                    }
                    exsql.append("'").append(append).append("'");
                } else {
                    exsql.append(args[i]);
                }
            }
            exsql.append(sqls[i]);
            exsql.deleteCharAt(exsql.length() - 1);
            return exsql.toString();
        }
        return "";
    }
}
