package com.dt.jdbc.parser;

import com.dt.core.data.ParseData;
import com.dt.core.engine.ColumnEngine;
import com.dt.core.norm.Engine;

import java.util.ArrayList;
import java.util.List;

/**
 * 查询数据解析器
 *
 * @author 白超
 * @version 1.0
 * @since 2018/7/10
 */
public class QueryParser {

    public String selectByPrimaryKey(ColumnEngine columnEngine) {
        return "select " +
                columnEngine.getColumnSql() +
                " from " +
                columnEngine.getTableName() +
                " " +
                columnEngine.getTableAlias() +
                " where " +
                columnEngine.getTableAlias() +
                "." +
                columnEngine.getPrimaryKeyName() +
                " = ?";
    }

    public ParseData selectList(Engine engine) {
        StringBuilder sql = new StringBuilder(128);
        ParseData parseData;
        String str;
        List<Object> args = new ArrayList<>();
        sql.append("select ")
                .append(engine.getColumnSql())
                .append(" from ")
                .append(engine.getTableName())
                .append(" ")
                .append(engine.getTableAlias());
        parseData = engine.getJoinParseData();
        if (parseData != null && parseData.getSql() != null) {
            sql.append(" ").append(parseData.getSql());
            args.addAll(parseData.getArgs());
        }
        parseData = engine.getWhereParseData();
        if (parseData != null && parseData.getSql() != null) {
            sql.append(" ").append(parseData.getSql());
            args.addAll(parseData.getArgs());
        }
        str = engine.getGroupSql();
        if (str != null && str.trim().length() != 0) {
            sql.append(" ").append(str);
        }
        str = engine.getSortSql();
        if (str != null && str.trim().length() != 0) {
            sql.append(" ").append(str);
        }
        parseData = engine.getLimitParseData();
        if (parseData != null && parseData.getSql() != null) {
            sql.append(" ").append(parseData.getSql());
            args.addAll(parseData.getArgs());
        }
        parseData = new ParseData();
        parseData.setSql(sql.toString());
        parseData.setArgs(args);
        return parseData;
    }

    public ParseData selectCount(Engine engine) {
        StringBuilder sql = new StringBuilder(128);
        ParseData parseData;
        List<Object> args = new ArrayList<>();
        String str = engine.getGroupSql();
        boolean hasGroup = str != null && str.trim().length() != 0;
        if (hasGroup) {
            sql.append("select count(1) from (select ")
                    .append(engine.getTableAlias())
                    .append(".* from ");
        } else {
            sql.append("select count(1) from ");
        }
        sql.append(engine.getTableName()).append(" ").append(engine.getTableAlias());
        parseData = engine.getJoinParseData();
        if (parseData != null && parseData.getSql() != null) {
            sql.append(" ").append(parseData.getSql());
            args.addAll(parseData.getArgs());
        }
        parseData = engine.getWhereParseData();
        if (parseData != null && parseData.getSql() != null) {
            sql.append(" ").append(parseData.getSql());
            args.addAll(parseData.getArgs());
        }
        if (hasGroup) {
            sql.append(" ").append(str).append(") C");
        }
        parseData = new ParseData();
        parseData.setSql(sql.toString());
        parseData.setArgs(args);
        return parseData;
    }

}
