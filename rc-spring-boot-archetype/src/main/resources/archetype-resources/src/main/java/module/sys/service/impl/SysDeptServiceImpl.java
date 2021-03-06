#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.module.sys.service.impl;

import cc.rc.framework.core.constant.RcConstant;
import cc.rc.framework.core.exception.BusinessException;
import cc.rc.framework.crud.service.impl.RcBaseServiceImpl;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ${package}.module.sys.annotation.SysLog;
import ${package}.module.sys.constant.SysConstant;
import ${package}.module.sys.entity.SysDeptEntity;
import ${package}.module.sys.entity.SysUserDeptRelationEntity;
import ${package}.module.sys.enums.SysErrorEnum;
import ${package}.module.sys.mapper.SysDeptMapper;
import ${package}.module.sys.model.request.AdminInsertOrUpdateSysDeptDTO;
import ${package}.module.sys.model.request.AdminListSysDeptDTO;
import ${package}.module.sys.model.response.SysDeptVO;
import ${package}.module.sys.service.SysDeptService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;


/**
 * 部门
 * @author linweijian
 */
@Slf4j
@Service
public class SysDeptServiceImpl extends RcBaseServiceImpl<SysDeptMapper, SysDeptEntity> implements SysDeptService {

    @Resource
    private SysUserDeptRelationServiceImpl sysUserDeptRelationServiceImpl;


    /**
     * 后台管理-列表
     */
    @Override
    public List<SysDeptVO> adminList(AdminListSysDeptDTO dto) {
        List<SysDeptEntity> entityList = this.list(
                new QueryWrapper<SysDeptEntity>()
                        .lambda()
                        // 名称
                        .like(StrUtil.isNotBlank(dto.getTitle()), SysDeptEntity::getTitle, StrUtil.cleanBlank(dto.getTitle()))
                        // 上级ID
                        .eq(ObjectUtil.isNotNull(dto.getParentId()), SysDeptEntity::getParentId, dto.getParentId())
                        // 排序
                        .orderByAsc(SysDeptEntity::getSort)
        );

        return this.entityList2BOs(entityList);
    }

    /**
     * 通用-详情
     */
    @Override
    public SysDeptVO getOneById(Long entityId) {
        SysDeptEntity entity = this.getById(entityId);
        SysErrorEnum.INVALID_ID.assertNotNull(entity);

        return this.entity2BO(entity, false);
    }

    /**
     * 后台管理-新增
     */
    @Override
    @SysLog(value = "新增部门")
    @Transactional(rollbackFor = Exception.class)
    public Long adminInsert(AdminInsertOrUpdateSysDeptDTO dto) {
        this.checkExistence(dto);

        if (ObjectUtil.isNull(dto.getParentId())) {
            dto.setParentId(0L);
        }

        dto.setId(null);
        SysDeptEntity entity = new SysDeptEntity();
        BeanUtil.copyProperties(dto, entity);

        this.save(entity);

        return entity.getId();
    }

    /**
     * 后台管理-编辑
     */
    @Override
    @SysLog(value = "编辑部门")
    @Transactional(rollbackFor = Exception.class)
    public void adminUpdate(AdminInsertOrUpdateSysDeptDTO dto) {
        this.checkExistence(dto);

        if (ObjectUtil.isNull(dto.getParentId())) {
            dto.setParentId(0L);
        }

        SysDeptEntity entity = new SysDeptEntity();
        BeanUtil.copyProperties(dto, entity);

        this.updateById(entity);
    }

    /**
     * 后台管理-删除
     */
    @Override
    @SysLog(value = "删除部门")
    @Transactional(rollbackFor = Exception.class)
    public void adminDelete(List<Long> ids) {
        this.removeByIds(ids);
    }

    /**
     * 取所属部门简易信息
     * @param userId 用户ID
     */
    @Override
    public SysDeptVO getPlainDeptByUserId(Long userId) {
        SysUserDeptRelationEntity relationEntity = sysUserDeptRelationServiceImpl.getOne(
                new QueryWrapper<SysUserDeptRelationEntity>()
                        .lambda()
                        .eq(SysUserDeptRelationEntity::getUserId, userId)
                        .last(RcConstant.CRUD.SQL_LIMIT_1)
        );

        if (relationEntity == null) {
            return null;
        }

        SysDeptEntity entity = this.getById(relationEntity.getDeptId());
        return this.entity2BO(entity, false);
    }

    
    /*
    私有方法
    ------------------------------------------------------------------------------------------------
     */

    private SysDeptVO entity2BO(SysDeptEntity entity, boolean traverseChildren) {
        if (entity == null) {
            return null;
        }

        SysDeptVO bo = new SysDeptVO();
        BeanUtil.copyProperties(entity, bo);

        // 可以在此处为BO填充字段
        if (SysConstant.ROOT_PARENT_ID.equals(bo.getParentId())) {
            bo.setParentId(null);
        }

        if (traverseChildren) {
            List<SysDeptVO> children = this.adminList(
                    AdminListSysDeptDTO.builder()
                            .parentId(bo.getId())
                            .build()
            );
            if (CollUtil.isEmpty(children)) {
                children = null;
            }

            bo.setChildren(children);
        }

        return bo;
    }

    private List<SysDeptVO> entityList2BOs(List<SysDeptEntity> entityList) {
        // 深拷贝
        List<SysDeptVO> ret = new ArrayList<>(entityList.size());
        entityList.forEach(
                entity -> ret.add(this.entity2BO(entity, true))
        );

        return ret;
    }


    /**
     * 检查是否已存在相同数据
     * 
     * @param dto DTO
     */
    private void checkExistence(AdminInsertOrUpdateSysDeptDTO dto) {
        SysDeptEntity existingEntity = this.getOne(
                new QueryWrapper<SysDeptEntity>()
                        .select(RcConstant.CRUD.SQL_COLUMN_ID)
                        .lambda()
                        .eq(SysDeptEntity::getTitle, dto.getTitle())
                        .last(RcConstant.CRUD.SQL_LIMIT_1)
        );

        if (existingEntity != null && !existingEntity.getId().equals(dto.getId())) {
            throw new BusinessException(400, "已存在相同部门，请重新输入");
        }
    }
}
