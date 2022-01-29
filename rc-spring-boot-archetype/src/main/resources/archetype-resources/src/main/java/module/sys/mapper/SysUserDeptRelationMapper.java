#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.module.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import ${package}.module.sys.entity.SysUserDeptRelationEntity;

/**
 * 后台用户-部门关联
 * @author linweijian
 */
@Mapper
public interface SysUserDeptRelationMapper extends BaseMapper<SysUserDeptRelationEntity> {
	
}
