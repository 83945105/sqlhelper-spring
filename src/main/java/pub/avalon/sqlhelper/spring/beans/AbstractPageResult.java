package pub.avalon.sqlhelper.spring.beans;

import pub.avalon.beans.LimitHandler;

/**
 * @author 白超
 * @version 1.0
 * @since 2018/7/11
 */
public abstract class AbstractPageResult {

    protected LimitHandler limit;

    public LimitHandler getLimit() {
        return limit;
    }

    public void setLimit(LimitHandler limit) {
        this.limit = limit;
    }
}
