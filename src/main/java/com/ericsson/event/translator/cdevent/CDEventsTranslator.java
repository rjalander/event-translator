package com.ericsson.event.translator.cdevent;

import com.ericsson.event.translator.cdevent.models.CDEventData;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import dev.cdevents.CDEventEnums;
import dev.cdevents.CDEventTypes;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.message.MessageWriter;
import io.cloudevents.http.HttpMessageFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class CDEventsTranslator {
    @Autowired
    private ObjectMapper objectMapper;

    public void translateToCDEvent(String eiffelEventJson) throws IOException {
        String artifactName = "";
        String artifactId = "";
        String contextId = "";
        String triggerId = "";

        JsonNode jsonNode = objectMapper.readTree(eiffelEventJson);
        JsonNode metaList = jsonNode.get("mydata").get(0);
        String eiffelEvent = metaList.get("fullaggregation").textValue();
        JsonNode eiffelEventNode = objectMapper.readTree(eiffelEvent);
        JsonNode metaParams = eiffelEventNode.get("meta");
        String eventName = metaParams.get("type").asText();
        log.info("RJR eventName received {} ", eventName);
        String eventId = metaParams.get("id").asText();
        log.info("RJR eventId received {} ", eventId);
        JsonNode dataParams = eiffelEventNode.get("data");

        Map<String,String> customDataMap = new HashMap<>();
        JsonNode customDataParams = dataParams.get("customData");
        customDataParams.elements().forEachRemaining((data) -> {
            if (data.get("key").asText().equalsIgnoreCase("artifactid")){
                customDataMap.put("artifactId", data.get("value").asText());
            }else if(data.get("key").asText().equalsIgnoreCase("artifactname")){
                customDataMap.put("artifactName", data.get("value").asText());
            }
        });

        if (!customDataMap.isEmpty()){
            artifactId = customDataMap.get("artifactId");
            artifactName = customDataMap.get("artifactName");
            System.out.println("customDataMap artifactName---> " + artifactName);
            System.out.println("customDataMap artifactId---> " + artifactId);
        }

        if(eventName.equalsIgnoreCase("EiffelArtifactDeployedEvent")){
            createServiceDeployedEvent(eventId, eventName, contextId, triggerId);
        }else if (eventName.equalsIgnoreCase("EiffelTestSuiteStartedEvent")){
            createTestSuiteStartedEvent(contextId, triggerId);
        }else if (eventName.equalsIgnoreCase("EiffelTestSuiteFinishedEvent")){
            createTestSuiteFinishedEvent(eventId, eventName, contextId, triggerId, artifactName, artifactId);
        }

        JsonNode linkParams = eiffelEventNode.get("links");
        String templateName = eiffelEventNode.get("TemplateName").asText();
    }

    @Value("${cloudevent.broker.url}")
    private String CLOUDEVENT_BROKER_URL;
    public void createServiceDeployedEvent(String eventId, String eventName, String contextId, String triggerId)
            throws IOException {
        log.info("Create ServiceDeployed event and send to knative events-broker URL - {}", CLOUDEVENT_BROKER_URL);
        CDEventData data = new CDEventData();
        data.setEventId(eventId);
        data.setEventName(eventName);
        data.setContextId(contextId);
        data.setTriggerId(triggerId);
        data.setSubject("poc");
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        CloudEvent cloudEvent = CDEventTypes.createServiceEvent(CDEventEnums.ServiceDeployedEventV1.getEventType(), "serviceId", "poc", "3.0.0", objectMapper.writeValueAsString(data));
        sendCloudEvent(cloudEvent);
        log.info("cloudEvent service deployed Data {} ", cloudEvent.getData());
        log.info("ServiceDeployed event sent to events-broker URL - {}", CLOUDEVENT_BROKER_URL);
    }

    public void createTestSuiteStartedEvent(String contextId, String triggerId)
            throws IOException {
        log.info("Create TestSuiteStarted event and send to knative events-broker URL - {}", CLOUDEVENT_BROKER_URL);
        CDEventData data = new CDEventData();
        data.setEventId("");
        data.setEventName("");
        data.setContextId(contextId);
        data.setTriggerId(triggerId);
        data.setSubject("poc");
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        CloudEvent cloudEvent = CDEventTypes.createTestEvent(CDEventEnums.TestSuiteStartedEventV1.getEventType(), "testSuiteId", "poc", "3.0.0", objectMapper.writeValueAsString(data));
        sendCloudEvent(cloudEvent);
        log.info("cloudEvent testSuite started Data {} ", cloudEvent.getData());
        log.info("TestSuiteStarted event sent to events-broker URL - {}", CLOUDEVENT_BROKER_URL);
    }

    public void createTestSuiteFinishedEvent(String eventId, String eventName, String contextId, String triggerId, String artifactName, String artifactId)
            throws IOException {
        log.info("Create TestSuiteFinished event and send to knative events-broker URL - {}", CLOUDEVENT_BROKER_URL);
        CDEventData data = new CDEventData();
        data.setEventId(eventId);
        data.setEventName(eventName);
        data.setContextId(contextId);
        data.setTriggerId(triggerId);
        data.setSubject("cdevent_poc");
        data.setArtifactName(artifactName);
        data.setArtifactId(artifactId);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        CloudEvent cloudEvent = CDEventTypes.createTestEvent(CDEventEnums.TestSuiteFinishedEventV1.getEventType(), "testSuiteId", "poc", "3.0.0", objectMapper.writeValueAsString(data));
        sendCloudEvent(cloudEvent);
        log.info("cloudEvent testSuite finished Data {} ", cloudEvent.getData());
        log.info("TestSuiteFinished event sent to events-broker URL - {}", CLOUDEVENT_BROKER_URL);
    }

    public void sendCloudEvent(CloudEvent ceToSend) throws IOException {
        URL url = new URL(CLOUDEVENT_BROKER_URL);
        HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();
        httpUrlConnection.setRequestMethod("POST");
        httpUrlConnection.setDoOutput(true);
        httpUrlConnection.setDoInput(true);
        MessageWriter messageWriter = createMessageWriter(httpUrlConnection);
        messageWriter.writeBinary(ceToSend);

        if (httpUrlConnection.getResponseCode() / 100 != 2) {
            throw new RuntimeException(
                    "Failed : HTTP error code : " + httpUrlConnection.getResponseCode());
        }
    }

    private MessageWriter createMessageWriter(HttpURLConnection httpUrlConnection) {
        return HttpMessageFactory.createWriter(
                httpUrlConnection::setRequestProperty,
                body -> {
                    try {
                        if (body != null) {
                            httpUrlConnection.setRequestProperty("content-length", String.valueOf(body.length));
                            try (OutputStream outputStream = httpUrlConnection.getOutputStream()) {
                                outputStream.write(body);
                            }
                        } else {
                            httpUrlConnection.setRequestProperty("content-length", "0");
                        }
                    } catch (IOException t) {
                        throw new UncheckedIOException(t);
                    }
                });
    }


}
