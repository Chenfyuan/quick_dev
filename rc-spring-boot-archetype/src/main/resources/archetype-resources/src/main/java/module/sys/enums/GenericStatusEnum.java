#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.module.sys.enums;

import cc.rc.framework.core.enums.RcBaseEnum;
import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;


/**
 * 通用状态枚举类
 * @author linweijian
 */
@AllArgsConstructor
@Getter
public enum GenericStatusEnum implements RcBaseEnum<Integer> {

    /**
     * 禁用
     */
    DISABLED(0, "禁用"),

    /**
     * 启用
     */
    ENABLED(1, "启用"),
    ;

    @EnumValue
    private final Integer value;
    private final String label;

}
