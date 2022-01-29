#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.module.sys.service.impl;

import cc.rc.framework.core.constant.RcConstant;
import cc.rc.framework.core.context.UserContextHolder;
import cc.rc.framework.core.exception.BusinessException;
import cc.rc.framework.crud.service.impl.RcBaseServiceImpl;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ${package}.module.sys.annotation.SysLog;
import ${package}.module.sys.constant.SysConstant;
import ${package}.module.sys.entity.SysMenuEntity;
import ${package}.module.sys.enums.GenericStatusEnum;
import ${package}.module.sys.enums.SysErrorEnum;
import ${package}.module.sys.enums.SysMenuTypeEnum;
import ${package}.module.sys.mapper.SysMenuMapper;
import ${package}.module.sys.model.request.AdminInsertOrUpdateSysMenuDTO;
import ${package}.module.sys.model.request.AdminListSysMenuDTO;
import ${package}.module.sys.model.response.SysMenuVO;
import ${package}.module.sys.model.response.VbenAdminMenuMetaVO;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * 后台菜单
 * @author linweijian
 */
@Slf4j
@Service
public class SysMenuServiceImpl extends RcBaseServiceImpl<SysMenuMapper, SysMenuEntity> {

    @Resource
    private SysRoleMenuRelationServiceImpl sysRoleMenuRelationServiceImpl;

    @Resource
    private SysUserRoleRelationServiceImpl sysUserRoleRelationServiceImpl;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private static final Snowflake SNOWFLAKE = IdUtil.getSnowflake(0L, 0L);


    /**
     * 后台管理-列表
     */
    public List<SysMenuVO> adminList(AdminListSysMenuDTO dto) {
        List<SysMenuEntity> entityList = this.list(
                new QueryWrapper<SysMenuEntity>()
                        .lambda()
                        // 标题
                        .like(StrUtil.isNotBlank(dto.getTitle()), SysMenuEntity::getTitle, StrUtil.cleanBlank(dto.getTitle()))
                        // 上级ID
                        .eq(SysMenuEntity::getParentId, ObjectUtil.isNull(dto.getParentId()) ? SysConstant.ROOT_PARENT_ID : dto.getParentId())
                        // 菜单类型
                        .in(CollUtil.isNotEmpty(dto.getMenuTypes()), SysMenuEntity::getType, dto.getMenuTypes())
                        // 排序
                        .orderByAsc(SysMenuEntity::getSort)
        );

        return this.entityList2BOs(entityList, true);
    }

    /**
     * 通用-详情
     */
    public SysMenuVO getOneById(Long entityId) {
        SysMenuEntity entity = this.getById(entityId);
        SysErrorEnum.INVALID_ID.assertNotNull(entity);

        return this.entity2BO(entity, false, null);
    }

    /**
     * 后台管理-新增
     * @return 主键ID
     */
    @SysLog(value = "新增后台菜单")
    @Transactional(rollbackFor = Exception.class)
    public Long adminInsert(AdminInsertOrUpdateSysMenuDTO dto) {
        this.checkExistence(dto);

        if (ObjectUtil.isNull(dto.getParentId())) {
            dto.setParentId(0L);
        }

        dto.setId(null);

        SysMenuEntity entity = new SysMenuEntity();
        BeanUtil.copyProperties(dto, entity);

        this.save(entity);

        return entity.getId();
    }

    /**
     * 后台管理-编辑
     */
    @SysLog(value = "编辑后台菜单")
    @Transactional(rollbackFor = Exception.class)
    public void adminUpdate(AdminInsertOrUpdateSysMenuDTO dto) {
        this.checkExistence(dto);

        if (ObjectUtil.isNull(dto.getParentId())) {
            dto.setParentId(0L);
        }

        SysMenuEntity entity = new SysMenuEntity();
        BeanUtil.copyProperties(dto, entity);

        this.updateById(entity);
    }

    /**
     * 后台管理-删除
     */
    @SysLog(value = "删除后台菜单")
    @Transactional(rollbackFor = Exception.class)
    public void adminDelete(List<Long> ids) {
        this.removeByIds(ids);
    }

    /**
     * 后台管理-根据角色Ids取权限串List
     */
    public List<String> adminListPermissionByRoleIds(List<Long> roleIds) {
        if (CollUtil.isEmpty(roleIds)) {
            return CollUtil.newArrayList();
        }

        // 超级管理员直接允许所有权限
        if (roleIds.contains(SysConstant.SUPER_ADMIN_ROLE_ID)) {
            return this.list().stream().map(SysMenuEntity::getPermission).collect(Collectors.toList());
        }

        List<Long> menuIds = sysRoleMenuRelationServiceImpl.listMenuIdByRoleIds(roleIds);

        if (CollUtil.isEmpty(menuIds)) {
            return CollUtil.newArrayList();
        }

        return this.list(
                new QueryWrapper<SysMenuEntity>()
                        .select(" DISTINCT permission ")
                        .lambda()
                        .in(SysMenuEntity::getId, menuIds)
                        .ne(SysMenuEntity::getPermission, "")
        ).stream().map(SysMenuEntity::getPermission).collect(Collectors.toList());
    }

    /**
     * 后台管理-取当前账号可见侧边菜单
     */
    public List<SysMenuVO> adminListSideMenu() {
        List<Long> visibleMenuIds = this.listCurrentUserVisibleMenuId();

        // 3. 取出无上级节点菜单
        List<SysMenuTypeEnum> requiredMenuTypes = CollUtil.newArrayList(SysMenuTypeEnum.DIR, SysMenuTypeEnum.MENU, SysMenuTypeEnum.EXTERNAL_LINK);
        List<SysMenuVO> allMenus = this.listMenuByParentId(visibleMenuIds, requiredMenuTypes, SysConstant.ROOT_PARENT_ID);
        log.debug("[后台管理][取当前账号可见侧边菜单] 取出无上级节点菜单 >> {}", allMenus);

        // 4.递归查询子节点, 孙节点, 曾孙节点...
        this.recursiveFindChildren(visibleMenuIds, requiredMenuTypes, allMenus);
        log.debug("[后台管理][取当前账号可见侧边菜单] 递归查询子节点, 孙节点, 曾孙节点... >> {}", allMenus);

        return allMenus;
    }

    /**
     * 后台管理-取当前账号所有可见菜单(包括按钮类型)
     */
    public List<SysMenuVO> adminListVisibleMenu() {
        List<Long> visibleMenuIds = this.listCurrentUserVisibleMenuId();

        // 3. 取出无上级节点菜单
        List<SysMenuTypeEnum> requiredMenuTypes = CollUtil.newArrayList(SysMenuTypeEnum.DIR, SysMenuTypeEnum.MENU, SysMenuTypeEnum.EXTERNAL_LINK, SysMenuTypeEnum.BUTTON);
        List<SysMenuVO> allMenus = this.listMenuByParentId(visibleMenuIds, requiredMenuTypes, SysConstant.ROOT_PARENT_ID);
        log.debug("[后台管理][取当前账号所有可见菜单] 取出无上级节点菜单:{}", allMenus);

        // 4.递归查询子节点, 孙节点, 曾孙节点...
        this.recursiveFindChildren(visibleMenuIds, requiredMenuTypes, allMenus);
        log.debug("[后台管理][取当前账号所有可见菜单] 递归查询子节点, 孙节点, 曾孙节点...:{}", allMenus);

        return allMenus;
    }

    /**
     * 内部-过滤被禁用的菜单ID
     *
     * @param visibleMenuIds 待过滤的可见的菜单ID列表
     * @return 过滤后的菜单ID列表
     */
    public List<Long> filterDisabledIds(List<Long> visibleMenuIds) {
        if (CollUtil.isEmpty(visibleMenuIds)) {
            return visibleMenuIds;
        }

        /*
        1. 找出目前被禁用的菜单ID
         */
        List<Long> disabledMenuIds = this.list(
                new QueryWrapper<SysMenuEntity>()
                        .select(RcConstant.CRUD.SQL_COLUMN_ID)
                        .lambda()
                        .eq(SysMenuEntity::getStatus, GenericStatusEnum.DISABLED)
        ).stream().map(SysMenuEntity::getId).collect(Collectors.toList());


        /*
        2. 提取出符合以下条件的菜单ID:
            启用状态 && 父菜单未被禁用
         */
        return this.list(
                new QueryWrapper<SysMenuEntity>()
                        .select(RcConstant.CRUD.SQL_COLUMN_ID)
                        .lambda()
                        .and(
                                wrapper -> wrapper
                                        .eq(SysMenuEntity::getStatus, GenericStatusEnum.ENABLED)
                                        .notIn(CollUtil.isNotEmpty(disabledMenuIds), SysMenuEntity::getParentId, disabledMenuIds)
                        )
                        .in(SysMenuEntity::getId, visibleMenuIds)
        ).stream().map(SysMenuEntity::getId).collect(Collectors.toList());
    }

    /**
     * 通用-清除Redis中所有菜单缓存
     */
    public void cleanMenuCacheInRedis() {
        this.cleanMenuCacheInRedis(null);
    }

    /**
     * 通用-根据用户ID，清除Redis中相关菜单缓存
     * @param userId 用户ID；为null则清除所有
     */
    public void cleanMenuCacheInRedis(Long userId) {
        String redisKey;
        if (ObjectUtil.isNull(userId)) {
            redisKey = "*";
        } else {
            redisKey = "userId_" + StrUtil.toString(userId);
        }

        log.info("[清除Redis中相关菜单缓存] >> redisKey={}", redisKey);

        Set<String> keys = stringRedisTemplate.keys(SysConstant.REDIS_KEY_SIDE_MENU + redisKey);
        if (keys != null && !keys.isEmpty()) {
            stringRedisTemplate.delete(keys);
        }

        keys = stringRedisTemplate.keys(SysConstant.REDIS_KEY_VISIBLE_MENU + redisKey);
        if (keys != null && !keys.isEmpty()) {
            stringRedisTemplate.delete(keys);
        }
    }
    

    /*
    私有方法
    ------------------------------------------------------------------------------------------------
     */

    private SysMenuVO entity2BO(SysMenuEntity entity, boolean traverseChildren, List<SysMenuTypeEnum> menuTypes) {
        if (entity == null) {
            return null;
        }

        SysMenuVO bo = new SysMenuVO();
        BeanUtil.copyProperties(entity, bo);

        // 可以在此处为BO填充字段
        if (SysConstant.ROOT_PARENT_ID.equals(bo.getParentId())) {
            bo.setParentId(null);
        }

        String snowflakeIdStr = SNOWFLAKE.nextIdStr();
        bo
                .setIdStr(StrUtil.toString(bo.getId()))
                .setName(snowflakeIdStr)
                .setMeta(
                        VbenAdminMenuMetaVO.builder()
                                .title(bo.getTitle())
                                .affix(false)
                                .icon(bo.getIcon())
                                .build()
                );

        // 这里是兼容JDK8的写法，使用JDK15可使用语法糖免写break
        switch (bo.getType()) {
            case DIR:
            case BUTTON: {
                bo
                        .setComponent(SysConstant.VBEN_ADMIN_BLANK_VIEW)
                        .setExternalLink(null)
                        .setPath("/" + snowflakeIdStr)
                ;
                break;
            }
            case MENU: {
                bo
                        .setExternalLink(null)
                        .setPath(bo.getComponent())
                ;
                // 防止用户忘记加了, 主动补充/
                if (StrUtil.isNotBlank(bo.getPath()) && !bo.getPath().startsWith("/")) {
                    bo.setPath("/" + bo.getPath());
                }
                break;
            }
            case EXTERNAL_LINK: {
                bo
                        .setComponent(bo.getExternalLink())
                        .setPath(bo.getExternalLink())
                ;
                break;
            }
        }

        // 遍历子级菜单
        if (traverseChildren) {
            List<SysMenuVO> children = this.adminList(
                    AdminListSysMenuDTO.builder()
                            .parentId(bo.getId())
                            .menuTypes(menuTypes)
                            .build()
            );
            if (CollUtil.isEmpty(children)) {
                children = null;
            }

            bo.setChildren(children);
        }

        return bo;
    }

    private List<SysMenuVO> entityList2BOs(List<SysMenuEntity> entityList, Boolean traverseChildren) {
        // 深拷贝
        List<SysMenuVO> ret = new ArrayList<>(entityList.size());
        entityList.forEach(
                entity -> ret.add(this.entity2BO(entity, traverseChildren, null))
        );

        return ret;
    }

    /**
     * 取当前账号可见菜单Ids
     * @return 菜单Ids
     */
    private List<Long> listCurrentUserVisibleMenuId() {
        // 1. 取当前账号拥有角色Ids
        List<Long> roleIds = sysUserRoleRelationServiceImpl.listRoleIdByUserId(UserContextHolder.getUserId());
        log.debug("[后台管理][取当前账号可见菜单Ids] 当前账号拥有角色Ids >> {}", roleIds);
        if (CollUtil.isEmpty(roleIds)) {
            throw new BusinessException(SysErrorEnum.NO_ROLE_AVAILABLE_FOR_CURRENT_USER);
        }

        // 2-1. 超级管理员直接允许所有菜单，即使是禁用状态
        if (roleIds.contains(SysConstant.SUPER_ADMIN_ROLE_ID)) {
            return this.list(
                    new QueryWrapper<SysMenuEntity>()
                            .select(RcConstant.CRUD.SQL_COLUMN_ID)
            ).stream().map(SysMenuEntity::getId).collect(Collectors.toList());
        }

        // 2-2. 根据角色Ids取菜单Ids
        List<Long> menuIds = sysRoleMenuRelationServiceImpl.listMenuIdByRoleIds(roleIds);
        log.debug("[后台管理][取当前账号可见菜单Ids] 根据角色Ids取菜单Ids >> {}", menuIds);
        if (CollUtil.isEmpty(menuIds)) {
            throw new BusinessException(SysErrorEnum.NO_MENU_AVAILABLE_FOR_CURRENT_ROLE);
        }

        /*
        3. 补充上级菜单ID
        这个Ant-Design的Tree组件有个吊诡的地方, 只选一部分子级的话, 父级菜单不算勾选
        只能在代码里补上了
         */
        List<Long> missingParentIds = this.listMissingParentId(menuIds);
        menuIds.addAll(missingParentIds);

        return menuIds;
    }

    private List<SysMenuVO> listMenuByParentId(List<Long> visibleMenuIds, List<SysMenuTypeEnum> requiredMenuTypes, Long parentId) throws IllegalArgumentException {
        if (CollUtil.isEmpty(visibleMenuIds)) {
            throw new IllegalArgumentException("visibleMenuIds不能为空");
        }

        if (CollUtil.isEmpty(requiredMenuTypes)) {
            throw new IllegalArgumentException("requiredMenuTypes不能为空");
        }

        List<SysMenuEntity> entityList = this.list(
                new QueryWrapper<SysMenuEntity>()
                        .lambda()
                        .in(SysMenuEntity::getId, visibleMenuIds)
                        .in(SysMenuEntity::getType, requiredMenuTypes)
                        .eq(SysMenuEntity::getParentId, parentId)
                        .orderByAsc(SysMenuEntity::getSort)
        );

        if (CollUtil.isEmpty(entityList)) {
            return CollUtil.newArrayList();
        }

        List<SysMenuVO> ret = new ArrayList<>(entityList.size());
        entityList.forEach(
                each -> ret.add(this.entity2BO(each, false, requiredMenuTypes))
        );

        return ret;
    }

    /**
     * 递归查询子节点
     */
    private void recursiveFindChildren(List<Long> visibleMenuIds, List<SysMenuTypeEnum> requiredMenuTypes, List<SysMenuVO> menuBOs) {
        menuBOs.forEach(
                menu -> {
                    // 查询子节点
                    List<SysMenuVO> itsChildren = this.listMenuByParentId(visibleMenuIds, requiredMenuTypes, menu.getId());
                    if (CollUtil.isNotEmpty(itsChildren)) {
                        menu.setChildren(itsChildren);
                        // 递归查询孙节点
                        this.recursiveFindChildren(visibleMenuIds, requiredMenuTypes, menu.getChildren());
                    }
                }
        );
    }

    /**
     * 检查是否已存在相同数据
     * 
     * @param dto DTO
     */
    private void checkExistence(AdminInsertOrUpdateSysMenuDTO dto) {
        if (StrUtil.isNotBlank(dto.getPermission())) {
            dto.setPermission(StrUtil.cleanBlank(dto.getPermission()));

            SysMenuEntity existingEntity = this.getOne(
                    new QueryWrapper<SysMenuEntity>()
                            .select(RcConstant.CRUD.SQL_COLUMN_ID)
                            .lambda()
                            .eq(SysMenuEntity::getPermission, dto.getPermission())
                            .last(RcConstant.CRUD.SQL_LIMIT_1)
            );

            if (existingEntity != null && !existingEntity.getId().equals(dto.getId())) {
                throw new BusinessException(400, "已存在相同权限标识，请重新输入");
            }
        }
    }

    /**
     * 补充遗漏的上级菜单ID
     */
    private List<Long> listMissingParentId(List<Long> menuIds) {
        // 返回值
        List<Long> ret = new ArrayList<>(32);
        // 单次循环返回值
        List<Long> loopRet = menuIds;

        for (int i = 0; i < 5; i++) {
            // 最多上溯5层
            loopRet = this.list(
                    new QueryWrapper<SysMenuEntity>()
                            .select(" DISTINCT parent_id ")
                            .lambda()
                            .ne(SysMenuEntity::getParentId, SysConstant.ROOT_PARENT_ID)
                            .in(SysMenuEntity::getId, loopRet)
                            .notIn(SysMenuEntity::getParentId, loopRet)
            ).stream().map(SysMenuEntity::getParentId).collect(Collectors.toList());

            if (loopRet.isEmpty()) {
                break;
            } else {
                ret.addAll(loopRet);
            }
        }

        return ret;
    }
}
