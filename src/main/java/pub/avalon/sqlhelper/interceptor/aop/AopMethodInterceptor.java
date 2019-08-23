package pub.avalon.sqlhelper.interceptor.aop;

import pub.avalon.sqlhelper.interceptor.MethodInvocation;

/**
 * @author 白超
 * @date 2019/8/23
 */
public interface AopMethodInterceptor {

    Object invoke(MethodInvocation methodInvocation) throws Throwable;
}