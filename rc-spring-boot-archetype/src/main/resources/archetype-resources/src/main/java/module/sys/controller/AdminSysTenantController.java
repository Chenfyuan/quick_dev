#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.module.sys.controller;

import cc.rc.framework.core.constant.RcConstant;
import cc.rc.framework.core.page.PageParam;
import cc.rc.framework.core.page.PageResult;
import cc.rc.framework.web.model.request.IdsDTO;
import cc.rc.framework.web.model.response.ApiResult;
import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ${package}.module.sys.constant.SysConstant;
import ${package}.module.sys.model.request.AdminInsertSysTenantDTO;
import ${package}.module.sys.model.request.AdminListSysTenantDTO;
import ${package}.module.sys.model.request.AdminUpdateSysTenantDTO;
import ${package}.module.sys.model.response.SysTenantVO;
import ${package}.module.sys.service.impl.SysTenantServiceImpl;
import ${package}.module.sys.util.AdminStpUtil;

import javax.annotation.Resource;
import javax.validation.Valid;


/**
 * @author linweijian
 */
@SaCheckLogin(type = AdminStpUtil.TYPE)
@Slf4j
@Api(value = "系统租户管理接口", tags = {"系统租户管理接口"})
@RequestMapping(SysConstant.SYS_MODULE_CONTEXT_PATH + RcConstant.Version.HTTP_API_VERSION_V1 + "/sys/tenants")
@RestController
public class AdminSysTenantController {

    private static final String PERMISSION_PREFIX = "SysTenant:";
    
    @Resource
    private SysTenantServiceImpl sysTenantServiceImpl;


    @SaCheckPermission(type = AdminStpUtil.TYPE, value = PERMISSION_PREFIX + RcConstant.Permission.RETRIEVE)
    @ApiOperation(value = "分页列表", produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping
    public ApiResult<PageResult<SysTenantVO>> list(PageParam pageParam, AdminListSysTenantDTO dto) {
        return ApiResult.data(sysTenantServiceImpl.adminList(pageParam, dto));
    }

    @SaCheckPermission(type = AdminStpUtil.TYPE, value = PERMISSION_PREFIX + RcConstant.Permission.RETRIEVE)
    @ApiOperation(value = "详情", produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping(value = "/{id}")
    public ApiResult<SysTenantVO> getById(@PathVariable Long id) {
        return ApiResult.data(sysTenantServiceImpl.getOneById(id));
    }

    @SaCheckPermission(type = AdminStpUtil.TYPE, value = PERMISSION_PREFIX + RcConstant.Permission.CREATE)
    @ApiOperation(value = "新增", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping
    public ApiResult<?> insert(@RequestBody @Valid AdminInsertSysTenantDTO dto) {
        sysTenantServiceImpl.adminInsert(dto);

        return ApiResult.success();
    }

    @SaCheckPermission(type = AdminStpUtil.TYPE, value = PERMISSION_PREFIX + RcConstant.Permission.UPDATE)
    @ApiOperation(value = "编辑", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PutMapping(value = "/{id}")
    public ApiResult<?> update(@PathVariable Long id, @RequestBody @Valid AdminUpdateSysTenantDTO dto) {
        dto.setId(id);
        sysTenantServiceImpl.adminUpdate(dto);

        return ApiResult.success();
    }

    @SaCheckPermission(type = AdminStpUtil.TYPE, value = PERMISSION_PREFIX + RcConstant.Permission.DELETE)
    @ApiOperation(value = "删除", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @DeleteMapping
    public ApiResult<?> delete(@RequestBody @Valid IdsDTO<Long> dto) {
        sysTenantServiceImpl.adminDelete(dto.getIds());

        return ApiResult.success();
    }

}
