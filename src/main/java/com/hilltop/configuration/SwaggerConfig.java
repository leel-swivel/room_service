package com.hilltop.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;

@Configuration
@EnableSwagger2
public class SwaggerConfig {


    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(generateAPIInfo())
                .select()
                //Here adding base package to scan controllers. This will scan only controllers inside
                //specific package and include in the swagger documentation
                .apis(RequestHandlerSelectors.basePackage("com.hilltop"))
                .paths(PathSelectors.any())
                .build();
    }

    //Api information
    private ApiInfo generateAPIInfo() {
        return new ApiInfo("Room Service", "Implementing Swagger with SpringBoot Application", "1.0.0",
                "", getContacts(), "", "", new ArrayList<>());
    }

    // Developer Contacts
    private Contact getContacts() {
        return new Contact("Leel K", "", "leel@swivelgroup.com.au");
    }
}
