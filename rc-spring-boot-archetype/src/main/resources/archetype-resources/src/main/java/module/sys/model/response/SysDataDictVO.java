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

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * 数据字典BO
 *
 * @author linweijian
 */
@ApiModel(value = "数据字典")
@Accessors(chain = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SysDataDictVO implements Serializable {

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

    @ApiModelProperty(value = "驼峰式键名")
    private String camelCaseKey;

    @ApiModelProperty(value = "下划线式键名")
    private String underCaseKey;

    @ApiModelProperty(value = "帕斯卡式键名")
    private String pascalCaseKey;

    @ApiModelProperty(value = "键值")
    private String value;

    @ApiModelProperty(value = "参数描述")
    private String description;

    @ApiModelProperty(value = "单位")
    private String unit;

    @ApiModelProperty(value = "取值范围")
    private String valueRange;

    @ApiModelProperty(value = "别称键名")
    private String aliasKey;

}
