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
import cn.hutool.core.collection.CollUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ${package}.module.sys.constant.SysConstant;
import ${package}.module.sys.model.request.AdminInsertOrUpdateSysRoleDTO;
import ${package}.module.sys.model.request.AdminListSysRoleDTO;
import ${package}.module.sys.model.response.SysRoleVO;
import ${package}.module.sys.service.impl.SysRoleServiceImpl;
import ${package}.module.sys.service.impl.SysUserRoleRelationServiceImpl;
import ${package}.module.sys.util.AdminStpUtil;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;


/**
 * @author linweijian
 */
@SaCheckLogin(type = AdminStpUtil.TYPE)
@Slf4j
@Api(value = "后台角色管理接口", tags = {"后台角色管理接口"})
@RequestMapping(SysConstant.SYS_MODULE_CONTEXT_PATH + RcConstant.Version.HTTP_API_VERSION_V1 + "/sys/roles")
@RestController
public class AdminSysRoleController {

    private static final String PERMISSION_PREFIX = "SysRole:";

    @Resource
    private SysRoleServiceImpl sysRoleServiceImpl;

    @Resource
    private SysUserRoleRelationServiceImpl sysUserRoleRelationServiceImpl;

    @Resource
    private StringRedisTemplate stringRedisTemplate;


    @SaCheckPermission(type = AdminStpUtil.TYPE, value = PERMISSION_PREFIX + RcConstant.Permission.RETRIEVE)
    @ApiOperation(value = "分页列表", produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping
    public ApiResult<PageResult<SysRoleVO>> list(PageParam pageParam, AdminListSysRoleDTO dto) {
        return ApiResult.data(sysRoleServiceImpl.adminList(pageParam, dto));
    }

    @SaCheckPermission(type = AdminStpUtil.TYPE, value = PERMISSION_PREFIX + RcConstant.Permission.RETRIEVE)
    @ApiOperation(value = "详情", produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping(value = "/{id}")
    public ApiResult<SysRoleVO> getById(@PathVariable Long id) {
        return ApiResult.data(sysRoleServiceImpl.getOneById(id));
    }

    @SaCheckPermission(type = AdminStpUtil.TYPE, value = PERMISSION_PREFIX + RcConstant.Permission.CREATE)
    @ApiOperation(value = "新增", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping
    public ApiResult<?> insert(@RequestBody @Valid AdminInsertOrUpdateSysRoleDTO dto) {
        sysRoleServiceImpl.adminInsert(dto);

        return ApiResult.success();
    }

    @SaCheckPermission(type = AdminStpUtil.TYPE, value = PERMISSION_PREFIX + RcConstant.Permission.UPDATE)
    @ApiOperation(value = "编辑", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PutMapping(value = "/{id}")
    public ApiResult<?> update(@PathVariable Long id, @RequestBody @Valid AdminInsertOrUpdateSysRoleDTO dto) {
        dto.setId(id);
        sysRoleServiceImpl.adminUpdate(dto);

        /*
        清除该角色下所有用户, 菜单相关的缓存
         */
        List<Long> userIds = sysUserRoleRelationServiceImpl.listUserIdByRoleIds(CollUtil.newArrayList(dto.getId()));
        userIds.forEach(
                userId -> {
                    String redisKey;

                    redisKey = String.format(SysConstant.REDIS_KEY_SIDE_MENU_BY_USERID, userId);
                    stringRedisTemplate.delete(redisKey);

                    redisKey = String.format(SysConstant.REDIS_KEY_VISIBLE_MENU_BY_USERID, userId);
                    stringRedisTemplate.delete(redisKey);
                }
        );

        return ApiResult.success();
    }

    @SaCheckPermission(type = AdminStpUtil.TYPE, value = PERMISSION_PREFIX + RcConstant.Permission.DELETE)
    @ApiOperation(value = "删除", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @DeleteMapping
    public ApiResult<?> delete(@RequestBody @Valid IdsDTO<Long> dto) {
        sysRoleServiceImpl.adminDelete(dto.getIds());

        return ApiResult.success();
    }

}
