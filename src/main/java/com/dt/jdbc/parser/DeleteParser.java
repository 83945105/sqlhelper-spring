package com.dt.jdbc.parser;

import com.dt.core.data.ParseData;
import com.dt.core.engine.WhereEngine;

import java.util.ArrayList;
import java.util.List;

/**
 * 删除解析器
 *
 * @author 白超
 * @version 1.0
 * @since 2018/7/10
 */
public class DeleteParser {

    public String deleteByPrimaryKey(String tableName, String primaryKeyName) {
        return "delete from " + tableName + " where " + primaryKeyName + " = ?";
    }

    public String batchDeleteByPrimaryKeys(String tableName, String primaryKeyName, int recordSize) {
        StringBuilder sql = new StringBuilder(32);
        sql.append("delete from ").append(tableName).append(" where ").append(primaryKeyName).append(" in (");
        for (; recordSize > 0; recordSize--) {
            if (recordSize == 1) {
                sql.append("?");
            } else {
                sql.append("?,");
            }
        }
        return sql.append(")").toString();
    }

    public ParseData delete(WhereEngine whereEngine) {
        ParseData parseData;
        String tableName = whereEngine.getTableName();
        String tableAlias = whereEngine.getTableAlias();
        StringBuilder sql = new StringBuilder(64);
        List<Object> args = new ArrayList<>();
        sql.append("delete ")
                .append(tableAlias)
                .append(" from ")
                .append(tableName)
                .append(" ")
                .append(tableAlias);
        parseData = whereEngine.getJoinParseData();
        if (parseData != null && parseData.getSql() != null) {
            sql.append(" ").append(parseData.getSql());
            args.addAll(parseData.getArgs());
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
}
