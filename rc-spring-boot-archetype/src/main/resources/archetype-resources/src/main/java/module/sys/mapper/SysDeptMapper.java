#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.module.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import ${package}.module.sys.entity.SysDeptEntity;

/**
 * 部门
 * @author linweijian
 */
@Mapper
public interface SysDeptMapper extends BaseMapper<SysDeptEntity> {
	
}
