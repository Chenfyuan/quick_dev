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
import ${package}.module.sys.enums.GenericStatusEnum;


/**
 * 系统租户
 * @author linweijian
 */
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName(value = "sys_tenant")
public class SysTenantEntity extends RcBaseEntity<Long> {

    @ApiModelProperty(value = "租户名")
    @TableField(value = "tenant_name")
    private String tenantName;

    @ApiModelProperty(value = "状态(0=禁用 1=启用)")
    @TableField(value = "status")
    private GenericStatusEnum status;

    @ApiModelProperty(value = "租户管理员用户ID")
    @TableField(value = "tenant_admin_user_id")
    private Long tenantAdminUserId;

}
