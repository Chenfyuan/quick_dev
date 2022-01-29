#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.module.sys.entity;

import cc.rc.framework.crud.entity.RcBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;


/**
 * 系统参数
 * @author linweijian
 */
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName(value = "sys_param")
public class SysParamEntity extends RcBaseEntity<Long> {

    @ApiModelProperty(value = "键名")
    @TableField(value = "name")
    private String name;

    @ApiModelProperty(value = "键值")
    @TableField(value = "value")
    private String value;

    @ApiModelProperty(value = "描述")
    @TableField(value = "description")
    private String description;

}
