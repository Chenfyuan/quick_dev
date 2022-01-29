#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.module.sys.service;

import cc.rc.framework.crud.service.RcBaseService;
import org.springframework.transaction.annotation.Transactional;
import ${package}.module.sys.annotation.SysLog;
import ${package}.module.sys.entity.SysDeptEntity;
import ${package}.module.sys.model.request.AdminInsertOrUpdateSysDeptDTO;
import ${package}.module.sys.model.request.AdminListSysDeptDTO;
import ${package}.module.sys.model.response.SysDeptVO;

import java.util.List;

/**
 * TODO
 *
 * @author weijian.lin
 * @version 1.0
 * @date 2022/1/22 15:31
 */
public interface SysDeptService extends RcBaseService<SysDeptEntity> {
    List<SysDeptVO> adminList(AdminListSysDeptDTO dto);

    SysDeptVO getOneById(Long entityId);

    @SysLog(value = "新增部门")
    @Transactional(rollbackFor = Exception.class)
    Long adminInsert(AdminInsertOrUpdateSysDeptDTO dto);

    @SysLog(value = "编辑部门")
    @Transactional(rollbackFor = Exception.class)
    void adminUpdate(AdminInsertOrUpdateSysDeptDTO dto);

    @SysLog(value = "删除部门")
    @Transactional(rollbackFor = Exception.class)
    void adminDelete(List<Long> ids);

    SysDeptVO getPlainDeptByUserId(Long userId);
}
