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
public final class CollectionArgumentPreparedStatementSetter implements PreparedStatementSetter, ParameterDisposer {

    private Collection<?> args;

    public CollectionArgumentPreparedStatementSetter(Collection<?> args) {
        this.args = args;
    }

    @Override
    public void setValues(PreparedStatement ps) throws SQLException {
        if (this.args != null) {
            int i = 0;
            Iterator<?> iterator = this.args.iterator();
            while (iterator.hasNext()) {
                doSetValue(ps, i++ + 1, iterator.next());
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
