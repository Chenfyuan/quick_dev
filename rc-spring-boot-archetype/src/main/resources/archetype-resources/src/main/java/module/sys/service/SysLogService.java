#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.module.sys.service;

import cc.rc.framework.core.page.PageParam;
import cc.rc.framework.core.page.PageResult;
import cc.rc.framework.crud.service.RcBaseService;
import ${package}.module.sys.entity.SysLogEntity;
import ${package}.module.sys.model.request.AdminListSysLogDTO;
import ${package}.module.sys.model.response.SysLogVO;

/**
 * TODO
 *
 * @author weijian.lin
 * @version 1.0
 * @date 2022/1/22 15:33
 */
public interface SysLogService extends RcBaseService<SysLogEntity> {
    PageResult<SysLogVO> adminList(PageParam pageParam, AdminListSysLogDTO dto);

    SysLogVO getOneById(Long entityId);
}
