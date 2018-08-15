package com.dt.jdbc.plugins;

import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.util.TypeUtils;
import com.dt.beans.BeanUtils;
import com.dt.beans.ClassAccessCache;
import com.dt.jdbc.utils.JdbcTools;
import com.esotericsoftware.reflectasm.MethodAccess;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.JdbcUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author 白超
 * @version 1.0
 * @see
 * @since 2018/7/18
 */
public class ListObjectResultSetExtractor<T> implements ResultSetExtractor<List<T>> {

    private Class<T> valueType;
    private final int rowsExpected;

    public ListObjectResultSetExtractor(Class<T> valueType) {
        this.valueType = valueType;
        this.rowsExpected = 0;
    }

    public ListObjectResultSetExtractor(Class<T> valueType, int rowsExpected) {
        this.valueType = valueType;
        this.rowsExpected = rowsExpected;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<T> extractData(ResultSet rs) throws SQLException, DataAccessException {
        List<T> results = (this.rowsExpected > 0 ? new ArrayList<>(this.rowsExpected) : new ArrayList<>());
        if (this.valueType == Object.class) {
            while (rs.next()) {
                results.add((T) rs.getObject(1));
            }
            return results;
        }
        if (this.valueType == boolean.class || this.valueType == Boolean.class) {
            while (rs.next()) {
                results.add((T) Boolean.valueOf(rs.getBoolean(1)));
            }
            return results;
        }
        if (this.valueType == byte.class || this.valueType == Byte.class) {
            while (rs.next()) {
                results.add((T) Byte.valueOf(rs.getByte(1)));
            }
            return results;
        }
        if (this.valueType == short.class || this.valueType == Short.class) {
            while (rs.next()) {
                results.add((T) Short.valueOf(rs.getShort(1)));
            }
            return results;
        }
        if (this.valueType == int.class || this.valueType == Integer.class) {
            while (rs.next()) {
                results.add((T) Integer.valueOf(rs.getInt(1)));
            }
            return results;
        }
        if (this.valueType == long.class || this.valueType == Long.class) {
            while (rs.next()) {
                results.add((T) Long.valueOf(rs.getLong(1)));
            }
            return results;
        }
        if (this.valueType == float.class || this.valueType == Float.class) {
            while (rs.next()) {
                results.add((T) Float.valueOf(rs.getFloat(1)));
            }
            return results;
        }
        if (this.valueType == double.class || this.valueType == Double.class) {
            while (rs.next()) {
                results.add((T) Double.valueOf(rs.getDouble(1)));
            }
            return results;
        }
        if (this.valueType == String.class) {
            while (rs.next()) {
                results.add((T) rs.getString(1));
            }
            return results;
        }
        if (this.valueType == BigDecimal.class) {
            while (rs.next()) {
                results.add((T) rs.getBigDecimal(1));
            }
            return results;
        }
        if (this.valueType == BigInteger.class) {
            while (rs.next()) {
                results.add((T) TypeUtils.castToBigInteger(rs.getObject(1)));
            }
            return results;
        }
        if (this.valueType == Date.class) {
            while (rs.next()) {
                results.add((T) TypeUtils.castToDate(rs.getObject(1)));
            }
            return results;
        }
        if (this.valueType == java.sql.Date.class) {
            while (rs.next()) {
                results.add((T) rs.getDate(1));
            }
            return results;
        }
        if (this.valueType == java.sql.Timestamp.class) {
            while (rs.next()) {
                results.add((T) rs.getTimestamp(1));
            }
            return results;
        }
        if (this.valueType.isEnum()) {
            while (rs.next()) {
                results.add(TypeUtils.castToEnum(rs.getObject(1), this.valueType, ParserConfig.getGlobalInstance()));
            }
            return results;
        }
        //以上情况后续扩展
        //以下为正常实体类情况
        T value = null;
        String name;
        MethodAccess methodAccess = ClassAccessCache.getMethodAccess(this.valueType);
        while (rs.next()) {
            try {
                value = this.valueType.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
            ResultSetMetaData rsd = rs.getMetaData();
            int columnCount = rsd.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                name = JdbcTools.getColumnKey(JdbcUtils.lookupColumnName(rsd, i));
                BeanUtils.invokeSetter(methodAccess, value, name, JdbcTools.getColumnValue(rs, i));
            }
            results.add(value);
        }
        return results;
    }

}
