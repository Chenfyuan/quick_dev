#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.module.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import ${package}.module.sys.entity.SysRoleMenuRelationEntity;

/**
 * 后台角色-可见菜单关联
 * @author linweijian
 */
@Mapper
public interface SysRoleMenuRelationMapper extends BaseMapper<SysRoleMenuRelationEntity> {
	
}
