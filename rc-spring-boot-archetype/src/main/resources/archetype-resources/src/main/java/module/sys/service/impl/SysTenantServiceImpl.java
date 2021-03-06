#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.module.sys.service.impl;

import cc.rc.framework.core.constant.RcConstant;
import cc.rc.framework.core.context.UserContextHolder;
import cc.rc.framework.core.exception.BusinessException;
import cc.rc.framework.core.page.PageParam;
import cc.rc.framework.core.page.PageResult;
import cc.rc.framework.crud.service.impl.RcBaseServiceImpl;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ${package}.module.sys.annotation.SysLog;
import ${package}.module.sys.entity.SysTenantEntity;
import ${package}.module.sys.entity.SysUserRoleRelationEntity;
import ${package}.module.sys.enums.SysErrorEnum;
import ${package}.module.sys.mapper.SysTenantMapper;
import ${package}.module.sys.model.request.*;
import ${package}.module.sys.model.response.SysTenantVO;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


/**
 * 系统租户
 * @author linweijian
 */
@Slf4j
@Service
public class SysTenantServiceImpl extends RcBaseServiceImpl<SysTenantMapper, SysTenantEntity> {

    @Resource
    private SysRoleServiceImpl sysRoleServiceImpl;

    @Resource
    private SysUserService sysUserService;

    @Resource
    private SysUserRoleRelationServiceImpl sysUserRoleRelationServiceImpl;


    /**
     * 后台管理-分页列表
     */
    public PageResult<SysTenantVO> adminList(PageParam pageParam, AdminListSysTenantDTO dto) {
        Page<SysTenantEntity> entityPage = this.page(
                new Page<>(pageParam.getPageNum(), pageParam.getPageSize()),
                new QueryWrapper<SysTenantEntity>()
                        .lambda()
                        // 租户名
                        .like(StrUtil.isNotBlank(dto.getTenantName()), SysTenantEntity::getTenantName, StrUtil.cleanBlank(dto.getTenantName()))
                        // 租户ID
                        .eq(ObjectUtil.isNotNull(dto.getTenantId()), SysTenantEntity::getTenantId, dto.getTenantId())
                        // 状态
                        .eq(ObjectUtil.isNotNull(dto.getStatus()), SysTenantEntity::getStatus, dto.getStatus())
                        // 时间区间
                        .between(ObjectUtil.isNotNull(dto.getBeginAt()) && ObjectUtil.isNotNull(dto.getEndAt()), SysTenantEntity::getCreatedAt, dto.getBeginAt(), dto.getEndAt())
                        // 排序
                        .orderByDesc(SysTenantEntity::getCreatedAt)
        );

        return this.entityPage2BOPage(entityPage);
    }

    /**
     * 通用-详情
     */
    public SysTenantVO getOneById(Long entityId) {
        SysTenantEntity entity = this.getById(entityId);
        SysErrorEnum.INVALID_ID.assertNotNull(entity);

        return this.entity2BO(entity);
    }

    /**
     * 后台管理-新增
     */
    @SysLog(value = "新增系统租户")
    @Transactional(rollbackFor = Exception.class)
    public Long adminInsert(AdminInsertSysTenantDTO dto) {
        this.checkExistence(dto);

        // 1. 加入一个新租户(tenant)
        //    这里是直接顺带创建管理员账号了, 你可以根据业务需要决定是否创建

        dto.setId(null);
        SysTenantEntity entity = new SysTenantEntity();
        BeanUtil.copyProperties(dto, entity);

        this.save(entity);

        Long newTenantEntityId = entity.getId();
        Long newTenantId = entity.getTenantId();

        // 2. 创建一个新角色(role)
        //    注意:这里并没有指派其可见菜单
        Long newRoleId = sysRoleServiceImpl.adminInsert(
                AdminInsertOrUpdateSysRoleDTO.builder()
                        .tenantId(newTenantId)
                        .title(dto.getTenantName() + "管理员")
                        .value("Admin")
                        .build()
        );

        // 3. 创建一个新用户
        Long newUserId = sysUserService.adminInsert(
                AdminInsertOrUpdateSysUserDTO.builder()
                        .tenantId(newTenantId)
                        .username(dto.getTenantAdminUsername())
                        .passwordOfNewUser(dto.getTenantAdminPassword())
                        .nickname(dto.getTenantName() + "管理员")
                        .email(dto.getTenantAdminEmail())
                        .phoneNo(dto.getTenantAdminPhoneNo())
                        .build()
        );

        // 4. 将新用户绑定至新角色上
        sysUserRoleRelationServiceImpl.save(
                SysUserRoleRelationEntity.builder()
                        .tenantId(newTenantId)
                        .userId(newUserId)
                        .roleId(newRoleId)
                        // 可能不会自动填充字段，手动补上
                        .createdAt(LocalDateTime.now())
                        .createdBy(UserContextHolder.getUserContext().getUserName())
                        .updatedAt(LocalDateTime.now())
                        .updatedBy(UserContextHolder.getUserContext().getUserName())
                        .build()
        );

        entity = new SysTenantEntity();
        entity
                .setTenantAdminUserId(newUserId)
                .setId(newTenantEntityId);

        // 5. 把管理员账号更新进库
        this.updateById(entity);

        return newTenantId;
    }

    /**
     * 后台管理-编辑
     */
    @SysLog(value = "编辑系统租户")
    @Transactional(rollbackFor = Exception.class)
    public void adminUpdate(AdminUpdateSysTenantDTO dto) {
        this.checkExistence(dto);

        SysTenantEntity entity = new SysTenantEntity();
        BeanUtil.copyProperties(dto, entity);

        this.updateById(entity);
    }

    /**
     * 后台管理-删除
     */
    @SysLog(value = "删除系统租户")
    @Transactional(rollbackFor = Exception.class)
    public void adminDelete(List<Long> ids) {
        this.removeByIds(ids);
    }

    /**
     * 通用-根据租户ID(非主键ID)查询
     */
    public SysTenantVO getTenantByTenantId(Long tenantId) {
        SysTenantEntity sysTenantEntity = this.getOne(
                new QueryWrapper<SysTenantEntity>()
                        .lambda()
                        .eq(SysTenantEntity::getTenantId, tenantId)
                        .last(RcConstant.CRUD.SQL_LIMIT_1)
        );

        if (sysTenantEntity == null) {
            return null;
        }

        return this.entity2BO(sysTenantEntity);
    }


    /*
    私有方法
    ------------------------------------------------------------------------------------------------
     */

    private SysTenantVO entity2BO(SysTenantEntity entity) {
        if (entity == null) {
            return null;
        }

        SysTenantVO bo = new SysTenantVO();
        BeanUtil.copyProperties(entity, bo);

        // 可以在此处为BO填充字段
        if (ObjectUtil.isNotNull(entity.getTenantAdminUserId())) {
            bo
                    .setTenantAdminUser(sysUserService.getBaseInfoById(entity.getTenantAdminUserId()))
            ;
        }

        return bo;
    }

    private List<SysTenantVO> entityList2BOs(List<SysTenantEntity> entityList) {
        // 深拷贝
        List<SysTenantVO> ret = new ArrayList<>(entityList.size());
        entityList.forEach(
                entity -> ret.add(this.entity2BO(entity))
        );

        return ret;
    }

    private PageResult<SysTenantVO> entityPage2BOPage(Page<SysTenantEntity> entityPage) {
        PageResult<SysTenantVO> ret = new PageResult<>();
        BeanUtil.copyProperties(entityPage, ret);
        ret.setRecords(this.entityList2BOs(entityPage.getRecords()));

        return ret;
    }

    /**
     * 检查是否已存在相同数据
     * 
     * @param dto DTO
     */
    private void checkExistence(AdminUpdateSysTenantDTO dto) {
        SysTenantEntity existingEntity = this.getOne(
                new QueryWrapper<SysTenantEntity>()
                        .select(RcConstant.CRUD.SQL_COLUMN_ID)
                        .lambda()
                        .eq(SysTenantEntity::getTenantId, dto.getTenantId())
                        .or()
                        .eq(SysTenantEntity::getTenantName, dto.getTenantName())
                        .last(RcConstant.CRUD.SQL_LIMIT_1)
        );

        if (existingEntity != null && !existingEntity.getId().equals(dto.getId())) {
            throw new BusinessException(400, "已存在相同系统租户，请重新输入");
        }
    }

}
