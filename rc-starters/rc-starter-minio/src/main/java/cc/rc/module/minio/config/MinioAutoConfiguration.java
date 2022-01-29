package cc.rc.module.minio.config;

import io.minio.MinioClient;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * minio自动配置类
 *
 * @author Linweijian
 */

@Configuration
@ConditionalOnClass(MinioConfig.class)
@EnableConfigurationProperties(MinioConfig.class)
public class MinioAutoConfiguration {
    private final MinioConfig config;

    public MinioAutoConfiguration(MinioConfig config) {
        this.config = config;
    }

    @Bean
    @ConditionalOnMissingBean
    public MinioClient getMinioClient() throws InvalidEndpointException, InvalidPortException {
        return new MinioClient(config.getEndpoint(), config.getPort(), config.getAccessKey(), config.getSecretKey(), config.getSecure());
    }
}
