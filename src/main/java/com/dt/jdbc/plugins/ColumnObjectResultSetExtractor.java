package com.dt.jdbc.plugins;

import com.dt.beans.BeanUtils;
import com.dt.beans.ClassAccessCache;
import com.dt.jdbc.utils.JdbcTools;
import com.esotericsoftware.reflectasm.MethodAccess;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.JdbcUtils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author 白超
 * @date 2018/7/3
 */
public final class ColumnObjectResultSetExtractor<K, T> implements ResultSetExtractor<Map<K, T>> {

    /**
     * index mode 1 => name mode
     */
    private int mode = 0;

    private int keyIndex = 1;

    private String keyColumnName;

    private Class<T> valueType;

    public ColumnObjectResultSetExtractor(int keyIndex, Class<T> valueType) {
        this.keyIndex = keyIndex;
        this.valueType = valueType;
    }

    public ColumnObjectResultSetExtractor(String keyColumnName, Class<T> valueType) {
        this.keyColumnName = keyColumnName;
        this.mode = 1;
        this.valueType = valueType;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<K, T> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<K, T> result = new LinkedHashMap<>();
        Object key;
        T value = null;
        if (mode == 0) {
            String name;
            MethodAccess methodAccess = ClassAccessCache.getMethodAccess(this.valueType);
            while (rs.next()) {
                key = null;
                try {
                    value = this.valueType.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
                ResultSetMetaData rsd = rs.getMetaData();
                int columnCount = rsd.getColumnCount();
                if (this.keyIndex <= columnCount) {
                    key = JdbcTools.getColumnValue(rs, this.keyIndex);
                }
                for (int i = 1; i <= columnCount; i++) {
                    name = JdbcTools.getColumnKey(JdbcUtils.lookupColumnName(rsd, i));
                    BeanUtils.invokeSetter(methodAccess, value, name, JdbcTools.getColumnValue(rs, i));
                }
                result.put((K) key, value);
            }
        } else if (mode == 1) {
            String name;
            MethodAccess methodAccess = ClassAccessCache.getMethodAccess(this.valueType);
            while (rs.next()) {
                key = null;
                try {
                    value = this.valueType.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
                ResultSetMetaData rsd = rs.getMetaData();
                int columnCount = rsd.getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    name = JdbcTools.getColumnKey(JdbcUtils.lookupColumnName(rsd, i));
                    if (name.equals(keyColumnName)) {
                        key = JdbcTools.getColumnValue(rs, i);
                    }
                    BeanUtils.invokeSetter(methodAccess, value, name, JdbcTools.getColumnValue(rs, i));
                }
                result.put((K) key, value);
            }
        }
        return result;
    }

}
