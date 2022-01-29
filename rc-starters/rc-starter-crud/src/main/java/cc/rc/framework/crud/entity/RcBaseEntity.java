package cc.rc.framework.crud.entity;

import cc.rc.framework.core.constant.RcConstant;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Linweijian
 */
@Accessors(chain = true)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public abstract class RcBaseEntity<PK> implements Serializable {

    /**
     * 主键ID
     */
    @ApiModelProperty(value = "主键ID")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private PK id;

    /**
     * 行级租户ID
     */
    @ApiModelProperty(value = "行级租户ID")
    @TableField(value = RcConstant.CRUD.COLUMN_TENANT_ID, fill = FieldFill.INSERT)
    private Long tenantId;

    /**
     * 乐观锁
     * 需自行加@Version注解才有效
     */
    @ApiModelProperty(value = "乐观锁(需自行加@Version注解才有效)")
    @TableField(value = "revision")
    private Long revision;

    /**
     * 逻辑删除标识
     */
    @ApiModelProperty(value = "逻辑删除标识")
    @TableLogic
    @TableField(value = "del_flag")
    private Integer delFlag;

    /**
     * 创建时刻
     */
    @ApiModelProperty(value = "创建时刻")
    @DateTimeFormat(pattern = RcConstant.Jackson.DATE_TIME_FORMAT)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = RcConstant.Jackson.DATE_TIME_FORMAT, timezone = RcConstant.Jackson.TIME_ZONE)
    @TableField(value = RcConstant.CRUD.COLUMN_CREATED_AT, fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 创建者
     */
    @ApiModelProperty(value = "创建者")
    @TableField(value = RcConstant.CRUD.COLUMN_CREATED_BY, fill = FieldFill.INSERT)
    private String createdBy;

    /**
     * 更新时刻
     */
    @ApiModelProperty(value = "更新时刻")
    @DateTimeFormat(pattern = RcConstant.Jackson.DATE_TIME_FORMAT)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = RcConstant.Jackson.DATE_TIME_FORMAT, timezone = RcConstant.Jackson.TIME_ZONE)
    @TableField(value = RcConstant.CRUD.COLUMN_UPDATED_AT, fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 更新者
     */
    @ApiModelProperty(value = "更新者")
    @TableField(value = RcConstant.CRUD.COLUMN_UPDATED_BY, fill = FieldFill.INSERT_UPDATE)
    private String updatedBy;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    @TableField(value = "remark")
    private String remark;

}
