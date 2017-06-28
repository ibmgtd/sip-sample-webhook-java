/*
   * Licensed Materials - Property of IBM
   * (C) Copyright IBM Corp. 2017. All Rights Reserved.
   * US Government Users Restricted Rights - Use, duplication or
   * disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */

package com.ibm.sip.sample.webhook.config;

import com.google.common.base.Predicates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Autowired
    ApplicationConfiguration applicationConfiguration;

    @Bean
    public Docket api() {
            return new Docket(DocumentationType.SWAGGER_2)
                    .useDefaultResponseMessages(false)
                    .select()
                    .apis(RequestHandlerSelectors.any())
                    .paths(Predicates.not(PathSelectors.regex("/error")))
                    .build();

    }
}