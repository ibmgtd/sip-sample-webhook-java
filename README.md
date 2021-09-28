# TradeLenns SIP Sample Webhook
This is a sample webhook which demonstrates how a user could receive events from TradeLens subscriptions.  This sample utilizes a 
shared secret for added security.

You will need to create a subscription in TradeLens using this webhook.  The subscription
REST API is documented with swagger [here](https://platform.tradelens.com/documentation/swagger/?urls.primaryName=Event%20Subscription%20API).  The shared secret specified during subscription creation
must match the value set in the `config/env.json` file.  The default setting
for `sharedSecret` is `theSharedSecret`

Take a moment to read the subscription implementation notes and to familiarize yourself with its REST APIs.  In particular look 
at the notes regarding the shared secret while looking at the sample webhook's controller 
code for verifying the shared secret signature.

## Running the sample webhook

Prerequisites:
 - install gradle 3.2.1
 - install java 1.6 or higher

Run the following commands:
 - `git clone https://github.com/ibmgtd/sip-sample-webhook-java.git`
 - Inspect and change config in main/resources/application.properties
    - **server.port** - server port
    - **sip.webhook.secret** - shared secret value used in subscriptions
 - Start server `gradle bootRun`
 - Validate running by viewing swagger at [http://localhost:8085/swagger-ui.html](http://localhost:8085/swagger-ui.html)

## Create a Subscription
Create the subscription using [https://platform.tradelens.com/documentation/swagger/?urls.primaryName=Event%20Subscription%20API](https://platform.tradelens.com/documentation/swagger/?urls.primaryName=Event%20Subscription%20API).  Use one of the `POST` APIs listed to create a subscription.

The webhook and shared secret correspond to this sample.

## Send an Event
To send an event that is received by the webhook, a Start Transport Equipment Tracking event will need to be sent first to the platorm.  The REST API is documented with swagger and can be viewed at [https://platform.tradelens.com/documentation/swagger/?urls.primaryName=Event%20Publish%20API](https://platform.tradelens.com/documentation/swagger/?urls.primaryName=Event%20Publish%20API).  Note
that if a country was used in the created subscription, either the export or import country will need to include that country.  If a port was
used, then the route must include the port that was used in the subscription.  For example, if the subscription was set up
for country 'US', either the export or import country must be 'US' for events to flow to the subscription created.

## See the Events
To see the events received by this webhook, perform an HTTP `GET` on [http://localhost:8085/events](http://localhost:8085/events)


