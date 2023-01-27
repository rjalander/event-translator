package com.ericsson.event.translator.rabbitmq;

import com.ericsson.event.translator.cdevent.CDEventsTranslator;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQMsgReceiver {

    private CDEventsTranslator cdEventTranslator;

    public void receiveMessage(String message) {
        System.out.println("CDEventsTranslator Received message from RabbitMQ <" + message + ">");

        //Check If this message is sent by the event-translator itself to avoid circulations between the event-brokers
        //event-translator should have some property added as published before sending it to the RabbitMQ broker

//        try {
//            //if(!message.headers.get('published')) {
//                //send this message only If it is not published by event-translator already
//                // and translate to CDEvent and send to message broker
//                cdEventTranslator.translateToCDEvent(message);
//            //}
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
    }
}
