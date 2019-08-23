package pub.avalon.sqlhelper.annotation;

import pub.avalon.sqlhelper.beans.Mode;

import java.lang.annotation.*;

/**
 * @author 白超
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface JdbcMode {
    Mode mode();
}
