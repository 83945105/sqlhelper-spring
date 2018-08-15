package com.shiro;

import com.dt.core.engine.MySqlEngine;
import com.dt.core.norm.Engine;
import com.dt.jdbc.core.SpringJdbcEngine;
import com.shiro.model.JurRoleModel;
import com.shiro.model.JurRoleUserModel;
import org.junit.jupiter.api.Test;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.util.Map;

/**
 * Created by 白超 on 2018/8/14.
 */
public class ThymeleafTest {

    @Test
    void queryForListTest(Map<String, Object> params) {

        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setCharacterEncoding("utf-8");
        resolver.setPrefix("sql-templates/");
        resolver.setSuffix(".text");
        resolver.setTemplateMode(TemplateMode.TEXT);
        resolver.setCacheable(true);

        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(resolver);
        Context context;

        int startCount = 100;
        int count = 1100;
        int doCount = 0;
        Long tt = 0L;

        for (int i = 0; i < count; i++) {

            Long startTime = System.nanoTime();

            context = new Context();

            Engine engine = MySqlEngine.main(JurRoleModel.class)
                    .innerJoin(JurRoleUserModel.class, (on, joinTable, mainTable) -> on.and(joinTable.roleId().equalTo(mainTable.id())))
                    .where((condition, mainTable) -> condition
                            .and(mainTable.id().equalTo("").createTime().greaterThan(""))
                            .or(mainTable.index().between("", "")))
                    .sort(table -> table.createTime().asc());
            context.setVariable("engine", engine);

            String text = templateEngine.process("queryForList", context);

            Long time = System.nanoTime() - startTime;
            if (i >= startCount) {
                tt += time;
                doCount++;
            }
            System.out.println("耗时:" + time + "纳秒 " + time / 1000000 + "毫秒");
//            System.out.println(text);
        }
        System.out.println("累计耗时:" + tt + "纳秒 " + tt / 1000000 + "毫秒 共计算" + doCount + "次");
        System.out.println("累计平均耗时:" + tt / (count - startCount) + "纳秒 " + tt / 1000000 / (count - startCount) + "毫秒 共计算" + doCount + "次");

    }

    @Test
    void textTemplateTest() {

        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setCharacterEncoding("utf-8");
        resolver.setPrefix("templates/");
        resolver.setSuffix(".text");
        resolver.setTemplateMode(TemplateMode.TEXT);
        resolver.setCacheable(true);

        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(resolver);
        Context context;

        for (int i = 0; i < 100; i++) {

            Long startTime = System.nanoTime();

            context = new Context();
            context.setVariable("name", "文本测试" + i);
            String text = templateEngine.process("TextTemplateTest", context);
            Long endTime = System.nanoTime() - startTime;
            System.out.println(text);

            System.out.println("耗时:" + endTime + "纳秒 " + endTime / 1000000 + "毫秒");

        }
    }

}
