package com.dt.jdbc.plugins;

import com.dt.beans.BeanUtils;
import com.dt.beans.ClassAccessCache;
import com.esotericsoftware.reflectasm.MethodAccess;
import org.springframework.jdbc.core.*;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;

/**
 * @author 白超
 * @version 1.0
 * @since 2018/7/10
 */
public final class BatchCollectionRecordPreparedStatementSetter implements PreparedStatementSetter, ParameterDisposer {

    private Collection<?> recordCollection;

    private Map<String, String> columnAliasMap;

    public BatchCollectionRecordPreparedStatementSetter(Collection<?> recordCollection, Map<String, String> columnAliasMap) {
        this.recordCollection = recordCollection;
        this.columnAliasMap = columnAliasMap;
    }

    @Override
    public void setValues(PreparedStatement ps) throws SQLException {
        if (this.recordCollection != null) {
            int i = 0;
            MethodAccess methodAccess;
            for (Object record : this.recordCollection) {
                if (record instanceof Map) {
                    for (Map.Entry<String, String> entry : this.columnAliasMap.entrySet()) {
                        doSetValue(ps, i++ + 1, ((Map) record).get(entry.getKey()));
                    }
                } else {
                    methodAccess = ClassAccessCache.getMethodAccess(record.getClass());
                    for (Map.Entry<String, String> entry : this.columnAliasMap.entrySet()) {
                        doSetValue(ps, i++ + 1, methodAccess.invoke(record, BeanUtils.getGetterMethodName(entry.getValue(), false)));
                    }
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
