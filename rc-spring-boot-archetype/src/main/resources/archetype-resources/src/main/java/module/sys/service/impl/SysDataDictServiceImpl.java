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
import ${package}.module.sys.entity.SysDataDictEntity;
import ${package}.module.sys.enums.SysErrorEnum;
import ${package}.module.sys.mapper.SysDataDictMapper;
import ${package}.module.sys.model.request.AdminInsertOrUpdateSysDataDictDTO;
import ${package}.module.sys.model.request.AdminListSysDataDictDTO;
import ${package}.module.sys.model.response.SysDataDictVO;
import ${package}.module.sys.service.ISysDataDictService;

import java.util.ArrayList;
import java.util.List;


/**
 * 数据字典
 *
 * @author linweijian
 */
@Slf4j
@Service
public class SysDataDictServiceImpl extends RcBaseServiceImpl<SysDataDictMapper, SysDataDictEntity> implements ISysDataDictService {

    /**
     * 后台管理-分页列表
     */
    @Override
    public PageResult<SysDataDictVO> adminList(PageParam pageParam, AdminListSysDataDictDTO dto) {
        Page<SysDataDictEntity> entityPage = this.page(
                new Page<>(pageParam.getPageNum(), pageParam.getPageSize()),
                new QueryWrapper<SysDataDictEntity>()
                        .lambda()
                        // 参数描述
                        .like(StrUtil.isNotBlank(dto.getDescription()), SysDataDictEntity::getDescription, StrUtil.cleanBlank(dto.getDescription()))
                        // 排序
                        .orderByDesc(SysDataDictEntity::getCreatedAt)
        );

        return this.entityPage2BOPage(entityPage);
    }

    /**
     * 通用-详情
     */
    @Override
    public SysDataDictVO getOneById(Long entityId) {
        SysDataDictEntity entity = this.getById(entityId);
        SysErrorEnum.INVALID_ID.assertNotNull(entity);

        return this.entity2BO(entity);
    }

    /**
     * 后台管理-新增
     */
    @Override
    @SysLog(value = "新增数据字典")
    @Transactional(rollbackFor = Exception.class)
    public Long adminInsert(AdminInsertOrUpdateSysDataDictDTO dto) {
        this.checkExistence(dto);

        dto.setId(null);
        SysDataDictEntity entity = new SysDataDictEntity();
        BeanUtil.copyProperties(dto, entity);

        this.save(entity);

        return entity.getId();
    }

    /**
     * 后台管理-编辑
     */
    @Override
    @SysLog(value = "编辑数据字典")
    @Transactional(rollbackFor = Exception.class)
    public void adminUpdate(AdminInsertOrUpdateSysDataDictDTO dto) {
        this.checkExistence(dto);

        SysDataDictEntity entity = new SysDataDictEntity();
        BeanUtil.copyProperties(dto, entity);

        this.updateById(entity);
    }

    /**
     * 后台管理-删除
     */
    @Override
    @SysLog(value = "删除数据字典")
    @Transactional(rollbackFor = Exception.class)
    public void adminDelete(List<Long> ids) {
        this.removeByIds(ids);
    }


    /*
    私有方法
    ------------------------------------------------------------------------------------------------
     */

    private SysDataDictVO entity2BO(SysDataDictEntity entity) {
        if (entity == null) {
            return null;
        }

        SysDataDictVO bo = new SysDataDictVO();
        BeanUtil.copyProperties(entity, bo);

        // 可以在此处为BO填充字段

        return bo;
    }

    private List<SysDataDictVO> entityList2BOs(List<SysDataDictEntity> entityList) {
        // 深拷贝
        List<SysDataDictVO> ret = new ArrayList<>(entityList.size());
        entityList.forEach(
                entity -> ret.add(this.entity2BO(entity))
        );

        return ret;
    }

    private PageResult<SysDataDictVO> entityPage2BOPage(Page<SysDataDictEntity> entityPage) {
        PageResult<SysDataDictVO> ret = new PageResult<>();
        BeanUtil.copyProperties(entityPage, ret);
        ret.setRecords(this.entityList2BOs(entityPage.getRecords()));

        return ret;
    }

    /**
     * 检查是否已存在相同数据
     *
     * @param dto DTO
     */
    private void checkExistence(AdminInsertOrUpdateSysDataDictDTO dto) {
        SysDataDictEntity existingEntity = this.getOne(
                new QueryWrapper<SysDataDictEntity>()
                        .select(RcConstant.CRUD.SQL_COLUMN_ID)
                        .lambda()
                        .eq(SysDataDictEntity::getCamelCaseKey, dto.getCamelCaseKey())
                        .or()
                        .eq(SysDataDictEntity::getPascalCaseKey, dto.getPascalCaseKey())
                        .or()
                        .eq(SysDataDictEntity::getUnderCaseKey, dto.getUnderCaseKey())
                        .last(RcConstant.CRUD.SQL_LIMIT_1)
        );

        if (existingEntity != null && !existingEntity.getId().equals(dto.getId())) {
            throw new BusinessException(400, "已存在相同数据字典，请重新输入");
        }
    }

}
