#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.module.sys.model.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import ${package}.module.sys.enums.GenderEnum;
import ${package}.module.sys.enums.SysUserStatusEnum;

import java.io.Serializable;


/**
 * 后台用户BO
 * 仅保留基本信息
 * @author linweijian
 */
@ApiModel(value = "后台用户基本信息")
@Accessors(chain = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SysUserBaseInfoVO implements Serializable {

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

}
