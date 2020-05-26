package com.pragmaticbitbucket.app.ws;

import com.pragmaticbitbucket.app.ws.security.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// SpringBootServletInitializer is needed for the war files
@SpringBootApplication
public class MobileAppWsApplication /* extends SpringBootServletInitializer */ {

    public static void main(String[] args) {

        SpringApplication.run(MobileAppWsApplication.class, args);
    }

    /* needed for War file generation
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(MobileAppWsApplication.class);
    }
    */


    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SpringApplicationContext SpringApplicationContext() {
        return new SpringApplicationContext();
    }

    @Bean(name="AppProperties")
    public AppProperties getAppProperties() {
        return new AppProperties();
    }
}

