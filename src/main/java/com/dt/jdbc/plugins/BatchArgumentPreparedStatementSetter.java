package com.dt.jdbc.plugins;

import org.springframework.jdbc.core.*;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author 白超
 * @version 1.0
 * @since 2018/7/10
 */
public final class BatchArgumentPreparedStatementSetter implements PreparedStatementSetter, ParameterDisposer {

    private Object[] argsArray;

    private Collection<?> argsCollection;

    private int columnCount;

    public BatchArgumentPreparedStatementSetter(Object[] argsArray, int columnCount) {
        this.argsArray = argsArray;
        this.columnCount = columnCount;
    }

    public BatchArgumentPreparedStatementSetter(Collection<?> argsCollection, int columnCount) {
        this.argsCollection = argsCollection;
        this.columnCount = columnCount;
    }

    private void setArrayValues(PreparedStatement ps) throws SQLException {
        int i = 0;
        Collection collection;
        Object[] arr;
        for (Object args : this.argsArray) {
            if (args instanceof Collection) {
                collection = ((Collection) args);
                if (collection.size() < this.columnCount) {
                    throw new SQLException("the number of parameters of a single args can not be less than " + this.columnCount);
                }
                Iterator<?> iterator = collection.iterator();
                while (iterator.hasNext()) {
                    doSetValue(ps, i++ + 1, iterator.next());
                }
            } else {
                arr = (Object[]) args;
                if (arr.length < this.columnCount) {
                    throw new SQLException("the number of parameters of a single args can not be less than " + this.columnCount);
                }
                for (Object arg : arr) {
                    doSetValue(ps, i++ + 1, arg);
                }
            }
        }
    }

    private void setCollectionValues(PreparedStatement ps) throws SQLException {
        Iterator<?> iterator = this.argsCollection.iterator();
        Object args;
        int i = 0;
        Collection collection;
        Object[] arr;
        while (iterator.hasNext()) {
            args = iterator.next();
            if (args instanceof Collection) {
                collection = ((Collection) args);
                if (collection.size() < this.columnCount) {
                    throw new SQLException("the number of parameters of a single args can not be less than " + this.columnCount);
                }
                Iterator<?> it = collection.iterator();
                while (it.hasNext()) {
                    doSetValue(ps, i++ + 1, it.next());
                }
            } else {
                arr = (Object[]) args;
                if (arr.length < this.columnCount) {
                    throw new SQLException("the number of parameters of a single args can not be less than " + this.columnCount);
                }
                for (Object arg : arr) {
                    doSetValue(ps, i++ + 1, arg);
                }
            }
        }
    }

    @Override
    public void setValues(PreparedStatement ps) throws SQLException {
        if (this.argsArray != null) {
            this.setArrayValues(ps);
        } else if (this.argsCollection != null) {
            this.setCollectionValues(ps);
        }
    }

    private void doSetValue(PreparedStatement ps, int parameterPosition, Object argValue) throws SQLException {
        if (argValue instanceof SqlParameterValue) {
            SqlParameterValue paramValue = (SqlParameterValue) argValue;
            StatementCreatorUtils.setParameterValue(ps, parameterPosition, paramValue, paramValue.getValue());
        } else {
            StatementCreatorUtils.setParameterValue(ps, parameterPosition, SqlTypeValue.TYPE_UNKNOWN, argValue);
        }
    }

    @Override
    public void cleanupParameters() {
        //不清空
    }

}
