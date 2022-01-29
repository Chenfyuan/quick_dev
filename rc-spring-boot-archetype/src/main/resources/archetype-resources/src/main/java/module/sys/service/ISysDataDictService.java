#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.module.sys.service;

import cc.rc.framework.core.page.PageParam;
import cc.rc.framework.core.page.PageResult;
import cc.rc.framework.crud.service.RcBaseService;
import ${package}.module.sys.entity.SysDataDictEntity;
import ${package}.module.sys.model.request.AdminInsertOrUpdateSysDataDictDTO;
import ${package}.module.sys.model.request.AdminListSysDataDictDTO;
import ${package}.module.sys.model.response.SysDataDictVO;

import java.util.List;

/**
 * TODO
 *
 * @author weijian.lin
 * @version 1.0
 * @date 2022/1/22 15:22
 */
public interface ISysDataDictService extends RcBaseService<SysDataDictEntity> {
    PageResult<SysDataDictVO> adminList(PageParam pageParam, AdminListSysDataDictDTO dto);

    SysDataDictVO getOneById(Long entityId);

    Long adminInsert(AdminInsertOrUpdateSysDataDictDTO dto);

    void adminUpdate(AdminInsertOrUpdateSysDataDictDTO dto);

    void adminDelete(List<Long> ids);
}
