package pub.avalon.sqlhelper.interceptor;

import java.lang.reflect.Method;

/**
 * @author 白超
 * @date 2019/8/23
 */
public interface MethodInvocation {

    Method getMethod();

    Object[] getArguments();

    Object proceed() throws Throwable;
}