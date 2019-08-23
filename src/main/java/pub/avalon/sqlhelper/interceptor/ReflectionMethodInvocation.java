package pub.avalon.sqlhelper.interceptor;

import pub.avalon.sqlhelper.interceptor.aop.AfterMethodInterceptor;
import pub.avalon.sqlhelper.interceptor.aop.BeforeMethodInterceptor;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author 白超
 * @date 2019/8/23
 */
public class ReflectionMethodInvocation implements ProxyMethodInvocation {

    protected final Object proxy;
    protected final Object target;
    protected final Method method;
    protected Object[] arguments;
    protected final List<BeforeMethodInterceptor> beforeMethodInterceptors;
    protected final List<AfterMethodInterceptor> afterMethodInterceptors;

    private int currentBeforeInterceptorIndex = -1;
    private int currentAfterInterceptorIndex = -1;

    public ReflectionMethodInvocation(Object proxy, Object target, Method method, Object[] arguments, List<BeforeMethodInterceptor> beforeMethodInterceptors, List<AfterMethodInterceptor> afterMethodInterceptors) {
        this.proxy = proxy;
        this.target = target;
        this.method = method;
        this.arguments = arguments;
        this.beforeMethodInterceptors = beforeMethodInterceptors;
        this.afterMethodInterceptors = afterMethodInterceptors;
    }

    @Override
    public Object getProxy() {
        return proxy;
    }

    @Override
    public Method getMethod() {
        return method;
    }

    @Override
    public Object[] getArguments() {
        return arguments;
    }

    @Override
    public Object proceed() throws Throwable {
        if (currentBeforeInterceptorIndex == this.beforeMethodInterceptors.size() - 1) {
            Object invokeResult = method.invoke(target, arguments);
            if (currentAfterInterceptorIndex == this.afterMethodInterceptors.size() - 1) {
                return invokeResult;
            }
            return afterMethodInterceptors.get(++currentAfterInterceptorIndex).invoke(this);
        }
        return beforeMethodInterceptors.get(++currentBeforeInterceptorIndex).invoke(this);
    }

    protected Object invokeOriginal() throws Throwable {
        return method.invoke(target, arguments);
    }
}