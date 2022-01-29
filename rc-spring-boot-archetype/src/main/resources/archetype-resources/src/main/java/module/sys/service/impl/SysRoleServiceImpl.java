#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.module.sys.service.impl;

import cc.rc.framework.core.constant.RcConstant;
import cc.rc.framework.core.exception.BusinessException;
import cc.rc.framework.core.page.PageParam;
import cc.rc.framework.core.page.PageResult;
import cc.rc.framework.crud.service.impl.RcBaseServiceImpl;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ${package}.module.sys.annotation.SysLog;
import ${package}.module.sys.entity.SysRoleEntity;
import ${package}.module.sys.enums.SysErrorEnum;
import ${package}.module.sys.mapper.SysRoleMapper;
import ${package}.module.sys.model.request.AdminInsertOrUpdateSysRoleDTO;
import ${package}.module.sys.model.request.AdminListSysRoleDTO;
import ${package}.module.sys.model.response.SysRoleVO;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;


/**
 * 后台角色
 * @author linweijian
 */
@Slf4j
@Service
public class SysRoleServiceImpl extends RcBaseServiceImpl<SysRoleMapper, SysRoleEntity> {

    @Resource
    private SysUserRoleRelationServiceImpl sysUserRoleRelationServiceImpl;

    @Resource
    private SysRoleMenuRelationServiceImpl sysRoleMenuRelationServiceImpl;


    /**
     * 后台管理-分页列表
     */
    public PageResult<SysRoleVO> adminList(PageParam pageParam, AdminListSysRoleDTO dto) {
        Page<SysRoleEntity> entityPage = this.page(
                new Page<>(pageParam.getPageNum(), pageParam.getPageSize()),
                new QueryWrapper<SysRoleEntity>()
                        .lambda()
                        // 名称
                        .like(StrUtil.isNotBlank(dto.getTitle()), SysRoleEntity::getTitle, StrUtil.cleanBlank(dto.getTitle()))
                        // 值
                        .like(StrUtil.isNotBlank(dto.getValue()), SysRoleEntity::getValue, StrUtil.cleanBlank(dto.getValue()))
                        // 时间区间
                        .between(ObjectUtil.isNotNull(dto.getBeginAt()) && ObjectUtil.isNotNull(dto.getEndAt()), SysRoleEntity::getCreatedAt, dto.getBeginAt(), dto.getEndAt())
                        // 排序
                        .orderByDesc(SysRoleEntity::getCreatedAt)
        );

        return this.entityPage2BOPage(entityPage);
    }

    /**
     * 通用-详情
     */
    public SysRoleVO getOneById(Long entityId) {
        SysRoleEntity entity = this.getById(entityId);
        SysErrorEnum.INVALID_ID.assertNotNull(entity);

        return this.entity2BO(entity);
    }

    /**
     * 后台管理-新增
     * @return 主键ID
     */
    @SysLog(value = "新增后台角色")
    @Transactional(rollbackFor = Exception.class)
    public Long adminInsert(AdminInsertOrUpdateSysRoleDTO dto) {
        this.checkExistence(dto);

        dto.setId(null);
        SysRoleEntity entity = new SysRoleEntity();
        BeanUtil.copyProperties(dto, entity);

        this.save(entity);

        sysRoleMenuRelationServiceImpl.cleanAndBind(entity.getId(), dto.getMenuIds());

        return entity.getId();
    }

    /**
     * 后台管理-编辑
     */
    @SysLog(value = "编辑后台角色")
    @Transactional(rollbackFor = Exception.class)
    public void adminUpdate(AdminInsertOrUpdateSysRoleDTO dto) {
        this.checkExistence(dto);

        SysRoleEntity entity = new SysRoleEntity();
        BeanUtil.copyProperties(dto, entity);

        sysRoleMenuRelationServiceImpl.cleanAndBind(dto.getId(), dto.getMenuIds());

        this.updateById(entity);
    }

    /**
     * 后台管理-删除
     */
    @SysLog(value = "删除后台角色")
    @Transactional(rollbackFor = Exception.class)
    public void adminDelete(List<Long> ids) {
        this.removeByIds(ids);
    }

    /**
     * 取拥有角色列表
     * @param userId 用户ID
     * @return 失败返回空列表
     */
    public List<SysRoleVO> listRoleByUserId(Long userId) {
        List<Long> roleIds = sysUserRoleRelationServiceImpl.listRoleIdByUserId(userId);

        if (CollUtil.isEmpty(roleIds)) {
            return CollUtil.newArrayList();
        }

        // 根据角色Ids取BO
        List<SysRoleEntity> entityList = this.listByIds(roleIds);

        return this.entityList2BOs(entityList);
    }


    /*
    私有方法
    ------------------------------------------------------------------------------------------------
     */

    private SysRoleVO entity2BO(SysRoleEntity entity) {
        if (entity == null) {
            return null;
        }

        SysRoleVO bo = new SysRoleVO();
        BeanUtil.copyProperties(entity, bo);

        // 可以在此处为BO填充字段
        bo.setMenuIds(sysRoleMenuRelationServiceImpl.listMenuIdByRoleIds(
           CollUtil.newArrayList(bo.getId())
        ));
        return bo;
    }

    private List<SysRoleVO> entityList2BOs(List<SysRoleEntity> entityList) {
        // 深拷贝
        List<SysRoleVO> ret = new ArrayList<>(entityList.size());
        entityList.forEach(
                entity -> ret.add(this.entity2BO(entity))
        );

        return ret;
    }

    private PageResult<SysRoleVO> entityPage2BOPage(Page<SysRoleEntity> entityPage) {
        PageResult<SysRoleVO> ret = new PageResult<>();
        BeanUtil.copyProperties(entityPage, ret);
        ret.setRecords(this.entityList2BOs(entityPage.getRecords()));

        return ret;
    }

    /**
     * 检查是否已存在相同数据
     * 
     * @param dto DTO
     */
    private void checkExistence(AdminInsertOrUpdateSysRoleDTO dto) {
        SysRoleEntity existingEntity = this.getOne(
                new QueryWrapper<SysRoleEntity>()
                        .select(RcConstant.CRUD.SQL_COLUMN_ID)
                        .lambda()
                        .eq(SysRoleEntity::getTitle, dto.getTitle())
                        .last(RcConstant.CRUD.SQL_LIMIT_1)
        );

        if (existingEntity != null && !existingEntity.getId().equals(dto.getId())) {
            throw new BusinessException(400, "已存在相同后台角色，请重新输入");
        }
    }
}
