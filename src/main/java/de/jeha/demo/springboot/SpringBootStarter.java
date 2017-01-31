package de.jeha.demo.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.JmsAutoConfiguration;

/**
 * @author jenshadlich@googlemail.com
 */
@SpringBootApplication(exclude = {JmsAutoConfiguration.class, DataSourceAutoConfiguration.class})
public class SpringBootStarter {

    public static void main(String[] args) throws Exception {
        SpringApplication application = new SpringApplication(SpringBootStarter.class);
        application.run(args);
    }

}
