#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.module.sys.enums;

import cc.rc.framework.core.enums.RcBaseEnum;
import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;


/**
 * 系统日志状态枚举类
 * @author linweijian
 */
@AllArgsConstructor
@Getter
public enum SysLogStatusEnum implements RcBaseEnum<Integer> {

    /**
     * 未执行
     */
    NON_EXECUTION(0, "未执行"),

    /**
     * 成功
     */
    SUCCESS(1, "成功"),
    ;

    @EnumValue
    private final Integer value;
    private final String label;

}
