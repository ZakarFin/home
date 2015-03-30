package fi.zakar;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.*;

/**
 * Created by zakar on 16/03/15.
 */
@Configuration
@ComponentScan
@EnableAutoConfiguration
@Import(ThymeleafConfig.class)
@PropertySource(value = {"classpath:application.properties", "classpath:lights.properties", "file:lights-ext.properties"}, ignoreResourceNotFound=true)
public class LightsApp {

    public static void main(String[] args) {
        SpringApplication.run(LightsApp.class, args);
    }

    @Value("${app.port:8888}")
    private int port = 8888;

    @Bean
    public EmbeddedServletContainerFactory servletContainer() {
        TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory();
        tomcat.setPort(port);
        //tomcat.addAdditionalTomcatConnectors(createSslConnector());
        return tomcat;
    }
}
