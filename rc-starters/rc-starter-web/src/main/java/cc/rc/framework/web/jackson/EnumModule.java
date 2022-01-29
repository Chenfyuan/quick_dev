package cc.rc.framework.web.jackson;

import cc.rc.framework.core.enums.RcBaseEnum;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

/**
 * 继承了BaseEnum接口的枚举值，将会统一按照以下格式序列化
 * {
 * "value": "foo",
 * "valueLabel": "bar"
 * }
 *
 * @author Zhu JW
 * @author Linweijian
 **/
public class EnumModule extends SimpleModule {
    public EnumModule() {
        super("jacksonEnumTypeModule", Version.unknownVersion());
        this.setDeserializers(new CustomDeserializers());
        this.addSerializer(new EnumSerializer());
    }

    private static class CustomDeserializers extends SimpleDeserializers {
        private CustomDeserializers() {

        }

        @Override
        @SuppressWarnings({"rawtypes", "unchecked"})
        public JsonDeserializer<?> findEnumDeserializer(Class<?> type, DeserializationConfig config, BeanDescription beanDesc) throws JsonMappingException {
            // RcBaseEnum<?>，调用此序列化方法，否则使用 jackson 默认的序列化方法
            return RcBaseEnum.class.isAssignableFrom(type) ?
                    new EnumDeserializer(type) :
                    super.findEnumDeserializer(type, config, beanDesc);
        }

        private static class EnumDeserializer<E extends RcBaseEnum<?>> extends StdScalarDeserializer<E> {
            private final Class<E> enumType;

            private EnumDeserializer(Class<E> clazz) {
                super(clazz);
                this.enumType = clazz;
            }

            @Override
            public E deserialize(JsonParser parser, DeserializationContext context) throws IOException {
                // 前台如果传递只传value
                if (parser.getCurrentToken().isNumeric()) {
                    return RcBaseEnum.of(this.enumType, parser.getIntValue()).orElseThrow(() -> new IllegalArgumentException("非法输入值"));
                } else if (StrUtil.isNotBlank(parser.getText())) {
                    return RcBaseEnum.of(this.enumType, parser.getText()).orElseThrow(() -> new IllegalArgumentException("非法输入值"));
                } else {
                    throw new IllegalArgumentException("枚举值类型错误");
                }
            }
        }
    }

    @SuppressWarnings({"rawtypes"})
    private static class EnumSerializer extends StdSerializer<RcBaseEnum> {

        private EnumSerializer() {
            super(RcBaseEnum.class);
        }

        @Override
        public void serialize(RcBaseEnum data, JsonGenerator jsonGenerator, SerializerProvider provider) throws IOException {
            jsonGenerator.writeObject(data.getValue());
            jsonGenerator.writeFieldName(jsonGenerator.getOutputContext().getCurrentName() + "Label");
            jsonGenerator.writeString(data.getLabel());
        }
    }

}
