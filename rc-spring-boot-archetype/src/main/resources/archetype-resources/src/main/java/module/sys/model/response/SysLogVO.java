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
import ${package}.module.sys.enums.SysLogStatusEnum;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * 后台操作日志BO
 * @author linweijian
 */
@ApiModel(value = "后台操作日志")
@Accessors(chain = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SysLogVO implements Serializable {

    @ApiModelProperty(value = "创建时刻")
    @DateTimeFormat(pattern = RcConstant.Jackson.DATE_TIME_FORMAT)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = RcConstant.Jackson.DATE_TIME_FORMAT, timezone = RcConstant.Jackson.TIME_ZONE)
    private LocalDateTime createdAt;

    @ApiModelProperty(value = "用户账号")
    private String username;

    @ApiModelProperty(value = "操作内容")
    private String operation;

    @ApiModelProperty(value = "IP地址")
    private String ip;

    @ApiModelProperty(value = "状态(0=未执行 1=成功)")
    private SysLogStatusEnum status;

}
