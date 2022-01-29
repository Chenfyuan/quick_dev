#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.module.sys.controller;

import cc.rc.framework.core.constant.RcConstant;
import cc.rc.framework.core.page.PageParam;
import cc.rc.framework.core.page.PageResult;
import cc.rc.framework.web.model.response.ApiResult;
import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ${package}.module.sys.constant.SysConstant;
import ${package}.module.sys.model.request.AdminListSysLogDTO;
import ${package}.module.sys.model.response.SysLogVO;
import ${package}.module.sys.service.impl.SysLogServiceImpl;
import ${package}.module.sys.util.AdminStpUtil;

import javax.annotation.Resource;


/**
 * @author linweijian
 */
@SaCheckLogin(type = AdminStpUtil.TYPE)
@Slf4j
@Api(value = "后台操作日志管理接口", tags = {"后台操作日志管理接口"})
@RequestMapping(SysConstant.SYS_MODULE_CONTEXT_PATH + RcConstant.Version.HTTP_API_VERSION_V1 + "/sys/logs")
@RestController
public class AdminSysLogController {

    private static final String PERMISSION_PREFIX = "SysLog:";
    
    @Resource
    private SysLogServiceImpl sysLogService;


    @SaCheckPermission(type = AdminStpUtil.TYPE, value = PERMISSION_PREFIX + RcConstant.Permission.RETRIEVE)
    @ApiOperation(value = "分页列表", produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping
    public ApiResult<PageResult<SysLogVO>> list(PageParam pageParam, AdminListSysLogDTO dto) {
        return ApiResult.data(sysLogService.adminList(pageParam, dto));
    }

    @SaCheckPermission(type = AdminStpUtil.TYPE, value = PERMISSION_PREFIX + RcConstant.Permission.RETRIEVE)
    @ApiOperation(value = "详情", produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping(value = "/{id}")
    public ApiResult<SysLogVO> getById(@PathVariable Long id) {
        return ApiResult.data(sysLogService.getOneById(id));
    }

}
