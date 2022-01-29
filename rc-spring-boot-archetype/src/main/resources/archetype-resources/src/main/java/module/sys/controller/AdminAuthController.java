#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.module.sys.controller;


import cc.rc.framework.core.constant.RcConstant;
import cc.rc.framework.core.context.UserContext;
import cc.rc.framework.core.context.UserContextHolder;
import cc.rc.framework.web.model.response.ApiResult;
import cn.dev33.satoken.annotation.SaCheckLogin;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ${package}.module.sys.constant.SysConstant;
import ${package}.module.sys.enums.UserTypeEnum;
import ${package}.module.sys.model.request.SysUserLoginDTO;
import ${package}.module.sys.model.response.SysUserLoginVO;
import ${package}.module.sys.service.impl.SysMenuServiceImpl;
import ${package}.module.sys.service.impl.SysUserService;
import ${package}.module.sys.util.AdminStpUtil;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;


/**
 * @author linweijian
 */
@Slf4j
@Api(value = "SaaS后台管理鉴权接口", tags = {"SaaS后台管理鉴权接口"})
@RequestMapping(SysConstant.SYS_MODULE_CONTEXT_PATH + RcConstant.Version.HTTP_API_VERSION_V1 + "/auth")
@RestController
public class AdminAuthController {

    @Resource
    private SysUserService sysUserService;

    @Resource
    private SysMenuServiceImpl sysMenuServiceImpl;


    @ApiOperation(value = "登录", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping(value = "/login")
    public ApiResult<?> login(@RequestBody @Valid SysUserLoginDTO dto) {
        // RPC调用, 失败抛异常, 成功返回用户信息
        SysUserLoginVO userInfo = sysUserService.adminLogin(dto);

        // 构造用户上下文
        UserContext userContext = UserContext.builder()
                .userId(userInfo.getId())
                .userName(userInfo.getUsername())
                .userPhoneNo(userInfo.getPhoneNo())
                .userType(UserTypeEnum.ADMIN_USER)
                .extraData(null)
                .rolesIds(userInfo.getRoleIds())
                .roles(userInfo.getRoles())
                .permissions(userInfo.getPermissions())
                .relationalTenant(userInfo.getRelationalTenant())
                .build();

        // 注册到SA-Token
        AdminStpUtil.login(userInfo.getId(), dto.getRememberMe());
        AdminStpUtil.getSession().set("userContext", userContext);


        // 返回登录token
        Map<String, Object> tokenInfo = new HashMap<>(16);
        tokenInfo.put("tokenName", AdminStpUtil.getTokenName());
        tokenInfo.put("tokenValue", AdminStpUtil.getTokenValue());
        tokenInfo.put("roles", userInfo.getRoles());
        tokenInfo.put("permissions", userInfo.getPermissions());

        return ApiResult.data("登录成功", tokenInfo);
    }

    @SaCheckLogin(type = AdminStpUtil.TYPE)
    @ApiOperation(value = "登出", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping(value = "/logout")
    public ApiResult<?> logout() {
        sysMenuServiceImpl.cleanMenuCacheInRedis(UserContextHolder.getUserId());

        AdminStpUtil.logout();
        UserContextHolder.setUserContext(null);

        return ApiResult.success();
    }

}
