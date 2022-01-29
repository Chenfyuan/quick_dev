package cc.rc.framework.core.exception;

import cc.rc.framework.core.enums.RcBaseEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 业务异常类
 * @author Linweijian
 */
@NoArgsConstructor
@Getter
public class BusinessException extends RuntimeException {

    private Integer code = null;

    public BusinessException(String msg) {
        super(msg);
    }

    public BusinessException(int code, String msg) {
        super(msg);
        this.code = code;
    }

    public BusinessException(RcBaseEnum<Integer> customEnum) {
        super(customEnum.getLabel());
        this.code = customEnum.getValue();
    }

    /**
     * 关闭爬栈
     */
    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
