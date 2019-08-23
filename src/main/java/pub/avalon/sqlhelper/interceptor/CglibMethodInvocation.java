package pub.avalon.sqlhelper.interceptor;

import net.sf.cglib.proxy.MethodProxy;
import pub.avalon.sqlhelper.interceptor.aop.AfterMethodInterceptor;
import pub.avalon.sqlhelper.interceptor.aop.AopMethodInterceptor;
import pub.avalon.sqlhelper.interceptor.aop.BeforeMethodInterceptor;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author 白超
 * @date 2019/8/23
 */
public class CglibMethodInvocation extends ReflectionMethodInvocation {

    private MethodProxy methodProxy;

    public CglibMethodInvocation(Object proxy, Object target, Method method, Object[] arguments, List<BeforeMethodInterceptor> beforeMethodInterceptors, List<AfterMethodInterceptor> afterMethodInterceptors, MethodProxy methodProxy) {
        super(proxy, target, method, arguments, beforeMethodInterceptors, afterMethodInterceptors);
        this.methodProxy = methodProxy;
    }

    @Override
    protected Object invokeOriginal() throws Throwable {
        return methodProxy.invoke(target, arguments);
    }
}