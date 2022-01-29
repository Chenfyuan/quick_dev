#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.module.sys.annotation;

import java.lang.annotation.*;


/**
 * 放在方法上，可将操作记录至系统日志中
 *
 * @author linweijian
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface SysLog {

    /**
     * 操作内容(如:新增部门)
     */
    String value() default "";

}
