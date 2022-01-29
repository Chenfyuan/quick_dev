#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.module.sys.model.response;

import cc.rc.framework.core.constant.RcConstant;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;
import ${package}.module.sys.enums.GenderEnum;
import ${package}.module.sys.enums.SysUserStatusEnum;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;


/**
 * 后台用户BO
 * @author linweijian
 */
@ApiModel(value = "后台用户")
@Accessors(chain = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SysUserVO implements Serializable {

    @ApiModelProperty(value = "主键ID")
    private Long id;

    @ApiModelProperty(value = "创建时刻")
    @DateTimeFormat(pattern = RcConstant.Jackson.DATE_TIME_FORMAT)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = RcConstant.Jackson.DATE_TIME_FORMAT, timezone = RcConstant.Jackson.TIME_ZONE)
    private LocalDateTime createdAt;

    @ApiModelProperty(value = "更新时刻")
    @DateTimeFormat(pattern = RcConstant.Jackson.DATE_TIME_FORMAT)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = RcConstant.Jackson.DATE_TIME_FORMAT, timezone = RcConstant.Jackson.TIME_ZONE)
    private LocalDateTime updatedAt;

    @ApiModelProperty(value = "账号")
    private String username;

    @ApiModelProperty(value = "昵称")
    private String nickname;

    @ApiModelProperty(value = "状态")
    private SysUserStatusEnum status;

    @ApiModelProperty(value = "性别")
    private GenderEnum gender;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "手机号")
    private String phoneNo;

    @ApiModelProperty(value = "最后登录时刻")
    private LocalDateTime lastLoginAt;

    @ApiModelProperty(value = "拥有角色")
    private List<SysRoleVO> roles;

    @ApiModelProperty(value = "拥有角色Ids")
    private List<Long> roleIds;

    @ApiModelProperty(value = "拥有权限")
    private List<String> permissions;

    @ApiModelProperty(value = "所属部门ID")
    private Long deptId;

    @ApiModelProperty(value = "所属部门名称")
    private String deptTitle;

}
