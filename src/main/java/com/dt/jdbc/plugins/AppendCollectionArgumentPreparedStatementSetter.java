package com.dt.jdbc.plugins;

import org.springframework.jdbc.core.*;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

/**
 * @author 白超
 * @version 1.0
 * @since 2018/7/10
 */
public final class AppendCollectionArgumentPreparedStatementSetter implements PreparedStatementSetter, ParameterDisposer {

    private Object args;

    private Object[] appendArgs;

    public AppendCollectionArgumentPreparedStatementSetter(Object[] args, Object... appendArgs) {
        this.args = args;
        this.appendArgs = appendArgs;
    }

    public AppendCollectionArgumentPreparedStatementSetter(Collection args, Object... appendArgs) {
        this.args = args;
        this.appendArgs = appendArgs;
    }

    @Override
    public void setValues(PreparedStatement ps) throws SQLException {
        if (this.args != null) {
            int i = 0;
            if (this.args instanceof Collection) {
                Iterator iterator = ((Collection) this.args).iterator();
                while (iterator.hasNext()) {
                    doSetValue(ps, i++ + 1, iterator.next());
                }
            }
            if (this.appendArgs != null) {
                for (Object arg : this.appendArgs) {
                    doSetValue(ps, i++ + 1, arg);
                }
            }
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
