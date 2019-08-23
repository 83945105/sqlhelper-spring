package pub.avalon.sqlhelper.interceptor;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import pub.avalon.sqlhelper.interceptor.aop.AfterMethodInterceptor;
import pub.avalon.sqlhelper.interceptor.aop.BeforeMethodInterceptor;
import pub.avalon.sqlhelper.spring.beans.JdbcEngine;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author 白超
 * @date 2019/8/23
 */
public class DynamicAdvisedInterceptor implements MethodInterceptor {

    protected Object target;
    protected List<BeforeMethodInterceptor> beforeMethodInterceptors = Collections.emptyList();
    protected List<AfterMethodInterceptor> afterMethodInterceptors = Collections.emptyList();

    public JdbcEngine getInstance(JdbcEngine jdbcEngine) {
        this.target = jdbcEngine;
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(jdbcEngine.getClass());
        enhancer.setCallback(this);
        return (JdbcEngine) enhancer.create();
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        return new CglibMethodInvocation(obj, target, method, args, beforeMethodInterceptors, afterMethodInterceptors, proxy).proceed();
    }

    public DynamicAdvisedInterceptor addBeforeMethodInterceptor(BeforeMethodInterceptor beforeMethodInterceptor) {
        if (beforeMethodInterceptor == null) {
            return this;
        }
        if (beforeMethodInterceptors.size() == 0) {
            beforeMethodInterceptors = new ArrayList<>(1);
        }
        beforeMethodInterceptors.add(beforeMethodInterceptor);
        return this;
    }

    public DynamicAdvisedInterceptor addAfterMethodInterceptor(AfterMethodInterceptor afterMethodInterceptor) {
        if (afterMethodInterceptor == null) {
            return this;
        }
        if (afterMethodInterceptors.size() == 0) {
            afterMethodInterceptors = new ArrayList<>(1);
        }
        afterMethodInterceptors.add(afterMethodInterceptor);
        return this;
    }
}