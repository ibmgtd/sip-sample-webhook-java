/*
   * Licensed Materials - Property of IBM
   * (C) Copyright IBM Corp. 2017. All Rights Reserved.
   * US Government Users Restricted Rights - Use, duplication or
   * disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */

package com.ibm.sip.sample.webhook.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.sip.sample.webhook.config.ApplicationConfiguration;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

@Api(description = "Sample webhook entry for event data")
@RestController
@RequestMapping(path = "/events/")
public class EventController {
    @Autowired
    private ApplicationConfiguration applicationConfiguration;

    //The shared secret that is used to calculate the matching signatures
    private boolean checkSharedSecretConfig = true;
    private String sharedSecret = null;

    //List of events received by this webhook
    private List<Object> eventsReceived = new ArrayList<Object>();

    private ObjectMapper objectMapper = new ObjectMapper();

    public EventController() {
    }

    private void init(){

        if(applicationConfiguration.getSecret() != null){
            sharedSecret = applicationConfiguration.getSecret();
        }
        checkSharedSecretConfig = false;
    }

    /*
     * Display a list of the events received by this webhook
     */
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @ApiOperation(tags = {"Events"}, value = "Get all published events", notes = "Retrieve all published events")
    public ResponseEntity<?> getEvents() {

        System.out.println(" Get events :"+eventsReceived.size());

        return new ResponseEntity<List<Object>>(eventsReceived, HttpStatus.OK);
    }

    /*
     * Receive Shipping Information Pipeline (SIP) events.
     * If the shared secret is configured, use it to verify the HMAC signature.
     * As a convenience, store them in a temporary array to be retrieved.
     */
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes =
            MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    @ApiOperation(tags = {"Events"}, value = "publish event", notes = "Publish event")
    public ResponseEntity<?> postEvents(@ApiParam(name = "event", value = "Body contains the details of the event")
                                            @RequestBody String event,
                                            @RequestHeader(value = "X-GTD-Signature", required = false) String signature) {

        //make sure the init to set the webhook secret has run at least once.
        if(checkSharedSecretConfig) init();

        //print out the signature and the event received
        System.out.println("header X-GTD-Signature: " + signature + ", Received event: " + event);
        //validate the signature and the event data
        boolean isvalid = verifySignature(signature, event);

        //if not valid, return a forbidden
        if (!isvalid) {
            System.out.println("Return http status of FORBIDDEN received event: " + event);
            return new ResponseEntity<String>(HttpStatus.FORBIDDEN);
        }

        try {
            List<Object> jsonArray = objectMapper.readValue(event, List.class);
            for(int i = 0;i<jsonArray.size();i++){
                Object o = jsonArray.get(i);
                eventsReceived.add(o);

            }

            //if valid, store the event and return an ok status
            System.out.println("Return http status of OK received event: " + event);
            return new ResponseEntity<String>(HttpStatus.OK);
        } catch (Throwable e) {
            //if valid, store the event and return an ok status
            return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /*
     * Verify the signature and the event data.
     *
     * The shared secret and the HMAC signature is documented in the subscription REST API with swagger and can be viewed at
     * [http://sip-subscription-integration.mybluemix.net/swagger-ui.html].
     */
    protected boolean verifySignature(String sentSignature, String event){
        boolean isValid = false;

        try {

            if(StringUtils.isEmpty(sharedSecret)) { // If shared secret is not set, receive all events
                return true;
            } else if (sentSignature == null){    //If shared secret is set, must have a signature on all events received
                return false;
            } else {                    //If shared secret is set and a signature is passed, validate that they match
                event = event.trim();

                System.out.println(" verifySignature sharedSecret: "+sharedSecret +
                        ", event string length: "+event.length()+
                        ", event string: "+event);

                //Get a HmacSHA1 instance
                Mac hmac = Mac.getInstance("HmacSHA1");
                //Initialize with the shared secret key
                hmac.init(new SecretKeySpec(sharedSecret.getBytes( Charset.forName("UTF-8")), "HmacSHA1"));
                //calculate the signature using the event data
                String calculatedSignature = Hex.encodeHexString(hmac.doFinal(event.getBytes(Charset.forName("UTF-8"))));
                System.out.println("calculatedSignature: "+calculatedSignature);

                //The received signature and the calculated signatures must be equal to validate that the data is accureate.
                if(sentSignature.equals(calculatedSignature)){
                    isValid = true;
                } else {
                    System.out.println("isValid = false, sentSignature: "+sentSignature+", calculatedSignature: "+calculatedSignature);
                }
            }
        } catch (Exception e) {
            System.out.println(" verifySignature had the following error:");
            e.printStackTrace();
        }
        return isValid;
    }

}
