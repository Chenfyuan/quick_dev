#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.interceptor;

import cc.rc.framework.core.context.UserContext;
import cc.rc.framework.core.context.UserContextHolder;
import cc.rc.framework.web.util.IPUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;
import ${package}.module.sys.util.AdminStpUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 从请求头解析并赋值到用户上下文
 * 其实就是"DefaultSaTokenParseInterceptor"改个名, 工具类换成"AdminStpUtil"
 * @author linweijian
 */
@Slf4j
public class AdminSaTokenParseInterceptor implements AsyncHandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        if (handler instanceof ResourceHttpRequestHandler) {
            // 直接放行静态资源
            return true;
        }

        // 从请求头解析用户上下文
        if (AdminStpUtil.isLogin()) {
            UserContext currentUser = (UserContext) AdminStpUtil.getSession().get("userContext");
            log.debug("[SA-Token][Admin] 从请求头解析出用户上下文 >> {}", currentUser);

            currentUser
                    .setClientIP(IPUtil.getClientIPAddress(request))
            ;
            UserContextHolder.setUserContext(currentUser);
        } else {
            UserContextHolder.setUserContext(null);
        }

        return true;
    }

}
