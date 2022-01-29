#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.module.sys.service.impl;

import cc.rc.framework.core.constant.RcConstant;
import cc.rc.framework.core.context.TenantContext;
import cc.rc.framework.core.context.UserContextHolder;
import cc.rc.framework.core.exception.BusinessException;
import cc.rc.framework.core.page.PageParam;
import cc.rc.framework.core.page.PageResult;
import cc.rc.framework.crud.service.impl.RcBaseServiceImpl;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ${package}.module.sys.annotation.SysLog;
import ${package}.module.sys.entity.SysUserEntity;
import ${package}.module.sys.enums.GenericStatusEnum;
import ${package}.module.sys.enums.SysErrorEnum;
import ${package}.module.sys.enums.SysUserStatusEnum;
import ${package}.module.sys.mapper.SysUserMapper;
import ${package}.module.sys.model.request.*;
import ${package}.module.sys.model.response.*;
import ${package}.module.sys.util.PwdUtil;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 后台用户
 * @author linweijian
 */
@Slf4j
@Service
public class SysUserService extends RcBaseServiceImpl<SysUserMapper, SysUserEntity> {

    @Resource
    private SysRoleServiceImpl sysRoleServiceImpl;

    @Resource
    private SysDeptServiceImpl sysDeptServiceImpl;

    @Resource
    private SysMenuServiceImpl sysMenuServiceImpl;

    @Resource
    private SysTenantServiceImpl sysTenantServiceImpl;

    @Resource
    private SysUserDeptRelationServiceImpl sysUserDeptRelationServiceImpl;

    @Resource
    private SysUserRoleRelationServiceImpl sysUserRoleRelationServiceImpl;


    /**
     * 后台管理-分页列表
     */
    public PageResult<SysUserVO> adminList(PageParam pageParam, AdminListSysUserDTO dto) {
        Page<SysUserEntity> entityPage = this.page(
                new Page<>(pageParam.getPageNum(), pageParam.getPageSize()),
                new QueryWrapper<SysUserEntity>()
                        .lambda()
                        // 手机号
                        .like(StrUtil.isNotBlank(dto.getPhoneNo()), SysUserEntity::getPhoneNo, StrUtil.cleanBlank(dto.getPhoneNo()))
                        // 排序
                        .orderByDesc(SysUserEntity::getCreatedAt)
        );

        return this.entityPage2BOPage(entityPage);
    }

    /**
     * 通用-详情
     */
    public SysUserVO getOneById(Long entityId) {
        SysUserEntity entity = this.getById(entityId);
        SysErrorEnum.INVALID_ID.assertNotNull(entity);

        return this.entity2BO(entity, true);
    }

    /**
     * 后台管理-新增
     * @return 主键ID
     */
    @SysLog(value = "新增后台用户")
    @Transactional(rollbackFor = Exception.class)
    public Long adminInsert(AdminInsertOrUpdateSysUserDTO dto) {
        this.checkExistence(dto);

        dto.setId(null);
        SysUserEntity entity = new SysUserEntity();
        BeanUtil.copyProperties(dto, entity);

        String salt = IdUtil.randomUUID();
        entity
                .setSalt(salt)
                .setPin(dto.getUsername())
                .setPwd(PwdUtil.encrypt(dto.getPasswordOfNewUser(), salt))
        ;

        this.save(entity);

        sysUserDeptRelationServiceImpl.cleanAndBind(entity.getId(), dto.getDeptId());

        return entity.getId();
    }

    /**
     * 后台管理-编辑
     */
    @SysLog(value = "编辑后台用户")
    @Transactional(rollbackFor = Exception.class)
    public void adminUpdate(AdminInsertOrUpdateSysUserDTO dto) {
        this.checkExistence(dto);

        SysUserEntity updateEntity = new SysUserEntity();
        BeanUtil.copyProperties(dto, updateEntity);

        sysUserDeptRelationServiceImpl.cleanAndBind(dto.getId(), dto.getDeptId());

        this.updateById(updateEntity);
    }

    /**
     * 后台管理-删除
     */
    @SysLog(value = "删除后台用户")
    @Transactional(rollbackFor = Exception.class)
    public void adminDelete(List<Long> ids) {
        this.removeByIds(ids);
    }

    /**
     * 后台管理-登录
     */
    @SysLog(value = "登录后台用户")
    public SysUserLoginVO adminLogin(SysUserLoginDTO dto) {
        // 主动清空用户上下文，避免残留租户ID导致的尴尬
        UserContextHolder.setUserContext(null);

        SysUserEntity sysUserEntity = this.getUserByPin(dto.getUsername());
        if (sysUserEntity == null) {
            throw new BusinessException(SysErrorEnum.USER_NOT_EXISTS);
        }

        if (!PwdUtil.encrypt(dto.getPassword(), sysUserEntity.getSalt()).equals(sysUserEntity.getPwd())) {
            throw new BusinessException(SysErrorEnum.INCORRECT_USER_PASSWORD);
        }

        if (SysUserStatusEnum.BANNED.equals(sysUserEntity.getStatus())) {
            throw new BusinessException(SysErrorEnum.BANNED_USER);
        }

        // 查询所属租户是否有效
        SysTenantVO tenantInfo = sysTenantServiceImpl.getTenantByTenantId(sysUserEntity.getTenantId());
        if (tenantInfo == null) {
            throw new BusinessException(SysErrorEnum.INVALID_TENANT);
        }

        if (GenericStatusEnum.DISABLED.equals(tenantInfo.getStatus())) {
            throw new BusinessException(SysErrorEnum.DISABLED_TENANT);
        }

        /*
        以上为有效性校验, 进入实际业务逻辑
        ---------------------------------------------------
         */

        // 将所属租户写入到用户上下文，使得 Mybatis-Plus 多租户拦截器可以正确执行到对应租户ID
        TenantContext currentTenantContext = TenantContext.builder()
                .tenantId(tenantInfo.getTenantId())
                .tenantName(tenantInfo.getTenantName())
                .build();
        UserContextHolder.setRelationalTenant(currentTenantContext);

        try {
            this.getBaseMapper().updateLastLoginAt(sysUserEntity.getId(), LocalDateTimeUtil.now());
        } catch (Exception ignored) {
            // 实际开发环境请删除本try-catch块
        }


        // 取账号完整BO
        SysUserVO sysUserBO = this.getOneById(sysUserEntity.getId());

        SysUserLoginVO ret = new SysUserLoginVO();
        BeanUtil.copyProperties(sysUserBO, ret);

        // 因字段类型不一致, 单独转换
        ret
                .setRoleIds(sysUserBO.getRoles().stream().map(SysRoleVO::getId).filter(ObjectUtil::isNotNull).sorted().collect(Collectors.toList()))
                .setRoles(sysUserBO.getRoles().stream().map(SysRoleVO::getValue).filter(StrUtil::isNotBlank).collect(Collectors.toList()))
                .setPermissions(sysUserBO.getPermissions())
                .setRelationalTenant(currentTenantContext)
        ;

        return ret;
    }

    /**
     * 后台管理-取当前用户信息
     */
    public VbenAdminUserInfoVO adminGetCurrentUserInfo() {
        SysUserVO sysUserBO = getOneById(UserContextHolder.getUserId());
        return VbenAdminUserInfoVO.builder()
                .username(sysUserBO.getUsername())
                .nickname(sysUserBO.getNickname())
                .build();
    }

    /**
     * 后台管理-重置某用户密码
     */
    @SysLog(value = "重置某用户密码")
    public void adminResetUserPassword(AdminResetSysUserPasswordDTO dto) {
        SysUserEntity sysUserEntity = this.getById(dto.getUserId());

        SysUserEntity templateEntity = new SysUserEntity();
        templateEntity
                .setPwd(PwdUtil.encrypt(dto.getRandomPassword(), sysUserEntity.getSalt()))
                .setId(dto.getUserId())
                ;

        this.updateById(templateEntity);
    }

    /**
     * 后台管理-修改当前用户密码
     */
    @SysLog(value = "修改当前用户密码")
    public void adminUpdateCurrentUserPassword(AdminUpdateCurrentSysUserPasswordDTO dto) {
        SysUserEntity sysUserEntity = this.getById(UserContextHolder.getUserId());
        if (sysUserEntity == null || !sysUserEntity.getPwd().equals(PwdUtil.encrypt(dto.getOldPassword(), sysUserEntity.getSalt()))) {
            throw new BusinessException(SysErrorEnum.INCORRECT_OLD_PASSWORD);
        }

        sysUserEntity
                .setPwd(PwdUtil.encrypt(dto.getConfirmNewPassword(), sysUserEntity.getSalt()))
                .setId(UserContextHolder.getUserId())
        ;

        this.updateById(sysUserEntity);
    }

    /**
     * 通用-根据用户账号查询
     */
    public SysUserEntity getUserByPin(String pin) {
        return this.getOne(
                new QueryWrapper<SysUserEntity>()
                        .lambda()
                        .eq(SysUserEntity::getPin, pin)
                        .last(RcConstant.CRUD.SQL_LIMIT_1)
        );
    }

    /**
     * 通用-取用户基本信息
     */
    public SysUserBaseInfoVO getBaseInfoById(Long entityId) {
        return this.getBaseMapper().getBaseInfoByUserId(entityId);
    }

    /**
     * 后台管理-绑定用户与角色关联关系
     */
    public void adminBindRoles(AdminBindUserRoleRelationDTO dto) {
        sysUserRoleRelationServiceImpl.cleanAndBind(dto.getUserId(), dto.getRoleIds());
    }

    /*
    私  有  方  法
    ------------------------------------------------------------------------------------------------
     */

    private SysUserVO entity2BO(SysUserEntity entity, boolean joinFullRolesAndPermissions) {
        if (entity == null) {
            return null;
        }

        SysUserVO bo = new SysUserVO();
        BeanUtil.copyProperties(entity, bo);

        // 可以在此处为BO填充字段
        bo.setRoleIds(sysUserRoleRelationServiceImpl.listRoleIdByUserId(bo.getId()));

        SysDeptVO dept = sysDeptServiceImpl.getPlainDeptByUserId(bo.getId());
        if (dept != null) {
            bo
                    .setDeptId(dept.getId())
                    .setDeptTitle(dept.getTitle())
            ;
        }

        if (joinFullRolesAndPermissions) {
            List<SysRoleVO> roleBOs = sysRoleServiceImpl.listRoleByUserId(bo.getId());
            bo
                    .setRoles(roleBOs)
                    .setPermissions(sysMenuServiceImpl.adminListPermissionByRoleIds(roleBOs.stream().map(SysRoleVO::getId).collect(Collectors.toList())))
            ;
        }

        return bo;
    }

    private PageResult<SysUserVO> entityPage2BOPage(Page<SysUserEntity> entityPage) {
        // 深拷贝
        List<SysUserVO> boRecords = new ArrayList<>(entityPage.getRecords().size());
        entityPage.getRecords().forEach(
                entity -> boRecords.add(this.entity2BO(entity, false))
        );

        PageResult<SysUserVO> ret = new PageResult<>();
        BeanUtil.copyProperties(entityPage, ret);
        ret.setRecords(boRecords);
        return ret;
    }

    /**
     * 检查是否已存在相同数据
     * 
     * @param dto DTO
     */
    private void checkExistence(AdminInsertOrUpdateSysUserDTO dto) {
        SysUserEntity existingEntity = this.getUserByPin(dto.getUsername());

        if (existingEntity != null && !existingEntity.getId().equals(dto.getId())) {
            throw new BusinessException(400, "已存在相同账号，请重新输入");
        }
    }
}
