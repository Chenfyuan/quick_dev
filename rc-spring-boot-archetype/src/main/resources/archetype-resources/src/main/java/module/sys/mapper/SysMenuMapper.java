#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.module.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import ${package}.module.sys.entity.SysMenuEntity;

/**
 * 后台菜单
 * @author linweijian
 */
@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenuEntity> {
	
}
