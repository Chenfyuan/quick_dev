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
import ${package}.module.sys.enums.GenericStatusEnum;
import ${package}.module.sys.enums.SysMenuTypeEnum;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;


/**
 * 后台菜单BO
 * @author linweijian
 */
@ApiModel(value = "后台菜单")
@Accessors(chain = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SysMenuVO implements Serializable {

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

    @ApiModelProperty(value = "名称")
    private String title;

    @ApiModelProperty(value = "上级菜单ID")
    private Long parentId;

    @ApiModelProperty(value = "菜单类型(参考MenuTypeEnum)")
    private SysMenuTypeEnum type;

    @ApiModelProperty(value = "权限标识")
    private String permission;

    @ApiModelProperty(value = "图标")
    private String icon;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "状态(0=禁用 1=启用)")
    private GenericStatusEnum status;

    @ApiModelProperty(value = "组件")
    private String component;

    @ApiModelProperty(value = "外链地址")
    private String externalLink;

    @ApiModelProperty(value = "子项数据")
    private List<SysMenuVO> children;

    @ApiModelProperty(value = "【用于Vben Admin】路由地址", hidden = true)
    private String path;

    @ApiModelProperty(value = "【用于Vben Admin】菜单名(全局唯一, 不能重复)", hidden = true)
    private String name;

    @ApiModelProperty(value = "【用于Vben Admin】菜单详情", hidden = true)
    private VbenAdminMenuMetaVO meta;

    @ApiModelProperty(value = "【用于Vben Admin树状菜单】菜单主键ID字符串", hidden = true)
    private String idStr;

}
