package cc.rc.framework.core.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 主键ID生成器策略枚举类
 * @author Linweijian
 */
@AllArgsConstructor
@Getter
public enum IdGeneratorStrategyEnum implements RcBaseEnum<Integer> {

    /**
     * Twitter雪花算法
     */
    SNOWFLAKE(1, "SNOWFLAKE");

    private final Integer value;
    private final String label;

}
