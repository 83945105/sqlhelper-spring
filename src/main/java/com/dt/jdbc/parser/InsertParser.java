package com.dt.jdbc.parser;

import com.dt.beans.BeanUtils;
import com.dt.beans.ClassAccessCache;
import com.dt.core.data.ParseData;
import com.esotericsoftware.reflectasm.MethodAccess;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 新增数据解析器
 *
 * @author 白超
 * @version 1.0
 * @since 2018/7/10
 */
public class InsertParser {

    public String insert(String tableName, Map<String, String> columnAliasMap) {
        StringBuilder sql = new StringBuilder(64);
        sql.append("insert into ")
                .append(tableName)
                .append(" (");
        int i = 0;
        for (Map.Entry<String, String> entry : columnAliasMap.entrySet()) {
            if (i++ != 0) {
                sql.append(",");
            }
            sql.append("`").append(entry.getKey()).append("`");
        }
        sql.append(") values (");
        for (; i > 0; i--) {
            if (i == 1) {
                sql.append("?");
            } else {
                sql.append("?,");
            }
        }
        return sql.append(")").toString();
    }

    public ParseData insertMap(String tableName, Map<String, String> columnAliasMap, Map<String, ?> record) {
        StringBuilder sql = new StringBuilder(64);
        ParseData parseData = new ParseData();
        List<Object> args = new ArrayList<>();
        sql.append("insert into ")
                .append(tableName)
                .append(" (");
        int i = 0;
        for (Map.Entry<String, String> entry : columnAliasMap.entrySet()) {
            if (i++ != 0) {
                sql.append(",");
            }
            sql.append("`").append(entry.getKey()).append("`");
            args.add(record.get(entry.getKey()));
        }
        sql.append(") values (");
        for (; i > 0; i--) {
            if (i == 1) {
                sql.append("?");
            } else {
                sql.append("?,");
            }
        }
        parseData.setSql(sql.append(")").toString());
        parseData.setArgs(args);
        return parseData;
    }

    public ParseData insertMapSelective(String tableName, Map<String, String> columnAliasMap, Map<String, ?> record) {
        StringBuilder sql = new StringBuilder(64);
        ParseData parseData = new ParseData();
        List<Object> args = new ArrayList<>();
        sql.append("insert into ")
                .append(tableName)
                .append(" (");
        int i = 0;
        Object value;
        for (Map.Entry<String, String> entry : columnAliasMap.entrySet()) {
            value = record.get(entry.getKey());
            if (value == null) {
                continue;
            }
            if (i++ != 0) {
                sql.append(",");
            }
            sql.append("`").append(entry.getKey()).append("`");
            args.add(value);
        }
        sql.append(") values (");
        for (; i > 0; i--) {
            if (i == 1) {
                sql.append("?");
            } else {
                sql.append("?,");
            }
        }
        parseData.setSql(sql.append(")").toString());
        parseData.setArgs(args);
        return parseData;
    }

    public ParseData insertObject(String tableName, Map<String, String> columnAliasMap, Object record) {
        Class clazz = record.getClass();
        MethodAccess methodAccess = ClassAccessCache.getMethodAccess(clazz);
        StringBuilder sql = new StringBuilder(64);
        ParseData parseData = new ParseData();
        List<Object> args = new ArrayList<>();
        sql.append("insert into ")
                .append(tableName)
                .append(" (");
        int i = 0;
        for (Map.Entry<String, String> entry : columnAliasMap.entrySet()) {
            if (i++ != 0) {
                sql.append(",");
            }
            sql.append("`").append(entry.getKey()).append("`");
            //暂不支持Boolean类型获取Get方法
            args.add(methodAccess.invoke(record, BeanUtils.getGetterMethodName(entry.getValue(), false)));
        }
        sql.append(") values (");
        for (; i > 0; i--) {
            if (i == 1) {
                sql.append("?");
            } else {
                sql.append("?,");
            }
        }
        parseData.setSql(sql.append(")").toString());
        parseData.setArgs(args);
        return parseData;
    }

    public ParseData insertObjectSelective(String tableName, Map<String, String> columnAliasMap, Object record) {
        Class clazz = record.getClass();
        MethodAccess methodAccess = ClassAccessCache.getMethodAccess(clazz);
        StringBuilder sql = new StringBuilder(64);
        ParseData parseData = new ParseData();
        List<Object> args = new ArrayList<>();
        sql.append("insert into ")
                .append(tableName)
                .append(" (");
        int i = 0;
        Object value;
        for (Map.Entry<String, String> entry : columnAliasMap.entrySet()) {
            //暂不支持Boolean类型获取Get方法
            value = methodAccess.invoke(record, BeanUtils.getGetterMethodName(entry.getValue(), false));
            if (value == null) {
                continue;
            }
            if (i++ != 0) {
                sql.append(",");
            }
            sql.append("`").append(entry.getKey()).append("`");
            args.add(value);
        }
        sql.append(") values (");
        for (; i > 0; i--) {
            if (i == 1) {
                sql.append("?");
            } else {
                sql.append("?,");
            }
        }
        parseData.setSql(sql.append(")").toString());
        parseData.setArgs(args);
        return parseData;
    }

    public String batchInsert(String tableName, Map<String, String> columnAliasMap, int recordSize) {
        StringBuilder sql = new StringBuilder(64);
        sql.append("insert into ")
                .append(tableName)
                .append(" (");
        int i = 0;
        for (Map.Entry<String, String> entry : columnAliasMap.entrySet()) {
            if (i++ != 0) {
                sql.append(",");
            }
            sql.append("`").append(entry.getKey()).append("`");
        }
        sql.append(") values ");
        StringBuilder values = new StringBuilder(32).append("(");
        for (; i > 0; i--) {
            if (i == 1) {
                values.append("?)");
            } else {
                values.append("?,");
            }
        }
        for (; recordSize > 0; recordSize--) {
            if (recordSize == 1) {
                sql.append(values.toString());
            } else {
                sql.append(values.toString()).append(",");
            }
        }
        return sql.toString();
    }

}
