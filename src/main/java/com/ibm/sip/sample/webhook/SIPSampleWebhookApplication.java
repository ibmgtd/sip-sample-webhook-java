/*
  * Licensed Materials - Property of IBM
  * (C) Copyright IBM Corp. 2017. All Rights Reserved.
  * US Government Users Restricted Rights - Use, duplication or
  * disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
  */

package com.ibm.sip.sample.webhook;

import com.ibm.sip.sample.webhook.config.ApplicationConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;

import javax.annotation.PostConstruct;


@SpringBootApplication
public class SIPSampleWebhookApplication extends SpringBootServletInitializer {
    private static final Logger logger = LoggerFactory.getLogger(SIPSampleWebhookApplication.class);

    @Autowired
    ApplicationConfiguration applicationConfiguration;

    public static void main(String[] args) {
        SpringApplication.run(SIPSampleWebhookApplication.class, args);
    }

    @PostConstruct
    public void displayApplicationDeploymentInformation(){
        logger.info(this.getClass().getSimpleName() + " startup environment information: ");
        if(applicationConfiguration.getSecret() == null){
            logger.info("Shared secret is set to null");
        }else{
            logger.info("Shared secret is set");
        }

    }


}
