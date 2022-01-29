#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.interceptor;

import cc.rc.framework.core.props.RcProperties;
import cn.dev33.satoken.interceptor.SaRouteInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.ModelAndView;
import ${package}.module.app.constant.AppConstant;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;

/**
 * 路由拦截器，自定义验证规则
 * http://sa-token.dev33.cn/doc/index.html${symbol_pound}/use/route-check
 *
 * @author linweijian
 */
@RequiredArgsConstructor
public class AppSaTokenRouteInterceptor extends SaRouteInterceptor {

    private final RcProperties rcProperties;


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        /*
        App登录验证
        这里是按路由匹配，除了配置文件中指定的URI外，其他URI都需要登录才行
         */
        SaRouter
                .match(Collections.singletonList(AppConstant.APP_MODULE_CONTEXT_PATH + "/**"))
                .notMatch(rcProperties.getSecurity().getExcludeRoutes())
                .check(StpUtil::checkLogin)
        ;
    }
}
