#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.module.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import ${package}.module.sys.entity.SysParamEntity;

/**
 * 系统参数
 * @author linweijian
 */
@Mapper
public interface SysParamMapper extends BaseMapper<SysParamEntity> {
	
}
