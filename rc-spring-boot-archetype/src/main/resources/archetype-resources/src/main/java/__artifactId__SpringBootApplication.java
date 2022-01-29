#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import cc.rc.framework.crud.annotation.EnableInitHikariPoolAtStartup;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * @author linweijian
 */
@EnableInitHikariPoolAtStartup
@SpringBootApplication
public class ${artifactId}SpringBootApplication {
    public static void main(String[] args) {
        SpringApplication.run(${artifactId}SpringBootApplication.class, args);
    }

}
