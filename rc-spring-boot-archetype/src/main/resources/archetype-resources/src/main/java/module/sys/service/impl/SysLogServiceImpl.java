#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.module.sys.service.impl;

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
import ${package}.module.sys.entity.SysLogEntity;
import ${package}.module.sys.enums.SysErrorEnum;
import ${package}.module.sys.mapper.SysLogMapper;
import ${package}.module.sys.model.request.AdminListSysLogDTO;
import ${package}.module.sys.model.response.SysLogVO;
import ${package}.module.sys.service.SysLogService;

import java.util.ArrayList;
import java.util.List;


/**
 * 后台操作日志
 * @author linweijian
 */
@Slf4j
@Service
public class SysLogServiceImpl extends RcBaseServiceImpl<SysLogMapper, SysLogEntity> implements SysLogService {

    /**
     * 后台管理-分页列表
     */
    @Override
    public PageResult<SysLogVO> adminList(PageParam pageParam, AdminListSysLogDTO dto) {
        Page<SysLogEntity> entityPage = this.page(
                new Page<>(pageParam.getPageNum(), pageParam.getPageSize()),
                new QueryWrapper<SysLogEntity>()
                        .lambda()
                        // 用户账号
                        .like(StrUtil.isNotBlank(dto.getUsername()), SysLogEntity::getUsername, StrUtil.cleanBlank(dto.getUsername()))
                        // 操作内容
                        .like(StrUtil.isNotBlank(dto.getOperation()), SysLogEntity::getOperation, StrUtil.cleanBlank(dto.getOperation()))
                        // 状态
                        .eq(ObjectUtil.isNotNull(dto.getStatus()), SysLogEntity::getStatus, dto.getStatus())
                        // 时间区间
                        .between(ObjectUtil.isNotNull(dto.getBeginAt()) && ObjectUtil.isNotNull(dto.getEndAt()), SysLogEntity::getCreatedAt, dto.getBeginAt(), dto.getEndAt())
                        // 排序
                        .orderByDesc(SysLogEntity::getCreatedAt)
        );

        return this.entityPage2BOPage(entityPage);
    }

    /**
     * 通用-详情
     */
    @Override
    public SysLogVO getOneById(Long entityId) {
        SysLogEntity entity = this.getById(entityId);
        SysErrorEnum.INVALID_ID.assertNotNull(entity);

        return this.entity2BO(entity);
    }




    /*
    私有方法
    ------------------------------------------------------------------------------------------------
     */

    private SysLogVO entity2BO(SysLogEntity entity) {
        if (entity == null) {
            return null;
        }

        SysLogVO bo = new SysLogVO();
        BeanUtil.copyProperties(entity, bo);

        // 可以在此处为BO填充字段

        return bo;
    }

    private List<SysLogVO> entityList2BOs(List<SysLogEntity> entityList) {
        // 深拷贝
        List<SysLogVO> ret = new ArrayList<>(entityList.size());
        entityList.forEach(
                entity -> ret.add(this.entity2BO(entity))
        );

        return ret;
    }

    private PageResult<SysLogVO> entityPage2BOPage(Page<SysLogEntity> entityPage) {
        PageResult<SysLogVO> ret = new PageResult<>();
        BeanUtil.copyProperties(entityPage, ret);
        ret.setRecords(this.entityList2BOs(entityPage.getRecords()));

        return ret;
    }

}