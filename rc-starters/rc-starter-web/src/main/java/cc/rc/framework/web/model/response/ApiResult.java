package cc.rc.framework.web.model.response;

import cc.rc.framework.core.constant.RcConstant;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * HTTP接口通用返回对象
 *
 * @author Linweijian
 */
@Accessors(chain = true)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ApiResult<T> implements Serializable {

    /**
     * 状态码
     **/
    @ApiModelProperty(value = "状态码")
    private Integer code;


    /**
     * 返回消息
     **/
    @ApiModelProperty(value = "返回消息")
    private String msg;
    /**
     * 数据体
     **/
    @ApiModelProperty(value = "承载数据")
    private T data;

    public static <T> ApiResult<T> success() {
        return build(HttpStatus.HTTP_OK, RcConstant.Message.SUCCESS, null);
    }

    public static <T> ApiResult<T> success(String msg) {
        return build(HttpStatus.HTTP_OK, msg, null);
    }

    public static <T> ApiResult<T> fail(Integer code, String msg) {
        return build(code, msg, null);
    }

    public static <T> ApiResult<T> fail(Integer code, String msg, T data) {
        return build(code, msg, data);
    }

    public static <T> ApiResult<T> data(T data) {
        return build(HttpStatus.HTTP_OK, ObjectUtil.isEmpty(data) ? RcConstant.Message.NULL : RcConstant.Message.SUCCESS, data);
    }

    public static <T> ApiResult<T> data(String msg, T data) {
        return build(HttpStatus.HTTP_OK, msg, data);
    }

    private static <T> ApiResult<T> build(Integer code, String msg, T data) {
        ApiResult<T> ret = new ApiResult<>();
        ret
                .setCode(code)
                .setMsg(msg)
                .setData(data);

        return ret;
    }

}
