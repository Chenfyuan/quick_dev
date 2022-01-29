#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.module.sys.enums;

import cc.rc.framework.core.enums.RcBaseEnum;
import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;


/**
 * 生理性别枚举类
 * @author linweijian
 */
@AllArgsConstructor
@Getter
public enum GenderEnum implements RcBaseEnum<Integer> {

    /**
     * 未知
     */
    UNKNOWN(0, "未知"),

    /**
     * 男
     */
    MALE(1, "男"),

    /**
     * 女
     */
    FEMALE(2, "女"),
    ;

    @EnumValue
    private final Integer value;
    private final String label;

}
