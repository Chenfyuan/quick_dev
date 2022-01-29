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
import ${package}.module.sys.enums.SysMenuTypeEnum;


/**
 * 后台菜单
 * @author linweijian
 */
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName(value = "sys_menu")
public class SysMenuEntity extends RcBaseEntity<Long> {

	@ApiModelProperty(value = "名称")
	@TableField(value = "title")
	private String title;

	@ApiModelProperty(value = "上级菜单ID")
	@TableField(value = "parent_id")
	private Long parentId;

	@ApiModelProperty(value = "菜单类型(参考MenuTypeEnum)")
	@TableField(value = "type")
	private SysMenuTypeEnum type;

	@ApiModelProperty(value = "权限标识")
	@TableField(value = "permission")
	private String permission;

	@ApiModelProperty(value = "图标")
	@TableField(value = "icon")
	private String icon;

	@ApiModelProperty(value = "排序")
	@TableField(value = "sort")
	private Integer sort;

	@ApiModelProperty(value = "状态(0=禁用 1=启用)")
	@TableField(value = "status")
	private GenericStatusEnum status;

	@ApiModelProperty(value = "组件", notes = "Vue项目中`/@/views/`的子路径; `LAYOUT`为空页面")
	@TableField(value = "component")
	private String component;

	@ApiModelProperty(value = "外链地址")
	@TableField(value = "external_link")
	private String externalLink;

}
