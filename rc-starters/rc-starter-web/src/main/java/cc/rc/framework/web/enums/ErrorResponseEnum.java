package cc.rc.framework.web.enums;


import cc.rc.framework.core.enums.RcBaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorResponseEnum implements RcBaseEnum<Integer> {
    ILLEGAL_ENUM_VALUE(400, "非法枚举值"),
    CONTAINS_ILLEGAL_CHARACTER(400, "包含非法字符"),
    ;

    private final Integer value;
    private final String label;
}
