package cc.rc.framework.core.context;

import cc.rc.framework.core.constant.RcConstant;
import cc.rc.framework.core.enums.RcBaseEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 当前用户上下文对象
 * @author Linweijian
 */
@Accessors(chain = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserContext implements Serializable {

    private static final long SerialVersionUID = 1L;

    @ApiModelProperty(value = "用户ID")
    private Long userId;

    @ApiModelProperty(value = "用户账号")
    private String userName;

    @ApiModelProperty(value = "用户手机号")
    private String userPhoneNo;

    @ApiModelProperty(value = "用户类型")
    private RcBaseEnum<?> userType;

    @ApiModelProperty(value = "用户对应角色ID数组(后台管理使用, 建议从小到大排序)")
    private List<Long> rolesIds;

    @ApiModelProperty(value = "用户对应角色串(后台管理使用)")
    private List<String> roles;

    @ApiModelProperty(value = "用户拥有权限串(后台管理使用, 决定可见菜单及按钮)")
    private List<String> permissions;

    @ApiModelProperty(value = "附加数据")
    private Map<String, Object> extraData;

    @ApiModelProperty(value = "所属租户")
    private TenantContext relationalTenant = TenantContext.builder()
            .tenantId(RcConstant.CRUD.DEFAULT_TENANT_ID)
            .tenantName(RcConstant.CRUD.DEFAULT_TENANT_NAME)
            .build();

    @ApiModelProperty(value = "客户端IP地址")
    private String clientIP;

}
