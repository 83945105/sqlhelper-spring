package pub.avalon.sqlhelper.spring;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pub.avalon.sqlhelper.annotation.JdbcEngineMode;
import pub.avalon.sqlhelper.annotation.JdbcSqlMode;
import pub.avalon.sqlhelper.core.engine.SqlEngine;
import pub.avalon.sqlhelper.spring.beans.JdbcEngine;
import pub.avalon.sqlhelper.spring.core.SpringJdbcEngine;

import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Created by 白超 on 2018/8/27.
 */
public class MySqlDynamicQueryTest {

    @Test
    void Test() {
        Method[] methods = JdbcEngine.class.getDeclaredMethods();
        for (Method method : methods) {
            Class<?>[] parameterTypes = method.getParameterTypes();
            JdbcSqlMode jdbcSqlMode = method.getAnnotation(JdbcSqlMode.class);
            JdbcEngineMode jdbcEngineMode = method.getAnnotation(JdbcEngineMode.class);
            if (jdbcSqlMode != null) {
                Assertions.assertEquals(parameterTypes[jdbcSqlMode.sqlIndex()], String.class);
                continue;
            }
            if (jdbcEngineMode != null) {
                Assertions.assertTrue(SqlEngine.class.isAssignableFrom(parameterTypes[jdbcEngineMode.engineIndex()]));
                continue;
            }
            throw new RuntimeException("没有找到指定注解 " + method.getName());
        }
        methods = SpringJdbcEngine.class.getDeclaredMethods();
        for (Method method : methods) {
            if (new ArrayList<String>() {{
                add("getName");
                add("setName");
                add("setJdbcTemplate");
            }}.contains(method.getName())) {
                continue;
            }
            Class<?>[] parameterTypes = method.getParameterTypes();
            JdbcSqlMode jdbcSqlMode = method.getAnnotation(JdbcSqlMode.class);
            JdbcEngineMode jdbcEngineMode = method.getAnnotation(JdbcEngineMode.class);
            if (jdbcSqlMode != null) {
                Assertions.assertEquals(parameterTypes[jdbcSqlMode.sqlIndex()], String.class);
                continue;
            }
            if (jdbcEngineMode != null) {
                Assertions.assertTrue(SqlEngine.class.isAssignableFrom(parameterTypes[jdbcEngineMode.engineIndex()]));
                continue;
            }
            throw new RuntimeException("没有找到指定注解 " + method.getName());
        }
    }
}
