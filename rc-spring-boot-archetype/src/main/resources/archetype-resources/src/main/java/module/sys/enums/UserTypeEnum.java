#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.module.sys.enums;


import cc.rc.framework.core.enums.RcBaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;


/**
 * 用户类型枚举类
 * @author linweijian
 */
@AllArgsConstructor
@Getter
public enum UserTypeEnum implements RcBaseEnum<Integer> {

    /**
     * 后台管理用户
     */
    ADMIN_USER(1, "后台管理用户"),

    /**
     * C端个人用户
     * 未来可以自行扩展企业用户
     */
    APP_USER_INDIVIDUAL(2, "C端个人用户"),
    ;

    private final Integer value;
    private final String label;

}
