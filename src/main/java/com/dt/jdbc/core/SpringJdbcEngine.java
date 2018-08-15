package com.dt.jdbc.core;

import com.dt.core.data.ParseData;
import com.dt.core.engine.ColumnEngine;
import com.dt.core.engine.WhereEngine;
import com.dt.core.norm.Engine;
import com.dt.core.norm.Model;
import com.dt.jdbc.JdbcEngine;
import com.dt.jdbc.parser.*;
import com.dt.jdbc.plugins.*;
import com.dt.jdbc.utils.JdbcTools;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fusesource.jansi.Ansi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;
import org.springframework.jdbc.core.SingleColumnRowMapper;

import java.util.*;

/**
 * SpringJdbc引擎
 *
 * @author 白超
 * @version 1.0
 * @since 2018/7/10
 */
public final class SpringJdbcEngine implements JdbcEngine {

    @Autowired
    @SuppressWarnings("all")
    private JdbcTemplate jdbcTemplate;

    private QueryParser queryParser = new QueryParser();

    private InsertParser insertParser = new InsertParser();

    private UpdateParser updateParser = new UpdateParser();

    private UpdateOrInsertParser updateOrInsertParser = new UpdateOrInsertParser();

    private DeleteParser deleteParser = new DeleteParser();

    private final Log logger = LogFactory.getLog(getClass());

    private <T extends Model> Model newModel(Class<T> modelClass) {
        Model model = null;
        try {
            model = modelClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return model;
    }

    private void printPrecompileSqlAndArgs(String sql, Object prefixArgs, Object args, Object suffixArgs) {
        if (logger.isDebugEnabled()) {
            logger.debug(Ansi.ansi().eraseScreen()
                    .fg(Ansi.Color.YELLOW)
                    .a("Executing precompile SQL [" + sql + "]")
                    .reset());
            List<Object> objects = new ArrayList<>();
            if (prefixArgs != null) {
                if (prefixArgs instanceof Collection) {
                    objects.addAll((Collection<?>) prefixArgs);
                } else if (prefixArgs.getClass().isArray()) {
                    Collections.addAll(objects, prefixArgs);
                } else {
                    objects.add(prefixArgs);
                }
            }
            if (args != null) {
                if (args instanceof Collection) {
                    objects.addAll((Collection<?>) args);
                } else if (args.getClass().isArray()) {
                    Collections.addAll(objects, args);
                } else {
                    objects.add(args);
                }
            }
            if (suffixArgs != null) {
                if (suffixArgs instanceof Collection) {
                    objects.addAll((Collection<?>) suffixArgs);
                } else if (suffixArgs.getClass().isArray()) {
                    Collections.addAll(objects, suffixArgs);
                } else {
                    objects.add(suffixArgs);
                }
            }
            logger.debug(Ansi.ansi().eraseScreen()
                    .fg(Ansi.Color.RED)
                    .a("Precompile SQL args " + objects.toString())
                    .reset());
            String[] sqlParts = sql.split("\\?");
            StringBuilder sb = new StringBuilder(sql.length() + objects.size() + 5);
            Object value;
            for (int i = 0; i < sqlParts.length; i++) {
                if (i < objects.size()) {
                    value = objects.get(i);
                    if (value == null) {
                        sb.append(sqlParts[i]).append("null");
                    } else if (value instanceof String) {
                        sb.append(sqlParts[i]).append("'").append(value).append("'");
                    } else {
                        sb.append(sqlParts[i]).append(value);
                    }
                    continue;
                }
                sb.append(sqlParts[i]);
            }
            logger.debug(Ansi.ansi().eraseScreen()
                    .fg(Ansi.Color.GREEN)
                    .a("Execute SQL " + sb.toString())
                    .reset());
        }
    }

    @Override
    public int copyTable(String sourceTableName, String targetTableName) {
        String sql = "create table " + targetTableName + " like " + sourceTableName;
        printPrecompileSqlAndArgs(sql, null, null, null);
        return this.jdbcTemplate.update(sql);
    }

    @Override
    public int deleteTable(String tableName) {
        String sql = "drop table " + tableName;
        printPrecompileSqlAndArgs(sql, null, null, null);
        return this.jdbcTemplate.update(sql);
    }

    @Override
    public int renameTable(String sourceTableName, String targetTableName) {
        String sql = "rename table " + sourceTableName + " to " + targetTableName;
        printPrecompileSqlAndArgs(sql, null, null, null);
        return this.jdbcTemplate.update(sql);
    }

    @Override
    public boolean isTableExist(String tableName) {
        String sql = "select count(*) from information_schema.TABLES where table_name = '" + tableName + "'";
        printPrecompileSqlAndArgs(sql, null, null, null);
        Integer count = this.jdbcTemplate.queryForObject(sql, Integer.class);
        return count == null || count > 0;
    }

    @Override
    public Map<String, Object> queryByPrimaryKey(Object keyValue, ColumnEngine columnEngine) {
        String sql = this.queryParser.selectByPrimaryKey(columnEngine);
        printPrecompileSqlAndArgs(sql, null, keyValue, null);
        return this.jdbcTemplate.queryForMap(sql, keyValue);
    }

    @Override
    public <T> T queryByPrimaryKey(Object keyValue, Class<T> returnType, ColumnEngine columnEngine) {
        String sql = this.queryParser.selectByPrimaryKey(columnEngine);
        printPrecompileSqlAndArgs(sql, null, keyValue, null);
        List<T> results = this.jdbcTemplate.query(sql, new Object[]{keyValue}, new ListObjectResultSetExtractor<>(returnType, 1));
        return JdbcTools.nullableSingleResult(results);
    }

    @Override
    public Map<String, Object> queryOne(Engine engine) {
        ParseData data = this.queryParser.selectList(engine);
        String sql = data.getSql();
        List<Object> args = data.getArgs();
        printPrecompileSqlAndArgs(sql, null, args, null);
        List<Map<String, Object>> results = this.jdbcTemplate.query(sql, new CollectionArgumentPreparedStatementSetter(args),
                new RowMapperResultSetExtractor<>(new ColumnMapRowMapper(), 1));
        return JdbcTools.nullableSingleResult(results);
    }

    @Override
    public <T> T queryOne(Class<T> returnType, Engine engine) {
        ParseData data = this.queryParser.selectList(engine);
        String sql = data.getSql();
        List<Object> args = data.getArgs();
        printPrecompileSqlAndArgs(sql, null, args, null);
        List<T> results = this.jdbcTemplate.query(sql, new CollectionArgumentPreparedStatementSetter(args),
                new ListObjectResultSetExtractor<>(returnType, 1));
        return JdbcTools.nullableSingleResult(results);
    }

    @Override
    public List<Map<String, Object>> queryForList(Engine engine) {
        ParseData data = this.queryParser.selectList(engine);
        String sql = data.getSql();
        List<Object> args = data.getArgs();
        printPrecompileSqlAndArgs(sql, null, args, null);
        return this.jdbcTemplate.query(sql, new CollectionArgumentPreparedStatementSetter(args),
                new RowMapperResultSetExtractor<>(new ColumnMapRowMapper()));
    }

    @Override
    public <T> List<T> queryForList(Class<T> returnType, Engine engine) {
        ParseData data = this.queryParser.selectList(engine);
        String sql = data.getSql();
        List<Object> args = data.getArgs();
        printPrecompileSqlAndArgs(sql, null, args, null);
        return this.jdbcTemplate.query(sql, new CollectionArgumentPreparedStatementSetter(args),
                new ListObjectResultSetExtractor<>(returnType));
    }

    @Override
    public int queryCount(Engine engine) {
        ParseData data = this.queryParser.selectCount(engine);
        String sql = data.getSql();
        List<Object> args = data.getArgs();
        printPrecompileSqlAndArgs(sql, null, args, null);
        List<Integer> results = this.jdbcTemplate.query(sql, new CollectionArgumentPreparedStatementSetter(args),
                new RowMapperResultSetExtractor<>(new SingleColumnRowMapper<>(Integer.class), 1));
        return JdbcTools.countSingleResult(results);
    }

    @Override
    public <K, V> Map<K, V> queryPairColumnInMap(Engine engine) {
        ParseData data = this.queryParser.selectList(engine);
        String sql = data.getSql();
        List<Object> args = data.getArgs();
        printPrecompileSqlAndArgs(sql, null, args, null);
        return this.jdbcTemplate.query(sql, new CollectionArgumentPreparedStatementSetter(args), new PairColumnResultSetExtractor<>());
    }

    @Override
    public <K, V> Map<K, V> queryPairColumnInMap(int keyIndex, int valueIndex, Engine engine) {
        ParseData data = this.queryParser.selectList(engine);
        String sql = data.getSql();
        List<Object> args = data.getArgs();
        printPrecompileSqlAndArgs(sql, null, args, null);
        return this.jdbcTemplate.query(sql, new CollectionArgumentPreparedStatementSetter(args), new PairColumnResultSetExtractor<>(keyIndex, valueIndex));
    }

    @Override
    public <K, V> Map<K, V> queryPairColumnInMap(String keyColumnName, String valueColumnName, Engine engine) {
        ParseData data = this.queryParser.selectList(engine);
        String sql = data.getSql();
        List<Object> args = data.getArgs();
        printPrecompileSqlAndArgs(sql, null, args, null);
        return this.jdbcTemplate.query(sql, new CollectionArgumentPreparedStatementSetter(args), new PairColumnResultSetExtractor<>(keyColumnName, valueColumnName));
    }

    @Override
    public <K> Map<K, Map<String, Object>> queryForListInMap(int keyIndex, Engine engine) {
        ParseData data = this.queryParser.selectList(engine);
        String sql = data.getSql();
        List<Object> args = data.getArgs();
        printPrecompileSqlAndArgs(sql, null, args, null);
        return this.jdbcTemplate.query(sql, new CollectionArgumentPreparedStatementSetter(args),
                new ColumnMapResultSetExtractor<K>(keyIndex));
    }

    @Override
    public <K> Map<K, Map<String, Object>> queryForListInMap(String keyColumnName, Engine engine) {
        ParseData data = this.queryParser.selectList(engine);
        String sql = data.getSql();
        List<Object> args = data.getArgs();
        printPrecompileSqlAndArgs(sql, null, args, null);
        return this.jdbcTemplate.query(sql, new CollectionArgumentPreparedStatementSetter(args),
                new ColumnMapResultSetExtractor<K>(keyColumnName));
    }

    @Override
    public <K, T> Map<K, T> queryForListInMap(int keyIndex, Class<T> returnType, Engine engine) {
        ParseData data = this.queryParser.selectList(engine);
        String sql = data.getSql();
        List<Object> args = data.getArgs();
        printPrecompileSqlAndArgs(sql, null, args, null);
        return this.jdbcTemplate.query(sql, new CollectionArgumentPreparedStatementSetter(args),
                new ColumnObjectResultSetExtractor<K, T>(keyIndex, returnType));
    }

    @Override
    public <K, T> Map<K, T> queryForListInMap(String keyColumnName, Class<T> returnType, Engine engine) {
        ParseData data = this.queryParser.selectList(engine);
        String sql = data.getSql();
        List<Object> args = data.getArgs();
        printPrecompileSqlAndArgs(sql, null, args, null);
        return this.jdbcTemplate.query(sql, new CollectionArgumentPreparedStatementSetter(args),
                new ColumnObjectResultSetExtractor<K, T>(keyColumnName, returnType));
    }

    @Override
    @SuppressWarnings("unchecked")
    public int insertArgs(Object[] args, ColumnEngine columnEngine) {
        Map<String, String> columnAliasMap = columnEngine.getColumnAliasMap();
        if (columnAliasMap.size() == 0) {
            columnAliasMap = columnEngine.getTable().getColumnAliasMap();
        }
        String sql = this.insertParser.insert(columnEngine.getTableName(), columnAliasMap);
        printPrecompileSqlAndArgs(sql, null, args, null);
        return this.jdbcTemplate.update(sql, args);
    }

    @Override
    @SuppressWarnings("unchecked")
    public int insertArgs(Collection<?> args, ColumnEngine columnEngine) {
        Map<String, String> columnAliasMap = columnEngine.getColumnAliasMap();
        if (columnAliasMap.size() == 0) {
            columnAliasMap = columnEngine.getTable().getColumnAliasMap();
        }
        String sql = this.insertParser.insert(columnEngine.getTableName(), columnAliasMap);
        printPrecompileSqlAndArgs(sql, null, args, null);
        return this.jdbcTemplate.update(sql, new CollectionArgumentPreparedStatementSetter(args));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Model> int insertRecord(Map<String, ?> record, Class<T> modelClass) {
        Model model = newModel(modelClass);
        ParseData data = this.insertParser.insertMap(model.getTableName(), model.getColumnAliasMap(), record);
        String sql = data.getSql();
        List<Object> args = data.getArgs();
        printPrecompileSqlAndArgs(sql, null, args, null);
        return this.jdbcTemplate.update(sql, new CollectionArgumentPreparedStatementSetter(args));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Model> int insertRecord(Map<String, ?> record, String tableName, Class<T> modelClass) {
        Model model = newModel(modelClass);
        ParseData data = this.insertParser.insertMap(tableName, model.getColumnAliasMap(), record);
        String sql = data.getSql();
        List<Object> args = data.getArgs();
        printPrecompileSqlAndArgs(sql, null, args, null);
        return this.jdbcTemplate.update(sql, new CollectionArgumentPreparedStatementSetter(args));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Model> int insertRecord(Object record, Class<T> modelClass) {
        Model model = newModel(modelClass);
        ParseData data = this.insertParser.insertObject(model.getTableName(), model.getColumnAliasMap(), record);
        String sql = data.getSql();
        List<Object> args = data.getArgs();
        printPrecompileSqlAndArgs(sql, null, args, null);
        return this.jdbcTemplate.update(sql, new CollectionArgumentPreparedStatementSetter(args));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Model> int insertRecord(Object record, String tableName, Class<T> modelClass) {
        Model model = newModel(modelClass);
        ParseData data = this.insertParser.insertObject(tableName, model.getColumnAliasMap(), record);
        String sql = data.getSql();
        List<Object> args = data.getArgs();
        printPrecompileSqlAndArgs(sql, null, args, null);
        return this.jdbcTemplate.update(sql, new CollectionArgumentPreparedStatementSetter(args));
    }

    @Override
    @SuppressWarnings("unchecked")
    public int insertRecord(Map<String, ?> record, ColumnEngine columnEngine) {
        Map<String, String> columnAliasMap = columnEngine.getColumnAliasMap();
        if (columnAliasMap.size() == 0) {
            columnAliasMap = columnEngine.getTable().getColumnAliasMap();
        }
        ParseData data = this.insertParser.insertMap(columnEngine.getTableName(), columnAliasMap, record);
        String sql = data.getSql();
        List<Object> args = data.getArgs();
        printPrecompileSqlAndArgs(sql, null, args, null);
        return this.jdbcTemplate.update(sql, new CollectionArgumentPreparedStatementSetter(args));
    }

    @Override
    @SuppressWarnings("unchecked")
    public int insertRecord(Object record, ColumnEngine columnEngine) {
        Map<String, String> columnAliasMap = columnEngine.getColumnAliasMap();
        if (columnAliasMap.size() == 0) {
            columnAliasMap = columnEngine.getTable().getColumnAliasMap();
        }
        ParseData data = this.insertParser.insertObject(columnEngine.getTableName(), columnAliasMap, record);
        String sql = data.getSql();
        List<Object> args = data.getArgs();
        printPrecompileSqlAndArgs(sql, null, args, null);
        return this.jdbcTemplate.update(sql, new CollectionArgumentPreparedStatementSetter(args));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Model> int insertRecordSelective(Map<String, ?> record, Class<T> modelClass) {
        Model model = newModel(modelClass);
        ParseData data = this.insertParser.insertMapSelective(model.getTableName(), model.getColumnAliasMap(), record);
        String sql = data.getSql();
        List<Object> args = data.getArgs();
        printPrecompileSqlAndArgs(sql, null, args, null);
        return this.jdbcTemplate.update(sql, new CollectionArgumentPreparedStatementSetter(args));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Model> int insertRecordSelective(Map<String, ?> record, String tableName, Class<T> modelClass) {
        Model model = newModel(modelClass);
        ParseData data = this.insertParser.insertMapSelective(tableName, model.getColumnAliasMap(), record);
        String sql = data.getSql();
        List<Object> args = data.getArgs();
        printPrecompileSqlAndArgs(sql, null, args, null);
        return this.jdbcTemplate.update(sql, new CollectionArgumentPreparedStatementSetter(args));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Model> int insertRecordSelective(Object record, Class<T> modelClass) {
        Model model = newModel(modelClass);
        ParseData data = this.insertParser.insertObjectSelective(model.getTableName(), model.getColumnAliasMap(), record);
        String sql = data.getSql();
        List<Object> args = data.getArgs();
        printPrecompileSqlAndArgs(sql, null, args, null);
        return this.jdbcTemplate.update(sql, new CollectionArgumentPreparedStatementSetter(args));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Model> int insertRecordSelective(Object record, String tableName, Class<T> modelClass) {
        Model model = newModel(modelClass);
        ParseData data = this.insertParser.insertObjectSelective(tableName, model.getColumnAliasMap(), record);
        String sql = data.getSql();
        List<Object> args = data.getArgs();
        printPrecompileSqlAndArgs(sql, null, args, null);
        return this.jdbcTemplate.update(sql, new CollectionArgumentPreparedStatementSetter(args));
    }

    @Override
    @SuppressWarnings("unchecked")
    public int insertRecordSelective(Map<String, ?> record, ColumnEngine columnEngine) {
        Map<String, String> columnAliasMap = columnEngine.getColumnAliasMap();
        if (columnAliasMap.size() == 0) {
            columnAliasMap = columnEngine.getTable().getColumnAliasMap();
        }
        ParseData data = this.insertParser.insertMapSelective(columnEngine.getTableName(), columnAliasMap, record);
        String sql = data.getSql();
        List<Object> args = data.getArgs();
        printPrecompileSqlAndArgs(sql, null, args, null);
        return this.jdbcTemplate.update(sql, new CollectionArgumentPreparedStatementSetter(args));
    }

    @Override
    @SuppressWarnings("unchecked")
    public int insertRecordSelective(Object record, ColumnEngine columnEngine) {
        Map<String, String> columnAliasMap = columnEngine.getColumnAliasMap();
        if (columnAliasMap.size() == 0) {
            columnAliasMap = columnEngine.getTable().getColumnAliasMap();
        }
        ParseData data = this.insertParser.insertObjectSelective(columnEngine.getTableName(), columnAliasMap, record);
        String sql = data.getSql();
        List<Object> args = data.getArgs();
        printPrecompileSqlAndArgs(sql, null, args, null);
        return this.jdbcTemplate.update(sql, new CollectionArgumentPreparedStatementSetter(args));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Model> int batchInsertRecords(Object[] records, Class<T> modelClass) {
        Model model = newModel(modelClass);
        Map<String, String> columnAliasMap = model.getColumnAliasMap();
        String sql = this.insertParser.batchInsert(model.getTableName(), columnAliasMap, records.length);
        printPrecompileSqlAndArgs(sql, null, null, null);
        return this.jdbcTemplate.update(sql, new BatchArrayRecordPreparedStatementSetter(records, columnAliasMap));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Model> int batchInsertRecords(Object[] records, String tableName, Class<T> modelClass) {
        Model model = newModel(modelClass);
        Map<String, String> columnAliasMap = model.getColumnAliasMap();
        String sql = this.insertParser.batchInsert(tableName, columnAliasMap, records.length);
        printPrecompileSqlAndArgs(sql, null, null, null);
        return this.jdbcTemplate.update(sql, new BatchArrayRecordPreparedStatementSetter(records, columnAliasMap));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Model> int batchInsertRecords(Collection<?> records, Class<T> modelClass) {
        Model model = newModel(modelClass);
        Map<String, String> columnAliasMap = model.getColumnAliasMap();
        String sql = this.insertParser.batchInsert(model.getTableName(), columnAliasMap, records.size());
        printPrecompileSqlAndArgs(sql, null, null, null);
        return this.jdbcTemplate.update(sql, new BatchCollectionRecordPreparedStatementSetter(records, columnAliasMap));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Model> int batchInsertRecords(Collection<?> records, String tableName, Class<T> modelClass) {
        Model model = newModel(modelClass);
        Map<String, String> columnAliasMap = model.getColumnAliasMap();
        String sql = this.insertParser.batchInsert(tableName, columnAliasMap, records.size());
        printPrecompileSqlAndArgs(sql, null, null, null);
        return this.jdbcTemplate.update(sql, new BatchCollectionRecordPreparedStatementSetter(records, columnAliasMap));
    }

    @Override
    @SuppressWarnings("unchecked")
    public int batchInsertRecords(Object[] records, ColumnEngine columnEngine) {
        Map<String, String> columnAliasMap = columnEngine.getColumnAliasMap();
        if (columnAliasMap.size() == 0) {
            columnAliasMap = columnEngine.getTable().getColumnAliasMap();
        }
        String sql = this.insertParser.batchInsert(columnEngine.getTableName(), columnAliasMap, records.length);
        printPrecompileSqlAndArgs(sql, null, null, null);
        return this.jdbcTemplate.update(sql, new BatchArrayRecordPreparedStatementSetter(records, columnAliasMap));
    }

    @Override
    @SuppressWarnings("unchecked")
    public int batchInsertRecords(Collection<?> records, ColumnEngine columnEngine) {
        Map<String, String> columnAliasMap = columnEngine.getColumnAliasMap();
        if (columnAliasMap.size() == 0) {
            columnAliasMap = columnEngine.getTable().getColumnAliasMap();
        }
        String sql = this.insertParser.batchInsert(columnEngine.getTableName(), columnAliasMap, records.size());
        printPrecompileSqlAndArgs(sql, null, null, null);
        return this.jdbcTemplate.update(sql, new BatchCollectionRecordPreparedStatementSetter(records, columnAliasMap));
    }

    @Override
    @SuppressWarnings("unchecked")
    public int updateArgsByPrimaryKey(Object keyValue, Object[] args, ColumnEngine columnEngine) {
        Map<String, String> columnAliasMap = columnEngine.getColumnAliasMap();
        if (columnAliasMap.size() == 0) {
            columnAliasMap = columnEngine.getTable().getColumnAliasMap();
        }
        String sql = this.updateParser.updateByPrimaryKey(columnEngine.getTableName(), columnEngine.getPrimaryKeyName(), columnAliasMap);
        printPrecompileSqlAndArgs(sql, null, args, keyValue);
        return this.jdbcTemplate.update(sql, new AppendCollectionArgumentPreparedStatementSetter(args, keyValue));
    }

    @Override
    @SuppressWarnings("unchecked")
    public int updateArgsByPrimaryKey(Object keyValue, Collection<?> args, ColumnEngine columnEngine) {
        Map<String, String> columnAliasMap = columnEngine.getColumnAliasMap();
        if (columnAliasMap.size() == 0) {
            columnAliasMap = columnEngine.getTable().getColumnAliasMap();
        }
        String sql = this.updateParser.updateByPrimaryKey(columnEngine.getTableName(), columnEngine.getPrimaryKeyName(), columnAliasMap);
        printPrecompileSqlAndArgs(sql, null, args, keyValue);
        return this.jdbcTemplate.update(sql, new AppendCollectionArgumentPreparedStatementSetter(args, keyValue));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Model> int updateRecordByPrimaryKey(Object keyValue, Map<String, ?> record, Class<T> modelClass) {
        Model model = newModel(modelClass);
        ParseData data = this.updateParser.updateMapByPrimaryKey(model.getTableName(),
                model.getPrimaryKeyName(), keyValue, model.getColumnAliasMap(), record);
        String sql = data.getSql();
        List<Object> args = data.getArgs();
        printPrecompileSqlAndArgs(sql, null, args, null);
        return this.jdbcTemplate.update(sql, new CollectionArgumentPreparedStatementSetter(args));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Model> int updateRecordByPrimaryKey(Object keyValue, Object record, Class<T> modelClass) {
        Model model = newModel(modelClass);
        ParseData data = this.updateParser.updateObjectByPrimaryKey(model.getTableName(),
                model.getPrimaryKeyName(), keyValue, model.getColumnAliasMap(), record);
        String sql = data.getSql();
        List<Object> args = data.getArgs();
        printPrecompileSqlAndArgs(sql, null, args, null);
        return this.jdbcTemplate.update(sql, new CollectionArgumentPreparedStatementSetter(args));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Model> int updateRecordByPrimaryKey(Object keyValue, Map<String, ?> record, String tableName, Class<T> modelClass) {
        Model model = newModel(modelClass);
        ParseData data = this.updateParser.updateMapByPrimaryKey(tableName,
                model.getPrimaryKeyName(), keyValue, model.getColumnAliasMap(), record);
        String sql = data.getSql();
        List<Object> args = data.getArgs();
        printPrecompileSqlAndArgs(sql, null, args, null);
        return this.jdbcTemplate.update(sql, new CollectionArgumentPreparedStatementSetter(args));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Model> int updateRecordByPrimaryKey(Object keyValue, Object record, String tableName, Class<T> modelClass) {
        Model model = newModel(modelClass);
        ParseData data = this.updateParser.updateObjectByPrimaryKey(tableName,
                model.getPrimaryKeyName(), keyValue, model.getColumnAliasMap(), record);
        String sql = data.getSql();
        List<Object> args = data.getArgs();
        printPrecompileSqlAndArgs(sql, null, args, null);
        return this.jdbcTemplate.update(sql, new CollectionArgumentPreparedStatementSetter(args));
    }

    @Override
    @SuppressWarnings("unchecked")
    public int updateRecordByPrimaryKey(Object keyValue, Map<String, ?> record, ColumnEngine columnEngine) {
        Map<String, String> columnAliasMap = columnEngine.getColumnAliasMap();
        if (columnAliasMap.size() == 0) {
            columnAliasMap = columnEngine.getTable().getColumnAliasMap();
        }
        ParseData data = this.updateParser.updateMapByPrimaryKey(columnEngine.getTableName(),
                columnEngine.getPrimaryKeyName(), keyValue, columnAliasMap, record);
        String sql = data.getSql();
        List<Object> args = data.getArgs();
        printPrecompileSqlAndArgs(sql, null, args, null);
        return this.jdbcTemplate.update(sql, new CollectionArgumentPreparedStatementSetter(args));
    }

    @Override
    @SuppressWarnings("unchecked")
    public int updateRecordByPrimaryKey(Object keyValue, Object record, ColumnEngine columnEngine) {
        Map<String, String> columnAliasMap = columnEngine.getColumnAliasMap();
        if (columnAliasMap.size() == 0) {
            columnAliasMap = columnEngine.getTable().getColumnAliasMap();
        }
        ParseData data = this.updateParser.updateObjectByPrimaryKey(columnEngine.getTableName(),
                columnEngine.getPrimaryKeyName(), keyValue, columnAliasMap, record);
        String sql = data.getSql();
        List<Object> args = data.getArgs();
        printPrecompileSqlAndArgs(sql, null, args, null);
        return this.jdbcTemplate.update(sql, new CollectionArgumentPreparedStatementSetter(args));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Model> int updateRecordByPrimaryKeySelective(Object keyValue, Map<String, ?> record, Class<T> modelClass) {
        Model model = newModel(modelClass);
        ParseData data = this.updateParser.updateMapByPrimaryKeySelective(model.getTableName(),
                model.getPrimaryKeyName(), keyValue, model.getColumnAliasMap(), record);
        String sql = data.getSql();
        List<Object> args = data.getArgs();
        printPrecompileSqlAndArgs(sql, null, args, null);
        return this.jdbcTemplate.update(sql, new CollectionArgumentPreparedStatementSetter(args));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Model> int updateRecordByPrimaryKeySelective(Object keyValue, Map<String, ?> record, String tableName, Class<T> modelClass) {
        Model model = newModel(modelClass);
        ParseData data = this.updateParser.updateMapByPrimaryKeySelective(tableName,
                model.getPrimaryKeyName(), keyValue, model.getColumnAliasMap(), record);
        String sql = data.getSql();
        List<Object> args = data.getArgs();
        printPrecompileSqlAndArgs(sql, null, args, null);
        return this.jdbcTemplate.update(sql, new CollectionArgumentPreparedStatementSetter(args));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Model> int updateRecordByPrimaryKeySelective(Object keyValue, Object record, Class<T> modelClass) {
        Model model = newModel(modelClass);
        ParseData data = this.updateParser.updateObjectByPrimaryKeySelective(model.getTableName(),
                model.getPrimaryKeyName(), keyValue, model.getColumnAliasMap(), record);
        String sql = data.getSql();
        List<Object> args = data.getArgs();
        printPrecompileSqlAndArgs(sql, null, args, null);
        return this.jdbcTemplate.update(sql, new CollectionArgumentPreparedStatementSetter(args));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Model> int updateRecordByPrimaryKeySelective(Object keyValue, Object record, String tableName, Class<T> modelClass) {
        Model model = newModel(modelClass);
        ParseData data = this.updateParser.updateObjectByPrimaryKeySelective(tableName,
                model.getPrimaryKeyName(), keyValue, model.getColumnAliasMap(), record);
        String sql = data.getSql();
        List<Object> args = data.getArgs();
        printPrecompileSqlAndArgs(sql, null, args, null);
        return this.jdbcTemplate.update(sql, new CollectionArgumentPreparedStatementSetter(args));
    }

    @Override
    @SuppressWarnings("unchecked")
    public int updateRecordByPrimaryKeySelective(Object keyValue, Map<String, ?> record, ColumnEngine columnEngine) {
        Map<String, String> columnAliasMap = columnEngine.getColumnAliasMap();
        if (columnAliasMap.size() == 0) {
            columnAliasMap = columnEngine.getTable().getColumnAliasMap();
        }
        ParseData data = this.updateParser.updateMapByPrimaryKeySelective(columnEngine.getTableName(),
                columnEngine.getPrimaryKeyName(), keyValue, columnAliasMap, record);
        String sql = data.getSql();
        List<Object> args = data.getArgs();
        printPrecompileSqlAndArgs(sql, null, args, null);
        return this.jdbcTemplate.update(sql, new CollectionArgumentPreparedStatementSetter(args));
    }

    @Override
    @SuppressWarnings("unchecked")
    public int updateRecordByPrimaryKeySelective(Object keyValue, Object record, ColumnEngine columnEngine) {
        Map<String, String> columnAliasMap = columnEngine.getColumnAliasMap();
        if (columnAliasMap.size() == 0) {
            columnAliasMap = columnEngine.getTable().getColumnAliasMap();
        }
        ParseData data = this.updateParser.updateObjectByPrimaryKeySelective(columnEngine.getTableName(),
                columnEngine.getPrimaryKeyName(), keyValue, columnAliasMap, record);
        String sql = data.getSql();
        List<Object> args = data.getArgs();
        printPrecompileSqlAndArgs(sql, null, args, null);
        return this.jdbcTemplate.update(sql, new CollectionArgumentPreparedStatementSetter(args));
    }

    @Override
    public int updateRecord(Map<String, ?> record, WhereEngine whereEngine) {
        ParseData data = this.updateParser.updateMap(record, whereEngine);
        String sql = data.getSql();
        List<Object> args = data.getArgs();
        printPrecompileSqlAndArgs(sql, null, args, null);
        return this.jdbcTemplate.update(sql, new CollectionArgumentPreparedStatementSetter(args));
    }

    @Override
    public int updateRecord(Object record, WhereEngine whereEngine) {
        ParseData data = this.updateParser.updateObject(record, whereEngine);
        String sql = data.getSql();
        List<Object> args = data.getArgs();
        printPrecompileSqlAndArgs(sql, null, args, null);
        return this.jdbcTemplate.update(sql, new CollectionArgumentPreparedStatementSetter(args));
    }

    @Override
    public int updateRecordSelective(Map<String, ?> record, WhereEngine whereEngine) {
        ParseData data = this.updateParser.updateMapSelective(record, whereEngine);
        String sql = data.getSql();
        List<Object> args = data.getArgs();
        printPrecompileSqlAndArgs(sql, null, args, null);
        return this.jdbcTemplate.update(sql, new CollectionArgumentPreparedStatementSetter(args));
    }

    @Override
    public int updateRecordSelective(Object record, WhereEngine whereEngine) {
        ParseData data = this.updateParser.updateObjectSelective(record, whereEngine);
        String sql = data.getSql();
        List<Object> args = data.getArgs();
        printPrecompileSqlAndArgs(sql, null, args, null);
        return this.jdbcTemplate.update(sql, new CollectionArgumentPreparedStatementSetter(args));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Model> int batchUpdateRecordsByPrimaryKeys(Object[] records, Class<T> modelClass) {
        Model model = newModel(modelClass);
        ParseData data = this.updateParser.batchUpdateByPrimaryKeys(model.getTableName(),
                model.getPrimaryKeyName(), model.getPrimaryKeyAlias(), model.getColumnAliasMap(), records);
        String sql = data.getSql();
        List<Object> args = data.getArgs();
        printPrecompileSqlAndArgs(sql, null, args, null);
        return this.jdbcTemplate.update(sql, new CollectionArgumentPreparedStatementSetter(args));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Model> int batchUpdateRecordsByPrimaryKeys(Object[] records, String tableName, Class<T> modelClass) {
        Model model = newModel(modelClass);
        ParseData data = this.updateParser.batchUpdateByPrimaryKeys(tableName,
                model.getPrimaryKeyName(), model.getPrimaryKeyAlias(), model.getColumnAliasMap(), records);
        String sql = data.getSql();
        List<Object> args = data.getArgs();
        printPrecompileSqlAndArgs(sql, null, args, null);
        return this.jdbcTemplate.update(sql, new CollectionArgumentPreparedStatementSetter(args));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Model> int batchUpdateRecordsByPrimaryKeys(Collection<?> records, Class<T> modelClass) {
        Model model = newModel(modelClass);
        ParseData data = this.updateParser.batchUpdateByPrimaryKeys(model.getTableName(),
                model.getPrimaryKeyName(), model.getPrimaryKeyAlias(), model.getColumnAliasMap(), records);
        String sql = data.getSql();
        List<Object> args = data.getArgs();
        printPrecompileSqlAndArgs(sql, null, args, null);
        return this.jdbcTemplate.update(sql, new CollectionArgumentPreparedStatementSetter(args));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Model> int batchUpdateRecordsByPrimaryKeys(Collection<?> records, String tableName, Class<T> modelClass) {
        Model model = newModel(modelClass);
        ParseData data = this.updateParser.batchUpdateByPrimaryKeys(tableName,
                model.getPrimaryKeyName(), model.getPrimaryKeyAlias(), model.getColumnAliasMap(), records);
        String sql = data.getSql();
        List<Object> args = data.getArgs();
        printPrecompileSqlAndArgs(sql, null, args, null);
        return this.jdbcTemplate.update(sql, new CollectionArgumentPreparedStatementSetter(args));
    }

    @Override
    public int batchUpdateRecordsByPrimaryKeys(Object[] records, WhereEngine whereEngine) {
        ParseData data = this.updateParser.batchUpdateByPrimaryKeys(records, whereEngine);
        String sql = data.getSql();
        List<Object> args = data.getArgs();
        printPrecompileSqlAndArgs(sql, null, args, null);
        return this.jdbcTemplate.update(sql, new CollectionArgumentPreparedStatementSetter(args));
    }

    @Override
    public int batchUpdateRecordsByPrimaryKeys(Collection<?> records, WhereEngine whereEngine) {
        ParseData data = this.updateParser.batchUpdateByPrimaryKeys(records, whereEngine);
        String sql = data.getSql();
        List<Object> args = data.getArgs();
        printPrecompileSqlAndArgs(sql, null, args, null);
        return this.jdbcTemplate.update(sql, new CollectionArgumentPreparedStatementSetter(args));
    }

    @Override
    @SuppressWarnings("unchecked")
    public int updateOrInsertArgs(Object[] batchArgs, ColumnEngine columnEngine) {
        Map<String, String> columnAliasMap = columnEngine.getColumnAliasMap();
        if (columnAliasMap.size() == 0) {
            columnAliasMap = columnEngine.getTable().getColumnAliasMap();
        }
        String sql = this.updateOrInsertParser.updateOrInsert(columnEngine.getTableName(), columnAliasMap, batchArgs.length);
        printPrecompileSqlAndArgs(sql, null, null, null);
        return this.jdbcTemplate.update(sql, new BatchArgumentPreparedStatementSetter(batchArgs, columnAliasMap.size()));
    }

    @Override
    @SuppressWarnings("unchecked")
    public int updateOrInsertArgs(Collection<?> batchArgs, ColumnEngine columnEngine) {
        Map<String, String> columnAliasMap = columnEngine.getColumnAliasMap();
        if (columnAliasMap.size() == 0) {
            columnAliasMap = columnEngine.getTable().getColumnAliasMap();
        }
        String sql = this.updateOrInsertParser.updateOrInsert(columnEngine.getTableName(), columnAliasMap, batchArgs.size());
        printPrecompileSqlAndArgs(sql, null, null, null);
        return this.jdbcTemplate.update(sql, new BatchArgumentPreparedStatementSetter(batchArgs, columnAliasMap.size()));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Model> int updateOrInsertRecord(Object[] records, Class<T> modelClass) {
        Model model = newModel(modelClass);
        Map<String, String> columnAliasMap = model.getColumnAliasMap();
        String sql = this.updateOrInsertParser.updateOrInsert(model.getTableName(), columnAliasMap, records.length);
        printPrecompileSqlAndArgs(sql, null, null, null);
        return this.jdbcTemplate.update(sql, new BatchArrayRecordPreparedStatementSetter(records, columnAliasMap));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Model> int updateOrInsertRecord(Object[] records, String tableName, Class<T> modelClass) {
        Model model = newModel(modelClass);
        Map<String, String> columnAliasMap = model.getColumnAliasMap();
        String sql = this.updateOrInsertParser.updateOrInsert(tableName, columnAliasMap, records.length);
        printPrecompileSqlAndArgs(sql, null, null, null);
        return this.jdbcTemplate.update(sql, new BatchArrayRecordPreparedStatementSetter(records, columnAliasMap));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Model> int updateOrInsertRecord(Collection<?> records, Class<T> modelClass) {
        Model model = newModel(modelClass);
        Map<String, String> columnAliasMap = model.getColumnAliasMap();
        String sql = this.updateOrInsertParser.updateOrInsert(model.getTableName(), columnAliasMap, records.size());
        printPrecompileSqlAndArgs(sql, null, null, null);
        return this.jdbcTemplate.update(sql, new BatchCollectionRecordPreparedStatementSetter(records, columnAliasMap));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Model> int updateOrInsertRecord(Collection<?> records, String tableName, Class<T> modelClass) {
        Model model = newModel(modelClass);
        Map<String, String> columnAliasMap = model.getColumnAliasMap();
        String sql = this.updateOrInsertParser.updateOrInsert(tableName, columnAliasMap, records.size());
        printPrecompileSqlAndArgs(sql, null, null, null);
        return this.jdbcTemplate.update(sql, new BatchCollectionRecordPreparedStatementSetter(records, columnAliasMap));
    }

    @Override
    @SuppressWarnings("unchecked")
    public int updateOrInsertRecord(Object[] records, ColumnEngine columnEngine) {
        Map<String, String> columnAliasMap = columnEngine.getColumnAliasMap();
        if (columnAliasMap.size() == 0) {
            columnAliasMap = columnEngine.getTable().getColumnAliasMap();
        }
        String sql = this.updateOrInsertParser.updateOrInsert(columnEngine.getTableName(), columnAliasMap, records.length);
        printPrecompileSqlAndArgs(sql, null, null, null);
        return this.jdbcTemplate.update(sql, new BatchArrayRecordPreparedStatementSetter(records, columnAliasMap));
    }

    @Override
    @SuppressWarnings("unchecked")
    public int updateOrInsertRecord(Collection<?> records, ColumnEngine columnEngine) {
        Map<String, String> columnAliasMap = columnEngine.getColumnAliasMap();
        if (columnAliasMap.size() == 0) {
            columnAliasMap = columnEngine.getTable().getColumnAliasMap();
        }
        String sql = this.updateOrInsertParser.updateOrInsert(columnEngine.getTableName(), columnAliasMap, records.size());
        printPrecompileSqlAndArgs(sql, null, null, null);
        return this.jdbcTemplate.update(sql, new BatchCollectionRecordPreparedStatementSetter(records, columnAliasMap));
    }

    @Override
    public <T extends Model> int deleteByPrimaryKey(Object keyValue, Class<T> modelClass) {
        Model model = newModel(modelClass);
        String sql = this.deleteParser.deleteByPrimaryKey(model.getTableName(), model.getPrimaryKeyName());
        printPrecompileSqlAndArgs(sql, null, keyValue, null);
        return this.jdbcTemplate.update(sql, keyValue);
    }

    @Override
    public <T extends Model> int deleteByPrimaryKey(Object keyValue, String tableName, Class<T> modelClass) {
        Model model = newModel(modelClass);
        String sql = this.deleteParser.deleteByPrimaryKey(tableName, model.getPrimaryKeyName());
        printPrecompileSqlAndArgs(sql, null, keyValue, null);
        return this.jdbcTemplate.update(sql, keyValue);
    }

    @Override
    public <T extends Model> int batchDeleteByPrimaryKeys(Object[] keyValues, Class<T> modelClass) {
        Model model = newModel(modelClass);
        String sql = this.deleteParser.batchDeleteByPrimaryKeys(model.getTableName(), model.getPrimaryKeyName(), keyValues.length);
        printPrecompileSqlAndArgs(sql, null, keyValues, null);
        return this.jdbcTemplate.update(sql, keyValues);
    }

    @Override
    public <T extends Model> int batchDeleteByPrimaryKeys(Object[] keyValues, String tableName, Class<T> modelClass) {
        Model model = newModel(modelClass);
        String sql = this.deleteParser.batchDeleteByPrimaryKeys(tableName, model.getPrimaryKeyName(), keyValues.length);
        printPrecompileSqlAndArgs(sql, null, keyValues, null);
        return this.jdbcTemplate.update(sql, keyValues);
    }

    @Override
    public <T extends Model> int batchDeleteByPrimaryKeys(Collection<?> keyValues, Class<T> modelClass) {
        Model model = newModel(modelClass);
        String sql = this.deleteParser.batchDeleteByPrimaryKeys(model.getTableName(), model.getPrimaryKeyName(), keyValues.size());
        printPrecompileSqlAndArgs(sql, null, keyValues, null);
        return this.jdbcTemplate.update(sql, new CollectionArgumentPreparedStatementSetter(keyValues));
    }

    @Override
    public <T extends Model> int batchDeleteByPrimaryKeys(Collection<?> keyValues, String tableName, Class<T> modelClass) {
        Model model = newModel(modelClass);
        String sql = this.deleteParser.batchDeleteByPrimaryKeys(tableName, model.getPrimaryKeyName(), keyValues.size());
        printPrecompileSqlAndArgs(sql, null, keyValues, null);
        return this.jdbcTemplate.update(sql, new CollectionArgumentPreparedStatementSetter(keyValues));
    }

    @Override
    public int delete(WhereEngine whereEngine) {
        ParseData data = this.deleteParser.delete(whereEngine);
        String sql = data.getSql();
        List<Object> args = data.getArgs();
        printPrecompileSqlAndArgs(sql, null, args, null);
        return this.jdbcTemplate.update(sql, new CollectionArgumentPreparedStatementSetter(args));
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
