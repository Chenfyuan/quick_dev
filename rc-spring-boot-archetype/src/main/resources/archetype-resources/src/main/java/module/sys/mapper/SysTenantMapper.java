#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.module.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import ${package}.module.sys.entity.SysTenantEntity;

/**
 * 系统租户
 * @author linweijian
 */
@Mapper
public interface SysTenantMapper extends BaseMapper<SysTenantEntity> {
	
}
