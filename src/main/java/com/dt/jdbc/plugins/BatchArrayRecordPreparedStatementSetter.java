package com.dt.jdbc.plugins;

import com.dt.beans.BeanUtils;
import com.dt.beans.ClassAccessCache;
import com.esotericsoftware.reflectasm.MethodAccess;
import org.springframework.jdbc.core.*;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

/**
 * @author 白超
 * @version 1.0
 * @since 2018/7/10
 */
public final class BatchArrayRecordPreparedStatementSetter implements PreparedStatementSetter, ParameterDisposer {

    private Object[] recordArray;

    private Map<String, String> columnAliasMap;

    public BatchArrayRecordPreparedStatementSetter(Object[] recordArray, Map<String, String> columnAliasMap) {
        this.recordArray = recordArray;
        this.columnAliasMap = columnAliasMap;
    }

    @Override
    public void setValues(PreparedStatement ps) throws SQLException {
        if (this.recordArray != null) {
            int i = 0;
            MethodAccess methodAccess;
            for (Object record : this.recordArray) {
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
