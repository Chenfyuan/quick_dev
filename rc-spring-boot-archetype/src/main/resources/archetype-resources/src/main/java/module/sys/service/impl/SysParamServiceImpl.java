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
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ${package}.module.sys.annotation.SysLog;
import ${package}.module.sys.entity.SysParamEntity;
import ${package}.module.sys.enums.SysErrorEnum;
import ${package}.module.sys.mapper.SysParamMapper;
import ${package}.module.sys.model.request.AdminInsertOrUpdateSysParamDTO;
import ${package}.module.sys.model.request.AdminListSysParamDTO;
import ${package}.module.sys.model.response.SysParamVO;

import java.util.ArrayList;
import java.util.List;


/**
 * 系统参数
 * @author linweijian
 */
@Slf4j
@Service
public class SysParamServiceImpl extends RcBaseServiceImpl<SysParamMapper, SysParamEntity> {

    /**
     * 后台管理-分页列表
     */
    public PageResult<SysParamVO> adminList(PageParam pageParam, AdminListSysParamDTO dto) {
        Page<SysParamEntity> entityPage = this.page(
                new Page<>(pageParam.getPageNum(), pageParam.getPageSize()),
                new QueryWrapper<SysParamEntity>()
                        .lambda()
                        // 键名
                        .like(StrUtil.isNotBlank(dto.getName()), SysParamEntity::getName, StrUtil.cleanBlank(dto.getName()))
                        // 描述
                        .like(StrUtil.isNotBlank(dto.getDescription()), SysParamEntity::getDescription, StrUtil.cleanBlank(dto.getDescription()))
                        // 排序
                        .orderByDesc(SysParamEntity::getCreatedAt)
        );

        return this.entityPage2BOPage(entityPage);
    }

    /**
     * 通用-详情
     */
    public SysParamVO getOneById(Long entityId) {
        SysParamEntity entity = this.getById(entityId);
        SysErrorEnum.INVALID_ID.assertNotNull(entity);

        return this.entity2BO(entity);
    }

    /**
     * 后台管理-新增
     */
    @SysLog(value = "新增系统参数")
    @Transactional(rollbackFor = Exception.class)
    public Long adminInsert(AdminInsertOrUpdateSysParamDTO dto) {
        this.checkExistence(dto);

        dto.setId(null);
        SysParamEntity entity = new SysParamEntity();
        BeanUtil.copyProperties(dto, entity);

        this.save(entity);

        return entity.getId();
    }

    /**
     * 后台管理-编辑
     */
    @SysLog(value = "编辑系统参数")
    @Transactional(rollbackFor = Exception.class)
    public void adminUpdate(AdminInsertOrUpdateSysParamDTO dto) {
        this.checkExistence(dto);

        SysParamEntity entity = new SysParamEntity();
        BeanUtil.copyProperties(dto, entity);

        this.updateById(entity);
    }

    /**
     * 后台管理-删除
     */
    @SysLog(value = "删除系统参数")
    @Transactional(rollbackFor = Exception.class)
    public void adminDelete(List<Long> ids) {
        this.removeByIds(ids);
    }

    /**
     * 根据键名取值
     * @param key 键名
     * @return 成功返回值, 失败返回null
     */
    public String getParamValueByKey(String key) {
        SysParamEntity sysParamEntity = this.getOne(
                new QueryWrapper<SysParamEntity>()
                        .select(" value ")
                        .lambda()
                        .eq(SysParamEntity::getName, key)
                        .last(RcConstant.CRUD.SQL_LIMIT_1)
        );
        if (sysParamEntity == null) {
            return null;
        }

        return sysParamEntity.getValue();
    }

    /**
     * 根据键名取值
     *
     * @param key          键名
     * @param defaultValue 默认值
     * @return 成功返回值, 失败返回defaultValue
     */
    public String getParamValueByKey(String key, String defaultValue) {
        String value = this.getParamValueByKey(key);
        if (value == null) {
            return defaultValue;
        }

        return value;
    }


    /*
    私有方法
    ------------------------------------------------------------------------------------------------
     */

    private SysParamVO entity2BO(SysParamEntity entity) {
        if (entity == null) {
            return null;
        }

        SysParamVO bo = new SysParamVO();
        BeanUtil.copyProperties(entity, bo);

        // 可以在此处为BO填充字段

        return bo;
    }

    private PageResult<SysParamVO> entityPage2BOPage(Page<SysParamEntity> entityPage) {
        // 深拷贝
        List<SysParamVO> boRecords = new ArrayList<>(entityPage.getRecords().size());
        entityPage.getRecords().forEach(
                entity -> boRecords.add(this.entity2BO(entity))
        );

        PageResult<SysParamVO> ret = new PageResult<>();
        BeanUtil.copyProperties(entityPage, ret);
        ret.setRecords(boRecords);
        return ret;
    }

    /**
     * 检查是否已存在相同数据
     *
     * @param dto DTO
     */
    private void checkExistence(AdminInsertOrUpdateSysParamDTO dto) {
        SysParamEntity existingEntity = this.getOne(
                new QueryWrapper<SysParamEntity>()
                        .select(RcConstant.CRUD.SQL_COLUMN_ID)
                        .lambda()
                        .eq(SysParamEntity::getDescription, dto.getDescription())
                        .or()
                        .eq(SysParamEntity::getName, dto.getName())
                        .last(RcConstant.CRUD.SQL_LIMIT_1)
        );

        if (existingEntity != null && !existingEntity.getId().equals(dto.getId())) {
            throw new BusinessException(400, "已存在相同系统参数，请重新输入");
        }
    }
}
