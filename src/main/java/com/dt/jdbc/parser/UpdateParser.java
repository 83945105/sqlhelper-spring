package com.dt.jdbc.parser;

import com.dt.beans.BeanUtils;
import com.dt.beans.ClassAccessCache;
import com.dt.core.data.ParseData;
import com.dt.core.engine.WhereEngine;
import com.esotericsoftware.reflectasm.MethodAccess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 更新数据解析器
 *
 * @author 白超
 * @version 1.0
 * @since 2018/7/10
 */
public class UpdateParser {

    public String updateByPrimaryKey(String tableName, String primaryKeyName, Map<String, String> columnAliasMap) {
        StringBuilder sql = new StringBuilder(64);
        sql.append("update ")
                .append(tableName)
                .append(" set ");
        int i = 0;
        for (Map.Entry<String, String> entry : columnAliasMap.entrySet()) {
            if (entry.getKey().equals(primaryKeyName)) {
                continue;
            }
            if (i++ != 0) {
                sql.append(",");
            }
            sql.append("`").append(entry.getKey()).append("`").append(" = ?");
        }
        return sql.append(" where ").append(primaryKeyName).append(" = ?").toString();
    }

    public ParseData updateMapByPrimaryKey(String tableName, String primaryKeyName, Object primaryKeyValue, Map<String, String> columnAliasMap, Map<String, ?> record) {
        StringBuilder sql = new StringBuilder(64);
        ParseData parseData = new ParseData();
        List<Object> args = new ArrayList<>();
        sql.append("update ")
                .append(tableName)
                .append(" set ");
        int i = 0;
        for (Map.Entry<String, String> entry : columnAliasMap.entrySet()) {
            if (i++ != 0) {
                sql.append(",");
            }
            sql.append("`").append(entry.getKey()).append("`").append(" = ?");
            args.add(record.get(entry.getKey()));
        }
        args.add(primaryKeyValue);
        parseData.setSql(sql.append(" where ").append(primaryKeyName).append(" = ?").toString());
        parseData.setArgs(args);
        return parseData;
    }

    public ParseData updateMapByPrimaryKeySelective(String tableName, String primaryKeyName, Object primaryKeyValue, Map<String, String> columnAliasMap, Map<String, ?> record) {
        StringBuilder sql = new StringBuilder(64);
        ParseData parseData = new ParseData();
        List<Object> args = new ArrayList<>();
        sql.append("update ")
                .append(tableName)
                .append(" set ");
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
            sql.append("`").append(entry.getKey()).append("`").append(" = ?");
            args.add(value);
        }
        args.add(primaryKeyValue);
        parseData.setSql(sql.append(" where ").append(primaryKeyName).append(" = ?").toString());
        parseData.setArgs(args);
        return parseData;
    }

    public ParseData updateObjectByPrimaryKey(String tableName, String primaryKeyName, Object primaryKeyValue, Map<String, String> columnAliasMap, Object record) {
        Class clazz = record.getClass();
        MethodAccess methodAccess = ClassAccessCache.getMethodAccess(clazz);
        StringBuilder sql = new StringBuilder(64);
        ParseData parseData = new ParseData();
        List<Object> args = new ArrayList<>();
        sql.append("update ")
                .append(tableName)
                .append(" set ");
        int i = 0;
        for (Map.Entry<String, String> entry : columnAliasMap.entrySet()) {
            if (i++ != 0) {
                sql.append(",");
            }
            sql.append("`").append(entry.getKey()).append("`").append(" = ?");
            //暂不支持Boolean类型获取Get方法
            args.add(methodAccess.invoke(record, BeanUtils.getGetterMethodName(entry.getValue(), false)));
        }
        args.add(primaryKeyValue);
        parseData.setSql(sql.append(" where ").append(primaryKeyName).append(" = ?").toString());
        parseData.setArgs(args);
        return parseData;
    }

    public ParseData updateObjectByPrimaryKeySelective(String tableName, String primaryKeyName, Object primaryKeyValue, Map<String, String> columnAliasMap, Object record) {
        Class clazz = record.getClass();
        MethodAccess methodAccess = ClassAccessCache.getMethodAccess(clazz);
        StringBuilder sql = new StringBuilder(64);
        ParseData parseData = new ParseData();
        List<Object> args = new ArrayList<>();
        sql.append("update ")
                .append(tableName)
                .append(" set ");
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
            sql.append("`").append(entry.getKey()).append("`").append(" = ?");
            args.add(value);
        }
        args.add(primaryKeyValue);
        parseData.setSql(sql.append(" where ").append(primaryKeyName).append(" = ?").toString());
        parseData.setArgs(args);
        return parseData;
    }

    @SuppressWarnings("unchecked")
    public ParseData updateMap(Map<String, ?> record, WhereEngine whereEngine) {
        StringBuilder sql = new StringBuilder(64);
        ParseData parseData;
        List<Object> args = new ArrayList<>();
        String tableName = whereEngine.getTableName();
        String tableAlias = whereEngine.getTableAlias();
        sql.append("update ")
                .append(tableName)
                .append(" ")
                .append(tableAlias);
        parseData = whereEngine.getJoinParseData();
        if (parseData != null && parseData.getSql() != null) {
            sql.append(" ").append(parseData.getSql());
            args.addAll(parseData.getArgs());
        }
        sql.append(" set ");
        int i = 0;
        Map<String, String> columnAliasMap = whereEngine.getColumnAliasMap();
        if (columnAliasMap.size() == 0) {
            columnAliasMap = whereEngine.getTable().getColumnAliasMap();
        }
        for (Map.Entry<String, String> entry : columnAliasMap.entrySet()) {
            if (i++ != 0) {
                sql.append(",");
            }
            sql.append(tableAlias).append(".`").append(entry.getKey()).append("`").append(" = ?");
            args.add(record.get(entry.getKey()));
        }
        parseData = whereEngine.getWhereParseData();
        if (parseData != null && parseData.getSql() != null) {
            sql.append(" ").append(parseData.getSql());
            args.addAll(parseData.getArgs());
        }
        parseData = new ParseData();
        parseData.setSql(sql.toString());
        parseData.setArgs(args);
        return parseData;
    }

    @SuppressWarnings("unchecked")
    public ParseData updateObject(Object record, WhereEngine whereEngine) {
        Class clazz = record.getClass();
        MethodAccess methodAccess = ClassAccessCache.getMethodAccess(clazz);
        StringBuilder sql = new StringBuilder(64);
        ParseData parseData;
        List<Object> args = new ArrayList<>();
        String tableName = whereEngine.getTableName();
        String tableAlias = whereEngine.getTableAlias();
        sql.append("update ")
                .append(tableName)
                .append(" ")
                .append(tableAlias);
        parseData = whereEngine.getJoinParseData();
        if (parseData != null && parseData.getSql() != null) {
            sql.append(" ").append(parseData.getSql());
            args.addAll(parseData.getArgs());
        }
        sql.append(" set ");
        int i = 0;
        Map<String, String> columnAliasMap = whereEngine.getColumnAliasMap();
        if (columnAliasMap.size() == 0) {
            columnAliasMap = whereEngine.getTable().getColumnAliasMap();
        }
        for (Map.Entry<String, String> entry : columnAliasMap.entrySet()) {
            if (i++ != 0) {
                sql.append(",");
            }
            sql.append(tableAlias).append(".`").append(entry.getKey()).append("`").append(" = ?");
            //暂不支持Boolean类型获取Get方法
            args.add(methodAccess.invoke(record, BeanUtils.getGetterMethodName(entry.getValue(), false)));
        }
        parseData = whereEngine.getWhereParseData();
        if (parseData != null && parseData.getSql() != null) {
            sql.append(" ").append(parseData.getSql());
            args.addAll(parseData.getArgs());
        }
        parseData = new ParseData();
        parseData.setSql(sql.toString());
        parseData.setArgs(args);
        return parseData;
    }

    @SuppressWarnings("unchecked")
    public ParseData updateMapSelective(Map<String, ?> record, WhereEngine whereEngine) {
        StringBuilder sql = new StringBuilder(64);
        ParseData parseData;
        List<Object> args = new ArrayList<>();
        String tableName = whereEngine.getTableName();
        String tableAlias = whereEngine.getTableAlias();
        sql.append("update ")
                .append(tableName)
                .append(" ")
                .append(tableAlias);
        parseData = whereEngine.getJoinParseData();
        if (parseData != null && parseData.getSql() != null) {
            sql.append(" ").append(parseData.getSql());
            args.addAll(parseData.getArgs());
        }
        sql.append(" set ");
        int i = 0;
        Object value;
        Map<String, String> columnAliasMap = whereEngine.getColumnAliasMap();
        if (columnAliasMap.size() == 0) {
            columnAliasMap = whereEngine.getTable().getColumnAliasMap();
        }
        for (Map.Entry<String, String> entry : columnAliasMap.entrySet()) {
            value = record.get(entry.getKey());
            if (value == null) {
                continue;
            }
            if (i++ != 0) {
                sql.append(",");
            }
            sql.append(tableAlias).append(".`").append(entry.getKey()).append("`").append(" = ?");
            args.add(value);
        }
        parseData = whereEngine.getWhereParseData();
        if (parseData != null && parseData.getSql() != null) {
            sql.append(" ").append(parseData.getSql());
            args.addAll(parseData.getArgs());
        }
        parseData = new ParseData();
        parseData.setSql(sql.toString());
        parseData.setArgs(args);
        return parseData;
    }

    @SuppressWarnings("unchecked")
    public ParseData updateObjectSelective(Object record, WhereEngine whereEngine) {
        Class clazz = record.getClass();
        MethodAccess methodAccess = ClassAccessCache.getMethodAccess(clazz);
        StringBuilder sql = new StringBuilder(64);
        ParseData parseData;
        List<Object> args = new ArrayList<>();
        String tableName = whereEngine.getTableName();
        String tableAlias = whereEngine.getTableAlias();
        sql.append("update ")
                .append(tableName)
                .append(" ")
                .append(tableAlias);
        parseData = whereEngine.getJoinParseData();
        if (parseData != null && parseData.getSql() != null) {
            sql.append(" ").append(parseData.getSql());
            args.addAll(parseData.getArgs());
        }
        sql.append(" set ");
        int i = 0;
        Object value;
        Map<String, String> columnAliasMap = whereEngine.getColumnAliasMap();
        if (columnAliasMap.size() == 0) {
            columnAliasMap = whereEngine.getTable().getColumnAliasMap();
        }
        for (Map.Entry<String, String> entry : columnAliasMap.entrySet()) {
            //暂不支持Boolean类型获取Get方法
            value = methodAccess.invoke(record, BeanUtils.getGetterMethodName(entry.getValue(), false));
            if (value == null) {
                continue;
            }
            if (i++ != 0) {
                sql.append(",");
            }
            sql.append(tableAlias).append(".`").append(entry.getKey()).append("`");
            sql.append(" = ?");
            args.add(value);
        }
        parseData = whereEngine.getWhereParseData();
        if (parseData != null && parseData.getSql() != null) {
            sql.append(" ").append(parseData.getSql());
            args.addAll(parseData.getArgs());
        }
        parseData = new ParseData();
        parseData.setSql(sql.toString());
        parseData.setArgs(args);
        return parseData;
    }

    public ParseData batchUpdateByPrimaryKeys(String tableName, String primaryKeyName, String primaryKeyAlias, Map<String, String> columnAliasMap, Object[] records) {
        MethodAccess methodAccess;
        StringBuilder sql = new StringBuilder(128);
        List<Object> args = new ArrayList<>();
        sql.append("update ")
                .append(tableName)
                .append(" set ");

        StringBuilder when = new StringBuilder(64);
        StringBuilder in = new StringBuilder(32);
        List<Object> inArgs = new ArrayList<>();
        Object keyValue;
        int i = 0;
        for (Object record : records) {
            if (record instanceof Map) {
                keyValue = ((Map) record).get(primaryKeyName);
                if (i++ != 0) {
                    in.append(",");
                }
                in.append("?");
                inArgs.add(keyValue);
                for (Map.Entry<String, String> entry : columnAliasMap.entrySet()) {
                    if (entry.getKey().equals(primaryKeyName)) {
                        continue;
                    }
                    args.add(((Map) record).get(entry.getKey()));
                }
            } else {
                Class clazz = record.getClass();
                methodAccess = ClassAccessCache.getMethodAccess(clazz);
                keyValue = methodAccess.invoke(record, BeanUtils.getGetterMethodName(primaryKeyAlias, false));
                if (i++ != 0) {
                    in.append(",");
                }
                in.append("?");
                inArgs.add(keyValue);
                for (Map.Entry<String, String> entry : columnAliasMap.entrySet()) {
                    if (entry.getKey().equals(primaryKeyName)) {
                        continue;
                    }
                    args.add(methodAccess.invoke(record, BeanUtils.getGetterMethodName(entry.getValue(), false)));
                }
            }
            when.append("when '").append(keyValue).append("' then ? ");
        }
        i = 0;
        for (Map.Entry<String, String> entry : columnAliasMap.entrySet()) {
            if (entry.getKey().equals(primaryKeyName)) {
                continue;
            }
            if (i++ != 0) {
                sql.append(",");
            }
            sql.append("`")
                    .append(entry.getKey())
                    .append("`=case `")
                    .append(primaryKeyName)
                    .append("` ")
                    .append(when)
                    .append(" end");
        }
        sql.append(" where `").append(primaryKeyName).append("` in (").append(in).append(")");
        args.addAll(inArgs);
        ParseData parseData = new ParseData();
        parseData.setSql(sql.toString());
        parseData.setArgs(args);
        return parseData;
    }

    public ParseData batchUpdateByPrimaryKeys(String tableName, String primaryKeyName, String primaryKeyAlias, Map<String, String> columnAliasMap, Collection<?> records) {
        MethodAccess methodAccess;
        StringBuilder sql = new StringBuilder(128);
        List<Object> args = new ArrayList<>();
        sql.append("update ")
                .append(tableName)
                .append(" set ");

        StringBuilder when = new StringBuilder(64);
        StringBuilder in = new StringBuilder(32);
        List<Object> inArgs = new ArrayList<>();
        Object keyValue;
        int i = 0;
        for (Object record : records) {
            if (record instanceof Map) {
                keyValue = ((Map) record).get(primaryKeyName);
                if (i++ != 0) {
                    in.append(",");
                }
                in.append("?");
                inArgs.add(keyValue);
                for (Map.Entry<String, String> entry : columnAliasMap.entrySet()) {
                    if (entry.getKey().equals(primaryKeyName)) {
                        continue;
                    }
                    args.add(((Map) record).get(entry.getKey()));
                }
            } else {
                Class clazz = record.getClass();
                methodAccess = ClassAccessCache.getMethodAccess(clazz);
                keyValue = methodAccess.invoke(record, BeanUtils.getGetterMethodName(primaryKeyAlias, false));
                if (i++ != 0) {
                    in.append(",");
                }
                in.append("?");
                inArgs.add(keyValue);
                for (Map.Entry<String, String> entry : columnAliasMap.entrySet()) {
                    if (entry.getKey().equals(primaryKeyName)) {
                        continue;
                    }
                    args.add(methodAccess.invoke(record, BeanUtils.getGetterMethodName(entry.getValue(), false)));
                }
            }
            when.append("when '").append(keyValue).append("' then ? ");
        }
        i = 0;
        for (Map.Entry<String, String> entry : columnAliasMap.entrySet()) {
            if (entry.getKey().equals(primaryKeyName)) {
                continue;
            }
            if (i++ != 0) {
                sql.append(",");
            }
            sql.append("`")
                    .append(entry.getKey())
                    .append("`=case `")
                    .append(primaryKeyName)
                    .append("` ")
                    .append(when)
                    .append(" end");
        }
        sql.append(" where `").append(primaryKeyName).append("` in (").append(in).append(")");
        args.addAll(inArgs);
        ParseData parseData = new ParseData();
        parseData.setSql(sql.toString());
        parseData.setArgs(args);
        return parseData;
    }

    @SuppressWarnings("unchecked")
    public ParseData batchUpdateByPrimaryKeys(Object[] records, WhereEngine whereEngine) {
        ParseData parseData;
        String tableName = whereEngine.getTableName();
        String tableAlias = whereEngine.getTableAlias();
        String primaryKeyName = whereEngine.getPrimaryKeyName();
        String primaryKeyAlias = whereEngine.getPrimaryKeyAlias();
        Map<String, String> columnAliasMap = whereEngine.getColumnAliasMap();
        if (columnAliasMap.size() == 0) {
            columnAliasMap = whereEngine.getTable().getColumnAliasMap();
        }
        MethodAccess methodAccess;
        StringBuilder sql = new StringBuilder(128);
        List<Object> args = new ArrayList<>();
        sql.append("update ")
                .append(tableName).append(" ").append(tableAlias);
        parseData = whereEngine.getJoinParseData();
        if (parseData != null && parseData.getSql() != null) {
            sql.append(" ").append(parseData.getSql());
            args.addAll(parseData.getArgs());
        }
        sql.append(" set ");
        StringBuilder when = new StringBuilder(64);
        StringBuilder in = new StringBuilder(32);
        List<Object> inArgs = new ArrayList<>();
        Object keyValue;
        int i = 0;
        for (Object record : records) {
            if (record instanceof Map) {
                keyValue = ((Map) record).get(primaryKeyName);
                if (i++ != 0) {
                    in.append(",");
                }
                in.append("?");
                inArgs.add(keyValue);
                for (Map.Entry<String, String> entry : columnAliasMap.entrySet()) {
                    if (entry.getKey().equals(primaryKeyName)) {
                        continue;
                    }
                    args.add(((Map) record).get(entry.getKey()));
                }
            } else {
                Class clazz = record.getClass();
                methodAccess = ClassAccessCache.getMethodAccess(clazz);
                keyValue = methodAccess.invoke(record, BeanUtils.getGetterMethodName(primaryKeyAlias, false));
                if (i++ != 0) {
                    in.append(",");
                }
                in.append("?");
                inArgs.add(keyValue);
                for (Map.Entry<String, String> entry : columnAliasMap.entrySet()) {
                    if (entry.getKey().equals(primaryKeyName)) {
                        continue;
                    }
                    args.add(methodAccess.invoke(record, BeanUtils.getGetterMethodName(entry.getValue(), false)));
                }
            }
            when.append("when '").append(keyValue).append("' then ? ");
        }
        i = 0;
        for (Map.Entry<String, String> entry : columnAliasMap.entrySet()) {
            if (entry.getKey().equals(primaryKeyName)) {
                continue;
            }
            if (i++ != 0) {
                sql.append(",");
            }
            sql.append(tableAlias)
                    .append(".`")
                    .append(entry.getKey())
                    .append("`=case ")
                    .append(tableAlias)
                    .append(".`")
                    .append(primaryKeyName)
                    .append("` ")
                    .append(when)
                    .append(" end");
        }
        sql.append(" where ").append(tableAlias).append(".`").append(primaryKeyName).append("` in (").append(in).append(")");
        args.addAll(inArgs);
        parseData = whereEngine.getWhereParseData();
        if (parseData != null && parseData.getSql() != null) {
            sql.append(" and ").append(parseData.getSql());
            args.addAll(parseData.getArgs());
        }
        parseData = new ParseData();
        parseData.setSql(sql.toString());
        parseData.setArgs(args);
        return parseData;
    }

    @SuppressWarnings("unchecked")
    public ParseData batchUpdateByPrimaryKeys(Collection<?> records, WhereEngine whereEngine) {
        ParseData parseData;
        String tableName = whereEngine.getTableName();
        String tableAlias = whereEngine.getTableAlias();
        String primaryKeyName = whereEngine.getPrimaryKeyName();
        String primaryKeyAlias = whereEngine.getPrimaryKeyAlias();
        Map<String, String> columnAliasMap = whereEngine.getColumnAliasMap();
        if (columnAliasMap.size() == 0) {
            columnAliasMap = whereEngine.getTable().getColumnAliasMap();
        }
        MethodAccess methodAccess;
        StringBuilder sql = new StringBuilder(128);
        List<Object> args = new ArrayList<>();
        sql.append("update ")
                .append(tableName).append(" ").append(tableAlias);
        parseData = whereEngine.getJoinParseData();
        if (parseData != null && parseData.getSql() != null) {
            sql.append(" ").append(parseData.getSql());
            args.addAll(parseData.getArgs());
        }
        sql.append(" set ");
        StringBuilder when = new StringBuilder(64);
        StringBuilder in = new StringBuilder(32);
        List<Object> inArgs = new ArrayList<>();
        Object keyValue;
        int i = 0;
        for (Object record : records) {
            if (record instanceof Map) {
                keyValue = ((Map) record).get(primaryKeyName);
                if (i++ != 0) {
                    in.append(",");
                }
                in.append("?");
                inArgs.add(keyValue);
                for (Map.Entry<String, String> entry : columnAliasMap.entrySet()) {
                    if (entry.getKey().equals(primaryKeyName)) {
                        continue;
                    }
                    args.add(((Map) record).get(entry.getKey()));
                }
            } else {
                Class clazz = record.getClass();
                methodAccess = ClassAccessCache.getMethodAccess(clazz);
                keyValue = methodAccess.invoke(record, BeanUtils.getGetterMethodName(primaryKeyAlias, false));
                if (i++ != 0) {
                    in.append(",");
                }
                in.append("?");
                inArgs.add(keyValue);
                for (Map.Entry<String, String> entry : columnAliasMap.entrySet()) {
                    if (entry.getKey().equals(primaryKeyName)) {
                        continue;
                    }
                    args.add(methodAccess.invoke(record, BeanUtils.getGetterMethodName(entry.getValue(), false)));
                }
            }
            when.append("when '").append(keyValue).append("' then ? ");
        }
        i = 0;
        for (Map.Entry<String, String> entry : columnAliasMap.entrySet()) {
            if (entry.getKey().equals(primaryKeyName)) {
                continue;
            }
            if (i++ != 0) {
                sql.append(",");
            }
            sql.append(tableAlias)
                    .append(".`")
                    .append(entry.getKey())
                    .append("`=case ")
                    .append(tableAlias)
                    .append(".`")
                    .append(primaryKeyName)
                    .append("` ")
                    .append(when)
                    .append(" end");
        }
        sql.append(" where ").append(tableAlias).append(".`").append(primaryKeyName).append("` in (").append(in).append(")");
        args.addAll(inArgs);
        parseData = whereEngine.getWhereParseData();
        if (parseData != null && parseData.getSql() != null) {
            sql.append(" and ").append(parseData.getSql());
            args.addAll(parseData.getArgs());
        }
        parseData = new ParseData();
        parseData.setSql(sql.toString());
        parseData.setArgs(args);
        return parseData;
    }

}
