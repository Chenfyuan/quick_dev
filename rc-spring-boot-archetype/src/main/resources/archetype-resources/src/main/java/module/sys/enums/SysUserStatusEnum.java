#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.module.sys.enums;

import cc.rc.framework.core.enums.RcBaseEnum;
import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;


/**
 * 后台用户状态枚举类
 * @author linweijian
 */
@AllArgsConstructor
@Getter
public enum SysUserStatusEnum implements RcBaseEnum<Integer> {

    /**
     * 封禁
     */
    BANNED(0, "封禁"),

    /**
     * 正常
     */
    ENABLED(1, "正常"),
    ;

    @EnumValue
    private final Integer value;
    private final String label;

}
