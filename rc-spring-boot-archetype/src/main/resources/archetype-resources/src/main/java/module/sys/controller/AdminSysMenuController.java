#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.module.sys.controller;

import cc.rc.framework.core.constant.RcConstant;
import cc.rc.framework.core.context.UserContextHolder;
import cc.rc.framework.web.model.request.IdsDTO;
import cc.rc.framework.web.model.response.ApiResult;
import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.json.JSONUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ${package}.module.sys.constant.SysConstant;
import ${package}.module.sys.model.request.AdminInsertOrUpdateSysMenuDTO;
import ${package}.module.sys.model.request.AdminListSysMenuDTO;
import ${package}.module.sys.model.response.SysMenuVO;
import ${package}.module.sys.service.impl.SysMenuServiceImpl;
import ${package}.module.sys.service.impl.SysParamServiceImpl;
import ${package}.module.sys.util.AdminStpUtil;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * @author linweijian
 */
@SaCheckLogin(type = AdminStpUtil.TYPE)
@Slf4j
@Api(value = "后台菜单管理接口", tags = {"后台菜单管理接口"})
@RequestMapping(SysConstant.SYS_MODULE_CONTEXT_PATH + RcConstant.Version.HTTP_API_VERSION_V1 + "/sys/menus")
@RestController
public class AdminSysMenuController {

    private static final String PERMISSION_PREFIX = "SysMenu:";

    @Resource
    private SysMenuServiceImpl sysMenuServiceImpl;

    @Resource
    private SysParamServiceImpl sysParamServiceImpl;

    @Resource
    private StringRedisTemplate stringRedisTemplate;


    @SaCheckPermission(type = AdminStpUtil.TYPE, value = PERMISSION_PREFIX + RcConstant.Permission.RETRIEVE)
    @ApiOperation(value = "列表", produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping
    public ApiResult<List<SysMenuVO>> list(AdminListSysMenuDTO dto) {
        return ApiResult.data(sysMenuServiceImpl.adminList(dto));
    }

    @SaCheckPermission(type = AdminStpUtil.TYPE, value = PERMISSION_PREFIX + RcConstant.Permission.RETRIEVE)
    @ApiOperation(value = "详情", produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping(value = "/{id}")
    public ApiResult<SysMenuVO> getById(@PathVariable Long id) {
        return ApiResult.data(sysMenuServiceImpl.getOneById(id));
    }

    @SaCheckPermission(type = AdminStpUtil.TYPE, value = PERMISSION_PREFIX + RcConstant.Permission.CREATE)
    @ApiOperation(value = "新增", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping
    public ApiResult<?> insert(@RequestBody @Valid AdminInsertOrUpdateSysMenuDTO dto) {
        sysMenuServiceImpl.adminInsert(dto);
        sysMenuServiceImpl.cleanMenuCacheInRedis();

        return ApiResult.success();
    }

    @SaCheckPermission(type = AdminStpUtil.TYPE, value = PERMISSION_PREFIX + RcConstant.Permission.UPDATE)
    @ApiOperation(value = "编辑", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PutMapping(value = "/{id}")
    public ApiResult<?> update(@PathVariable Long id, @RequestBody @Valid AdminInsertOrUpdateSysMenuDTO dto) {
        dto.setId(id);
        sysMenuServiceImpl.adminUpdate(dto);
        sysMenuServiceImpl.cleanMenuCacheInRedis();

        return ApiResult.success();
    }

    @SaCheckPermission(type = AdminStpUtil.TYPE, value = PERMISSION_PREFIX + RcConstant.Permission.DELETE)
    @ApiOperation(value = "删除", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @DeleteMapping
    public ApiResult<?> delete(@RequestBody @Valid IdsDTO<Long> dto) {
        sysMenuServiceImpl.adminDelete(dto.getIds());
        sysMenuServiceImpl.cleanMenuCacheInRedis();

        return ApiResult.success();
    }

    @ApiOperation(value = "取当前账号可见侧边菜单", produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("/side")
    public ApiResult<List<SysMenuVO>> adminListSideMenu() {
        String redisKey = String.format(SysConstant.REDIS_KEY_SIDE_MENU_BY_USERID, UserContextHolder.getUserContext().getUserId());
        Object redisValue = stringRedisTemplate.opsForValue().get(redisKey);

        if (redisValue == null) {
            redisValue = sysMenuServiceImpl.adminListSideMenu();
            // 记录到缓存
            String sysMenuCacheDuration = sysParamServiceImpl.getParamValueByKey(SysConstant.PARAM_KEY_CACHE_MENU_DURATION, "30");
            stringRedisTemplate.opsForValue().set(redisKey, JSONUtil.toJsonStr(redisValue), Integer.parseInt(sysMenuCacheDuration), TimeUnit.MINUTES);
        } else {
            redisValue = JSONUtil.parse(redisValue).toBean(ArrayList.class);
        }

        return ApiResult.data((List<SysMenuVO>) redisValue);
    }

    @ApiOperation(value = "取当前账号所有可见菜单", produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("/all")
    public ApiResult<List<SysMenuVO>> adminListVisibleMenu() {
        String redisKey = String.format(SysConstant.REDIS_KEY_VISIBLE_MENU_BY_USERID, UserContextHolder.getUserContext().getUserId());
        Object redisValue = stringRedisTemplate.opsForValue().get(redisKey);

        if (redisValue == null) {
            redisValue = sysMenuServiceImpl.adminListVisibleMenu();
            // 记录到缓存
            String sysMenuCacheDuration = sysParamServiceImpl.getParamValueByKey(SysConstant.PARAM_KEY_CACHE_MENU_DURATION, "30");
            stringRedisTemplate.opsForValue().set(redisKey, JSONUtil.toJsonStr(redisValue), Integer.parseInt(sysMenuCacheDuration), TimeUnit.MINUTES);
        } else {
            redisValue = JSONUtil.parse(redisValue).toBean(ArrayList.class);
        }

        return ApiResult.data((List<SysMenuVO>) redisValue);
    }

}
