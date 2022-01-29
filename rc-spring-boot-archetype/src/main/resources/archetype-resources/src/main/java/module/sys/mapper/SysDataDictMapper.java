#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.module.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import ${package}.module.sys.entity.SysDataDictEntity;


/**
 * 数据字典
 *
 * @author linweijian
 */
@Mapper
public interface SysDataDictMapper extends BaseMapper<SysDataDictEntity> {

}
