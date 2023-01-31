package com.ericsson.event.translator.rabbitmq;

import com.ericsson.event.translator.cdevent.CDEventsTranslator;
import com.ericsson.eiffel.semantics.events.EiffelActivityFinishedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

@Slf4j
@Component
public class RabbitMQMsgReceiver {

    @Autowired
    private CDEventsTranslator cdEventTranslator;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${cloudevent.broker.url}")
    private String CLOUDEVENT_BROKER_URL;

    public void receiveMessage(byte[] message) {
        String eventJson =
                new String(message, StandardCharsets.UTF_8);
        boolean isPublished = false;
        log.info("RabbitMQMsgReceiver Received message from RabbitMQ <" + eventJson + ">");
        try {
            JsonNode jsonNode = objectMapper.readTree(eventJson);
            JsonNode metaObj = jsonNode.get("meta");
            String eiffelEventType = metaObj.get("type").asText();
            log.info("Eiffel event {} received", eiffelEventType);

            ArrayNode tagsArrayNode = (ArrayNode) metaObj.get("tags");
            for (Iterator<JsonNode> iterator = tagsArrayNode.elements(); iterator.hasNext(); ) {
                String tag = iterator.next().asText();
                log.info("Eiffel event meta tag {}" +tag);
                if(tag.equalsIgnoreCase("published")){
                    isPublished = true;
                    break;
                }
            }
            if(!isPublished){
                log.info("The Eiffel event {} will be translated to CDEvent and published to configured events-broker {}", eiffelEventType, CLOUDEVENT_BROKER_URL);
                cdEventTranslator.translateToCDEvent(eventJson, eiffelEventType);
            } else{
                log.info("Ignoring, the Eiffel event {} is published by event-translator itself..", eiffelEventType);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
