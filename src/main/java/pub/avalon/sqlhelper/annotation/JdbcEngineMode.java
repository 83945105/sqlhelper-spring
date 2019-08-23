package pub.avalon.sqlhelper.annotation;

import pub.avalon.sqlhelper.beans.Mode;

import java.lang.annotation.*;

/**
 * @author 白超
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@JdbcMode(mode = Mode.ENGINE)
public @interface JdbcEngineMode {

    int engineIndex();
}
