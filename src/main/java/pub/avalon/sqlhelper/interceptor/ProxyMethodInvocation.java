package pub.avalon.sqlhelper.interceptor;

/**
 * @author 白超
 * @date 2019/8/23
 */
public interface ProxyMethodInvocation extends MethodInvocation {

    Object getProxy();
}