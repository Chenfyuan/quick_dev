package cc.rc.framework.core.config;

import cc.rc.framework.core.props.RcProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 自动配置文件读取
 *
 * @author Linweijian
 */
@EnableConfigurationProperties(value = {RcProperties.class})
@Configuration
public class RcPropertiesAutoConfiguration {
}
