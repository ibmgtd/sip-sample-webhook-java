/*
   * Licensed Materials - Property of IBM
   * (C) Copyright IBM Corp. 2017. All Rights Reserved.
   * US Government Users Restricted Rights - Use, duplication or
   * disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
*/

package com.ibm.sip.sample.webhook.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ApplicationConfiguration {
    /*
     * secret: The shared secret that is used to calculate the HMAC signature to verify the signature that is passed on the subscription in the header.
     *
     * The shared secret and the HMAC signature is documented in the subscription REST API with swagger and can be viewed at
     * [http://sip-subscription-integration.mybluemix.net/swagger-ui.html].
     *
    */
    @Value("${sip.webhook.secret}")
    private String secret;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}
