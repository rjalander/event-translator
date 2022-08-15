package com.ericsson.event.translator.cdevent;

import com.ericsson.event.translator.cdevent.models.CDEventData;
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

@Slf4j
@Component
public class CDEventCreator {
    @Autowired
    ObjectMapper objectMapper;
    @Value(
            "${BROKER_SINK:http://broker-ingress.knative-eventing.svc.cluster.local/default/events-broker}")
    private String BROKER_SINK;
    public void createServiceDeployedEvent(String eventId, String eventName, String contextId, String triggerId)
            throws IOException {
        log.info("Create ServiceDeployed event and send to knative events-broker URL - {}", BROKER_SINK);
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
        log.info("ServiceDeployed event sent to events-broker URL - {}", BROKER_SINK);
    }

    public void createTestSuiteStartedEvent(String contextId, String triggerId)
            throws IOException {
        log.info("Create TestSuiteStarted event and send to knative events-broker URL - {}", BROKER_SINK);
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
        log.info("TestSuiteStarted event sent to events-broker URL - {}", BROKER_SINK);
    }

    public void createTestSuiteFinishedEvent(String contextId, String triggerId)
            throws IOException {
        log.info("Create TestSuiteStarted event and send to knative events-broker URL - {}", BROKER_SINK);
        CDEventData data = new CDEventData();
        data.setEventId("");
        data.setEventName("");
        data.setContextId(contextId);
        data.setTriggerId(triggerId);
        data.setSubject("poc");
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        CloudEvent cloudEvent = CDEventTypes.createTestEvent(CDEventEnums.TestSuiteFinishedEventV1.getEventType(), "testSuiteId", "poc", "3.0.0", objectMapper.writeValueAsString(data));
        sendCloudEvent(cloudEvent);
        log.info("cloudEvent testSuite started Data {} ", cloudEvent.getData());
        log.info("TestSuiteStarted event sent to events-broker URL - {}", BROKER_SINK);
    }

    public void sendCloudEvent(CloudEvent ceToSend) throws IOException {
        URL url = new URL(BROKER_SINK);
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
