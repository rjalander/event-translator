package com.ericsson.event.translator.rabbitmq;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQMsgSender { //Need to use RemRemPublish to send the Eiffel event instead

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private static Logger logger = LogManager.getLogger(RabbitMQMsgSender.class);

    @Value("${rabbitmq.exchange}")
    private String topicExchangeName;

    @Value("${rabbitmq.routingkey}")
    private String routingkey;

    public void send(String message) throws Exception {
        System.out.println("RabbitMQMsgSender Sending message to ... " + topicExchangeName);
        rabbitTemplate.convertAndSend(topicExchangeName, routingkey, message);
        System.out.println(" RabbitMQMsgSender Sent '" + message + "'");
    }
}
