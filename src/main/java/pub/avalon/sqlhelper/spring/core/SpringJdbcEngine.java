package pub.avalon.sqlhelper.spring.core;

import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import pub.avalon.sqlhelper.core.build.SqlBuilder;
import pub.avalon.sqlhelper.core.engine.*;
import pub.avalon.sqlhelper.spring.beans.JdbcEngine;
import pub.avalon.sqlhelper.spring.utils.JdbcTools;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * SpringJdbc引擎
 *
 * @author 白超
 * @version 1.0
 * @since 2018/7/10
 */
public final class SpringJdbcEngine implements JdbcEngine {

    private String name;

    private JdbcTemplate jdbcTemplate;

    @Override
    public int copyTable(String targetTableName, TableEngine tableEngine) {
        SqlBuilder sqlBuilder = tableEngine.copyTable(targetTableName);
        return this.jdbcTemplate.update(sqlBuilder.getPreparedStatementSql());
    }

    @Override
    public int deleteTable(TableEngine tableEngine) {
        SqlBuilder sqlBuilder = tableEngine.deleteTable();
        return this.jdbcTemplate.update(sqlBuilder.getPreparedStatementSql());
    }

    @Override
    public int renameTable(String newTableName, TableEngine tableEngine) {
        SqlBuilder sqlBuilder = tableEngine.renameTable(newTableName);
        return this.jdbcTemplate.update(sqlBuilder.getPreparedStatementSql());
    }

    @Override
    public boolean isTableExist(TableEngine tableEngine) {
        SqlBuilder sqlBuilder = tableEngine.isTableExist();
        Integer count = this.jdbcTemplate.queryForObject(sqlBuilder.getPreparedStatementSql(), Integer.class);
        return count == null || count > 0;
    }

    @Override
    public Map<String, Object> queryByPrimaryKey(Object keyValue, ColumnIntactEngine columnIntactEngine) {
        SqlBuilder sqlBuilder = columnIntactEngine.queryByPrimaryKey(keyValue);
        List<Map<String, Object>> results = this.jdbcTemplate.query(sqlBuilder.getPreparedStatementSql(),
                new CollectionArgumentPreparedStatementSetter(sqlBuilder.getPreparedStatementArgs()), new RowMapperResultSetExtractor<>(new ColumnMapRowMapper()));
        return JdbcTools.nullableSingleResult(results);
    }

    @Override
    public <T> T queryByPrimaryKey(Object keyValue, Class<T> returnType, ColumnIntactEngine columnIntactEngine) {
        SqlBuilder sqlBuilder = columnIntactEngine.queryByPrimaryKey(keyValue);
        List<T> results = this.jdbcTemplate.query(sqlBuilder.getPreparedStatementSql(),
                new CollectionArgumentPreparedStatementSetter(sqlBuilder.getPreparedStatementArgs()), new ListObjectResultSetExtractor<>(returnType, 1));
        return JdbcTools.nullableSingleResult(results);
    }

    @Override
    public Map<String, Object> queryOne(LimitIntactEngine limitIntactEngine) {
        SqlBuilder sqlBuilder = limitIntactEngine.query();
        List<Map<String, Object>> results = this.jdbcTemplate.query(sqlBuilder.getPreparedStatementSql(),
                new CollectionArgumentPreparedStatementSetter(sqlBuilder.getPreparedStatementArgs()), new RowMapperResultSetExtractor<>(new ColumnMapRowMapper()));
        return JdbcTools.nullableSingleResult(results);
    }

    @Override
    public <T> T queryOne(Class<T> returnType, LimitIntactEngine limitIntactEngine) {
        SqlBuilder sqlBuilder = limitIntactEngine.query();
        List<T> results = this.jdbcTemplate.query(sqlBuilder.getPreparedStatementSql(),
                new CollectionArgumentPreparedStatementSetter(sqlBuilder.getPreparedStatementArgs()), new ListObjectResultSetExtractor<>(returnType, 1));
        return JdbcTools.nullableSingleResult(results);
    }

    @Override
    public List<Map<String, Object>> queryForList(LimitIntactEngine limitIntactEngine) {
        SqlBuilder sqlBuilder = limitIntactEngine.query();
        return this.jdbcTemplate.query(sqlBuilder.getPreparedStatementSql(),
                new CollectionArgumentPreparedStatementSetter(sqlBuilder.getPreparedStatementArgs()), new RowMapperResultSetExtractor<>(new ColumnMapRowMapper()));
    }

    @Override
    public <T> List<T> queryForList(Class<T> returnType, LimitIntactEngine limitIntactEngine) {
        SqlBuilder sqlBuilder = limitIntactEngine.query();
        return this.jdbcTemplate.query(sqlBuilder.getPreparedStatementSql(),
                new CollectionArgumentPreparedStatementSetter(sqlBuilder.getPreparedStatementArgs()), new ListObjectResultSetExtractor<>(returnType, 1));
    }

    @Override
    public int queryCount(LimitIntactEngine limitIntactEngine) {
        SqlBuilder sqlBuilder = limitIntactEngine.queryCount();
        List<Integer> results = this.jdbcTemplate.query(sqlBuilder.getPreparedStatementSql(),
                new CollectionArgumentPreparedStatementSetter(sqlBuilder.getPreparedStatementArgs()),
                new RowMapperResultSetExtractor<>(new SingleColumnRowMapper<>(Integer.class), 1));
        return JdbcTools.countSingleResult(results);
    }

    @Override
    public <K, V> Map<K, V> queryPairColumnInMap(LimitIntactEngine limitIntactEngine) {
        SqlBuilder sqlBuilder = limitIntactEngine.query();
        return this.jdbcTemplate.query(sqlBuilder.getPreparedStatementSql(),
                new CollectionArgumentPreparedStatementSetter(sqlBuilder.getPreparedStatementArgs()),
                new PairColumnResultSetExtractor<>());
    }

    @Override
    public <K, V> Map<K, V> queryPairColumnInMap(int keyIndex, int valueIndex, LimitIntactEngine limitIntactEngine) {
        SqlBuilder sqlBuilder = limitIntactEngine.query();
        return this.jdbcTemplate.query(sqlBuilder.getPreparedStatementSql(),
                new CollectionArgumentPreparedStatementSetter(sqlBuilder.getPreparedStatementArgs()),
                new PairColumnResultSetExtractor<>(keyIndex, valueIndex));
    }

    @Override
    public <K, V> Map<K, V> queryPairColumnInMap(String keyColumnName, String valueColumnName, LimitIntactEngine limitIntactEngine) {
        SqlBuilder sqlBuilder = limitIntactEngine.query();
        return this.jdbcTemplate.query(sqlBuilder.getPreparedStatementSql(),
                new CollectionArgumentPreparedStatementSetter(sqlBuilder.getPreparedStatementArgs()),
                new PairColumnResultSetExtractor<>(keyColumnName, valueColumnName));
    }

    @Override
    public <K> Map<K, Map<String, Object>> queryForListInMap(int keyIndex, LimitIntactEngine limitIntactEngine) {
        SqlBuilder sqlBuilder = limitIntactEngine.query();
        return this.jdbcTemplate.query(sqlBuilder.getPreparedStatementSql(),
                new CollectionArgumentPreparedStatementSetter(sqlBuilder.getPreparedStatementArgs()),
                new ColumnMapResultSetExtractor<>(keyIndex));
    }

    @Override
    public <K> Map<K, Map<String, Object>> queryForListInMap(String keyColumnName, LimitIntactEngine limitIntactEngine) {
        SqlBuilder sqlBuilder = limitIntactEngine.query();
        return this.jdbcTemplate.query(sqlBuilder.getPreparedStatementSql(),
                new CollectionArgumentPreparedStatementSetter(sqlBuilder.getPreparedStatementArgs()),
                new ColumnMapResultSetExtractor<>(keyColumnName));
    }

    @Override
    public <K, T> Map<K, T> queryForListInMap(int keyIndex, Class<T> returnType, LimitIntactEngine limitIntactEngine) {
        SqlBuilder sqlBuilder = limitIntactEngine.query();
        return this.jdbcTemplate.query(sqlBuilder.getPreparedStatementSql(),
                new CollectionArgumentPreparedStatementSetter(sqlBuilder.getPreparedStatementArgs()),
                new ColumnObjectResultSetExtractor<>(keyIndex, returnType));
    }

    @Override
    public <K, T> Map<K, T> queryForListInMap(String keyColumnName, Class<T> returnType, LimitIntactEngine limitIntactEngine) {
        SqlBuilder sqlBuilder = limitIntactEngine.query();
        return this.jdbcTemplate.query(sqlBuilder.getPreparedStatementSql(),
                new CollectionArgumentPreparedStatementSetter(sqlBuilder.getPreparedStatementArgs()),
                new ColumnObjectResultSetExtractor<>(keyColumnName, returnType));
    }

    @Override
    public int insertArgs(Collection<?> args, ColumnEngine columnEngine) {
        SqlBuilder sqlBuilder = columnEngine.insertArgs(args);
        return this.jdbcTemplate.update(sqlBuilder.getPreparedStatementSql(),
                new CollectionArgumentPreparedStatementSetter(sqlBuilder.getPreparedStatementArgs()));
    }

    @Override
    public int insertJavaBean(Object javaBean, ColumnEngine columnEngine) {
        SqlBuilder sqlBuilder = columnEngine.insertJavaBean(javaBean);
        return this.jdbcTemplate.update(sqlBuilder.getPreparedStatementSql(),
                new CollectionArgumentPreparedStatementSetter(sqlBuilder.getPreparedStatementArgs()));
    }

    @Override
    public int insertJavaBeanSelective(Object javaBean, ColumnEngine columnEngine) {
        SqlBuilder sqlBuilder = columnEngine.insertJavaBeanSelective(javaBean);
        return this.jdbcTemplate.update(sqlBuilder.getPreparedStatementSql(),
                new CollectionArgumentPreparedStatementSetter(sqlBuilder.getPreparedStatementArgs()));
    }

    @Override
    public int batchInsertJavaBeans(Collection<?> javaBeans, ColumnEngine columnEngine) {
        SqlBuilder sqlBuilder = columnEngine.batchInsertJavaBeans(javaBeans);
        return this.jdbcTemplate.update(sqlBuilder.getPreparedStatementSql(),
                new CollectionArgumentPreparedStatementSetter(sqlBuilder.getPreparedStatementArgs()));
    }

    @Override
    public int updateArgsByPrimaryKey(Object keyValue, Collection<?> args, ColumnIntactEngine columnIntactEngine) {
        SqlBuilder sqlBuilder = columnIntactEngine.updateArgsByPrimaryKey(keyValue, args);
        return this.jdbcTemplate.update(sqlBuilder.getPreparedStatementSql(),
                new CollectionArgumentPreparedStatementSetter(sqlBuilder.getPreparedStatementArgs()));
    }

    @Override
    public int updateJavaBeanByPrimaryKey(Object keyValue, Object javaBean, ColumnIntactEngine columnIntactEngine) {
        SqlBuilder sqlBuilder = columnIntactEngine.updateJavaBeanByPrimaryKey(keyValue, javaBean);
        return this.jdbcTemplate.update(sqlBuilder.getPreparedStatementSql(),
                new CollectionArgumentPreparedStatementSetter(sqlBuilder.getPreparedStatementArgs()));
    }

    @Override
    public int updateJavaBeanByPrimaryKeySelective(Object keyValue, Object javaBean, ColumnIntactEngine columnIntactEngine) {
        SqlBuilder sqlBuilder = columnIntactEngine.updateJavaBeanByPrimaryKeySelective(keyValue, javaBean);
        return this.jdbcTemplate.update(sqlBuilder.getPreparedStatementSql(),
                new CollectionArgumentPreparedStatementSetter(sqlBuilder.getPreparedStatementArgs()));
    }

    @Override
    public int updateJavaBean(Object javaBean, WhereIntactEngine whereIntactEngine) {
        SqlBuilder sqlBuilder = whereIntactEngine.updateJavaBean(javaBean);
        return this.jdbcTemplate.update(sqlBuilder.getPreparedStatementSql(),
                new CollectionArgumentPreparedStatementSetter(sqlBuilder.getPreparedStatementArgs()));
    }

    @Override
    public int updateJavaBeanSelective(Object javaBean, WhereIntactEngine whereIntactEngine) {
        SqlBuilder sqlBuilder = whereIntactEngine.updateJavaBeanSelective(javaBean);
        return this.jdbcTemplate.update(sqlBuilder.getPreparedStatementSql(),
                new CollectionArgumentPreparedStatementSetter(sqlBuilder.getPreparedStatementArgs()));
    }

    @Override
    public int batchUpdateJavaBeansByPrimaryKeys(Collection<?> javaBeans, ColumnIntactEngine columnIntactEngine) {
        SqlBuilder sqlBuilder = columnIntactEngine.batchUpdateJavaBeansByPrimaryKeys(javaBeans);
        return this.jdbcTemplate.update(sqlBuilder.getPreparedStatementSql(),
                new CollectionArgumentPreparedStatementSetter(sqlBuilder.getPreparedStatementArgs()));
    }

    @Override
    public int updateOrInsertJavaBeans(Collection<?> javaBeans, ColumnIntactEngine columnIntactEngine) {
        SqlBuilder sqlBuilder = columnIntactEngine.updateOrInsertJavaBeans(javaBeans);
        return this.jdbcTemplate.update(sqlBuilder.getPreparedStatementSql(),
                new CollectionArgumentPreparedStatementSetter(sqlBuilder.getPreparedStatementArgs()));
    }

    @Override
    public int deleteByPrimaryKey(Object keyValue, DeleteEngine deleteEngine) {
        SqlBuilder sqlBuilder = deleteEngine.deleteByPrimaryKey(keyValue);
        return this.jdbcTemplate.update(sqlBuilder.getPreparedStatementSql(),
                new CollectionArgumentPreparedStatementSetter(sqlBuilder.getPreparedStatementArgs()));
    }

    @Override
    public int batchDeleteByPrimaryKeys(Collection<?> keyValues, DeleteEngine deleteEngine) {
        SqlBuilder sqlBuilder = deleteEngine.batchDeleteByPrimaryKeys(keyValues);
        return this.jdbcTemplate.update(sqlBuilder.getPreparedStatementSql(),
                new CollectionArgumentPreparedStatementSetter(sqlBuilder.getPreparedStatementArgs()));
    }

    @Override
    public int delete(WhereIntactEngine whereIntactEngine) {
        SqlBuilder sqlBuilder = whereIntactEngine.delete();
        return this.jdbcTemplate.update(sqlBuilder.getPreparedStatementSql(),
                new CollectionArgumentPreparedStatementSetter(sqlBuilder.getPreparedStatementArgs()));
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
