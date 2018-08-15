package com.dt.jdbc.parsing;

import com.dt.core.engine.ColumnEngine;
import com.dt.core.engine.WhereEngine;
import com.dt.core.norm.Engine;
import com.dt.core.norm.Model;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by 白超 on 2018/8/15.
 */
public interface JdbcEngineSqlBuilder {

    int copyTable(String sourceTableName, String targetTableName);

    int deleteTable(String tableName);

    int renameTable(String sourceTableName, String targetTableName);

    boolean isTableExist(String tableName);

    Map<String, Object> queryByPrimaryKey(Object keyValue, ColumnEngine columnEngine);

    Map<String, Object> queryOne(Engine engine);

    List<Map<String, Object>> queryForList(Engine engine);

    int queryCount(Engine engine);

    <K, V> Map<K, V> queryPairColumnInMap(Engine engine);

    <K, V> Map<K, V> queryPairColumnInMap(int keyIndex, int valueIndex, Engine engine);

    <K, V> Map<K, V> queryPairColumnInMap(String keyColumnName, String valueColumnName, Engine engine);

    <K> Map<K, Map<String, Object>> queryForListInMap(int keyIndex, Engine engine);

    <K> Map<K, Map<String, Object>> queryForListInMap(String keyColumnName, Engine engine);

    int insertArgs(Object[] args, ColumnEngine columnEngine);

    int insertArgs(Collection<?> args, ColumnEngine columnEngine);

    <T extends Model> int insertRecord(Map<String, ?> record, Class<T> modelClass);

    <T extends Model> int insertRecord(Map<String, ?> record, String tableName, Class<T> modelClass);

    <T extends Model> int insertRecord(Object record, Class<T> modelClass);

    <T extends Model> int insertRecord(Object record, String tableName, Class<T> modelClass);

    int insertRecord(Map<String, ?> record, ColumnEngine columnEngine);

    int insertRecord(Object record, ColumnEngine columnEngine);

    <T extends Model> int insertRecordSelective(Map<String, ?> record, Class<T> modelClass);

    <T extends Model> int insertRecordSelective(Map<String, ?> record, String tableName, Class<T> modelClass);

    <T extends Model> int insertRecordSelective(Object record, Class<T> modelClass);

    <T extends Model> int insertRecordSelective(Object record, String tableName, Class<T> modelClass);

    int insertRecordSelective(Map<String, ?> record, ColumnEngine columnEngine);

    int insertRecordSelective(Object record, ColumnEngine columnEngine);

    <T extends Model> int batchInsertRecords(Object[] records, Class<T> modelClass);

    <T extends Model> int batchInsertRecords(Object[] records, String tableName, Class<T> modelClass);

    <T extends Model> int batchInsertRecords(Collection<?> records, Class<T> modelClass);

    <T extends Model> int batchInsertRecords(Collection<?> records, String tableName, Class<T> modelClass);

    int batchInsertRecords(Object[] records, ColumnEngine columnEngine);

    int batchInsertRecords(Collection<?> records, ColumnEngine columnEngine);

    int updateArgsByPrimaryKey(Object keyValue, Object[] args, ColumnEngine columnEngine);

    int updateArgsByPrimaryKey(Object keyValue, Collection<?> args, ColumnEngine columnEngine);

    <T extends Model> int updateRecordByPrimaryKey(Object keyValue, Map<String, ?> record, Class<T> modelClass);

    <T extends Model> int updateRecordByPrimaryKey(Object keyValue, Object record, Class<T> modelClass);

    <T extends Model> int updateRecordByPrimaryKey(Object keyValue, Map<String, ?> record, String tableName, Class<T> modelClass);

    <T extends Model> int updateRecordByPrimaryKey(Object keyValue, Object record, String tableName, Class<T> modelClass);

    int updateRecordByPrimaryKey(Object keyValue, Map<String, ?> record, ColumnEngine columnEngine);

    int updateRecordByPrimaryKey(Object keyValue, Object record, ColumnEngine columnEngine);

    <T extends Model> int updateRecordByPrimaryKeySelective(Object keyValue, Map<String, ?> record, Class<T> modelClass);

    <T extends Model> int updateRecordByPrimaryKeySelective(Object keyValue, Map<String, ?> record, String tableName, Class<T> modelClass);

    <T extends Model> int updateRecordByPrimaryKeySelective(Object keyValue, Object record, Class<T> modelClass);

    <T extends Model> int updateRecordByPrimaryKeySelective(Object keyValue, Object record, String tableName, Class<T> modelClass);

    int updateRecordByPrimaryKeySelective(Object keyValue, Map<String, ?> record, ColumnEngine columnEngine);

    int updateRecordByPrimaryKeySelective(Object keyValue, Object record, ColumnEngine columnEngine);

    int updateRecord(Map<String, ?> record, WhereEngine whereEngine);

    int updateRecord(Object record, WhereEngine whereEngine);

    int updateRecordSelective(Map<String, ?> record, WhereEngine whereEngine);

    int updateRecordSelective(Object record, WhereEngine whereEngine);

    <T extends Model> int batchUpdateRecordsByPrimaryKeys(Object[] records, Class<T> modelClass);

    <T extends Model> int batchUpdateRecordsByPrimaryKeys(Object[] records, String tableName, Class<T> modelClass);

    <T extends Model> int batchUpdateRecordsByPrimaryKeys(Collection<?> records, Class<T> modelClass);

    <T extends Model> int batchUpdateRecordsByPrimaryKeys(Collection<?> records, String tableName, Class<T> modelClass);

    int batchUpdateRecordsByPrimaryKeys(Object[] records, WhereEngine whereEngine);

    int batchUpdateRecordsByPrimaryKeys(Collection<?> records, WhereEngine whereEngine);

    int updateOrInsertArgs(Object[] batchArgs, ColumnEngine columnEngine);

    int updateOrInsertArgs(Collection<?> batchArgs, ColumnEngine columnEngine);

    <T extends Model> int updateOrInsertRecord(Object[] records, Class<T> modelClass);

    <T extends Model> int updateOrInsertRecord(Object[] records, String tableName, Class<T> modelClass);

    <T extends Model> int updateOrInsertRecord(Collection<?> records, Class<T> modelClass);

    <T extends Model> int updateOrInsertRecord(Collection<?> records, String tableName, Class<T> modelClass);

    int updateOrInsertRecord(Object[] records, ColumnEngine columnEngine);

    int updateOrInsertRecord(Collection<?> records, ColumnEngine columnEngine);

    <T extends Model> int deleteByPrimaryKey(Object keyValue, Class<T> modelClass);

    <T extends Model> int deleteByPrimaryKey(Object keyValue, String tableName, Class<T> modelClass);

    <T extends Model> int batchDeleteByPrimaryKeys(Object[] keyValues, Class<T> modelClass);

    <T extends Model> int batchDeleteByPrimaryKeys(Object[] keyValues, String tableName, Class<T> modelClass);

    <T extends Model> int batchDeleteByPrimaryKeys(Collection<?> keyValues, Class<T> modelClass);

    <T extends Model> int batchDeleteByPrimaryKeys(Collection<?> keyValues, String tableName, Class<T> modelClass);

    int delete(WhereEngine whereEngine);

}
