package com;

import com.dt.core.converter.HumpConverter;
import com.dt.core.engine.MySqlEngine;
import com.dt.core.jdbc.JdbcSourceEngine;
import com.dt.core.model.ModelTemplateEngine;
import com.dt.jdbc.core.SpringJdbcEngine;
import com.shiro.model.JurRoleModel;
import com.shiro.model.JurRoleUserModel;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.SQLException;
import java.util.*;

/**
 * Created by 白超 on 2018/7/6.
 */
public class Test {

    public void method1() throws SQLException {
        JdbcSourceEngine engine = JdbcSourceEngine.newMySqlEngine("192.168.3.3",
                "3306", "shiro-manager-spring", "root", "root");

        new ModelTemplateEngine(engine, new HumpConverter())
                .addTable("jur_res", "JurRes")
                .addTable("jur_role", "JurRole")
                .addTable("jur_role_res", "JurRoleRes")
                .addTable("jur_role_user", "JurRoleUser")
                .addTable("zuul_route", "ZuulRoute")
                .process("/", "com.shiro.model");
    }

    public void method2() throws IllegalAccessException, InstantiationException {

        System.out.println("程序开始");

        int count = 1000000;

        long tt;


        tt = 0;
        for (int i = 0; i < count; i++) {
            long start = System.nanoTime();
//            JurResModel model = new JurResModel();
            long end = System.nanoTime() - start;
            tt += end;
        }
        System.out.println(tt + " - " + tt / 1000000);
        System.out.println(tt / count + " - " + tt / 1000000 / count);

        System.out.println("-----------------------------------------");
        tt = 0;
/*        for (int i = 0; i < count; i++) {
            long start = System.nanoTime();
            JurResModel model = JurResModel.class.newInstance();
            long end = System.nanoTime() - start;
            tt += end;
        }*/
        System.out.println(tt + " - " + tt / 1000000);
        System.out.println(tt / count + " - " + tt / 1000000 / count);
        System.out.println("-----------------------------------------");
    }

    public static Map newMap(String key, Object value) {
        Map map = new LinkedHashMap();
        map.put(key, value);
        return map;
    }

    public void method3() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://192.168.0.112:3306/shiro-manager-spring?useSSL=false");
        dataSource.setUsername("root");
        dataSource.setPassword("root");

        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(dataSource);

        SpringJdbcEngine engine = new SpringJdbcEngine();
        engine.setJdbcTemplate(jdbcTemplate);

        Map<String, String> record;
        /*        JurRole role;*/

        List<String> args = new ArrayList<>();
        args.add(UUID.randomUUID().toString());

        List<Map<String, String>> records = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            records.add(Test.newMap("id", UUID.randomUUID().toString()));
        }

/*        List<JurRole> roleList = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            JurRole r = new JurRole();
            r.setId(UUID.randomUUID().toString());
            roleList.add(r);
        }*/

        List<List<String>> lists = new ArrayList<>();
        lists.add(new ArrayList<>());

        int count = 100;
        int r = 0;
        long tt = 0;
        for (int i = 0; i < count; i++) {
            record = new LinkedHashMap<>();
            record.put("id", UUID.randomUUID().toString());
//            role = new JurRole();
//            role.setId(UUID.randomUUID().toString());
            long start = System.nanoTime();
//            r = engine.insertRecord(record, JurRoleModel.class);
//            r = engine.insertRecord(role, JurRoleModel.class);
//            r = engine.insertRecord(role, MySqlEngine.column(JurRoleModel.class));
//            r = engine.insertArgs(MySqlEngine.column(JurRoleModel.class), "65464646", null, null, null, null, null, null, null, null, null, null, null, null, null, null);
//            r = engine.insertArgs(args, MySqlEngine.column(JurRoleModel.class).column(table -> table.id()));
//            r = engine.batchInsertArgs(lists, JurRoleModel.class);
//            r = engine.batchInsertRecords(JurRoleModel.class, new LinkedHashMap<>(), new HashMap<>());
//            r = engine.batchInsertRecords(records, JurRoleModel.class);
//            r = engine.batchInsertRecords(roleList, JurRoleModel.class);
//            r = engine.updateArgsByPrimaryKey("666", JurRoleModel.class, "123", null, null, null, null, null, null, null, null, null, null, null, null, null);
            long end = System.nanoTime() - start;
            System.out.println(end + " - " + end / 1000000);
            tt += end;
            if (r == 0) {
                throw new RuntimeException();
            }
        }
        System.out.println(tt + " = " + tt / 1000000);
    }

    public void method4() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://192.168.3.3:3306/shiro-manager-spring?useSSL=false");
        dataSource.setUsername("root");
        dataSource.setPassword("root");

        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(dataSource);

        SpringJdbcEngine engine = new SpringJdbcEngine();
        engine.setJdbcTemplate(jdbcTemplate);

        int count = 100;
        long tt = 0;
        for (int i = 0; i < count; i++) {
            long start = System.nanoTime();
            engine.queryForList(MySqlEngine.main(JurRoleModel.class)
                    .innerJoin(JurRoleUserModel.class, (on, joinTable, mainTable) -> on
                            .and(joinTable.roleId().equalTo(mainTable.id())))
                    .column(table -> table)
                    .column(JurRoleUserModel.class, table -> table.createTime("createTime2"))
                    .virtualColumn("666", "233"));
            long end = System.nanoTime() - start;
            System.out.println(end + " - " + end / 1000000);
            tt += end;
        }
        System.out.println(tt + " = " + tt / 1000000);
    }

    public void method5() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://192.168.3.3:3306/shiro-manager-spring?useSSL=false");
        dataSource.setUsername("root");
        dataSource.setPassword("root");

        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(dataSource);

        SpringJdbcEngine engine = new SpringJdbcEngine();
        engine.setJdbcTemplate(jdbcTemplate);

/*        JurRole record = new JurRole();
        record.setId("1024");
        record.setDescription("64646");
        record.setParentId("233");
        JurRole record2 = new JurRole();
        record2.setId("2048");
        record2.setDescription("64646");
        record2.setParentId("233");*/

//        Map<String, Object> record = engine.queryByPrimaryKey("1024", MySqlEngine.column(JurRoleModel.class));
//        JurRole record = engine.queryByPrimaryKey("1024", JurRole.class, MySqlEngine.column(JurRoleModel.class));
        Map<String, Object> record = engine.queryPairColumnInMap("id", "parentId", MySqlEngine.main(JurRoleModel.class));

        int count = 1;
        long tt = 0;
        for (int i = 0; i < count; i++) {
            long start = System.nanoTime();

/*            engine.updateRecordSelective(record, MySqlEngine.main(JurRoleModel.class)
                    .innerJoin(JurRoleUserModel.class, (on, joinTable, mainTable) -> on
                            .and(joinTable.roleId().equalTo(mainTable.id())))
                    .where(JurRoleUserModel.class, (condition, table, mainTable) -> condition
                            .and(table.id().equalTo(1024))));*/

/*            engine.updateRecordSelective(record, MySqlEngine.main(JurRoleModel.class)
                    .where((condition, mainTable) -> condition.and(mainTable.id().equalTo("1024"))));*/

//            engine.updateRecordByPrimaryKeySelective("1024", record, JurRoleModel.class);

//            engine.updateOrInsertRecord(new Object[]{record}, MySqlEngine.column(JurRoleModel.class).column(table -> table.id().parentId()));

//            engine.batchUpdateRecordsByPrimaryKeys(JurRoleModel.class, record, record2, record2, record2, record2, record2, record2, record2, record2, record2);

/*            engine.batchUpdateRecordsByPrimaryKeys(new Object[]{record, record2}, MySqlEngine.main(JurRoleModel.class)
                    .innerJoin(JurRoleUserModel.class, (on, joinTable, mainTable) -> on
                            .and(joinTable.roleId().equalTo(mainTable.id())))
                    .column(table -> table.createTime())
                    .where((condition, mainTable) -> condition.and(mainTable.deleteTime().equalTo("4646"))));*/

//            engine.deleteByPrimaryKey("666", JurRoleModel.class);

//            engine.batchDeleteByPrimaryKeys(JurRoleModel.class, "666", "777");

/*            engine.delete(MySqlEngine.main(JurRoleModel.class)
                    .innerJoin(JurRoleUserModel.class, (on, joinTable, mainTable) -> on
                            .and(joinTable.roleId().equalTo(mainTable.id())))
                    .where((condition, mainTable) -> condition
                            .and(mainTable.deleteTime().equalTo("64646464"))));*/

            long end = System.nanoTime() - start;
            System.out.println(end + " - " + end / 1000000);
            tt += end;
        }
        System.out.println(tt + " = " + tt / 1000000);
    }


    public static void main(String[] args) throws SQLException, InstantiationException, IllegalAccessException {

        new Test().method5();
    }
}
