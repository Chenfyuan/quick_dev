#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.module.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import ${package}.module.sys.entity.SysLogEntity;

/**
 * 后台操作日志
 * @author linweijian
 */
@Mapper
public interface SysLogMapper extends BaseMapper<SysLogEntity> {
	
}
