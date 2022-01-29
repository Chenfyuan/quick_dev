#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.module.sys.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import ${package}.module.sys.entity.SysUserEntity;
import ${package}.module.sys.model.response.SysUserBaseInfoVO;

import java.time.LocalDateTime;

/**
 * 后台用户
 *
 * @author linweijian
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUserEntity> {

    @InterceptorIgnore(tenantLine = "true")
    void updateLastLoginAt(Long userId, LocalDateTime lastLoginAt);

    @InterceptorIgnore(tenantLine = "true")
    SysUserBaseInfoVO getBaseInfoByUserId(Long userId);

}
