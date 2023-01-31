package com.ericsson.event.translator.cdevent;

import com.ericsson.eiffel.semantics.events.*;
import com.ericsson.event.translator.Constants;
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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

@Slf4j
@Component
public class CDEventsTranslator {
    @Autowired
    private ObjectMapper objectMapper;

    @Value("${cloudevent.broker.url}")
    private String CLOUDEVENT_BROKER_URL;

    public boolean translateToCDEvent(String eiffelEventJson, String eiffelEventType) throws IOException {
        CloudEvent cloudEvent = buildCDEvent(eiffelEventJson, eiffelEventType);
        if (cloudEvent == null) {
            log.error("Error translating to CDEvent from Eiffel event type {} ", eiffelEventType);
            return false;
        }
        HttpURLConnection httpStatus = sendCloudEvent(cloudEvent);
        if(httpStatus.getResponseCode() == HttpStatus.OK.value()){
            log.info("CDEvent {} is published to events-broker URL - {}", cloudEvent.getType(), CLOUDEVENT_BROKER_URL);
        }else{
            log.error("Error sending CDEvent to events-broker response is {} ", httpStatus.getResponseCode());
            return false;
        }
        return true;
    }

    private CloudEvent buildCDEvent(String eiffelEventJson, String eiffelEventType) throws IOException {
        CloudEvent cloudEvent = null;
        if(eiffelEventType.equalsIgnoreCase(Constants.EIFFEL_TESTSUITE_STARTED)){
            EiffelTestSuiteStartedEvent eiffelTestSuiteStartedEvent = objectMapper.readValue(eiffelEventJson, EiffelTestSuiteStartedEvent.class);
            cloudEvent = createCDTestSuiteStartedEvent(eiffelTestSuiteStartedEvent);
        } else if(eiffelEventType.equalsIgnoreCase(Constants.EIFFEL_TESTSUITE_FINISHED)){
            EiffelTestSuiteFinishedEvent eiffelTestSuiteFinishedEvent = objectMapper.readValue(eiffelEventJson, EiffelTestSuiteFinishedEvent.class);
            cloudEvent = createCDTestSuiteFinishedEvent(eiffelTestSuiteFinishedEvent);
        }
        return cloudEvent;
    }

    private CloudEvent createCDTestSuiteStartedEvent(EiffelTestSuiteStartedEvent eiffelTestSuiteStartedEvent) throws IOException {
        log.info("Create TestSuiteStarted CDEvent from Eiffel event - {}", eiffelTestSuiteStartedEvent.getMeta().getType());
        CDEventData data = new CDEventData();
        data.setEventId("");
        data.setEventName("");
        data.setSubject("poc");
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        CloudEvent cloudEvent = CDEventTypes.createTestEvent(CDEventEnums.TestSuiteStartedEventV1.getEventType(), "testSuiteId", eiffelTestSuiteStartedEvent.getData().getName(), "3.0.0", objectMapper.writeValueAsString(data));
        log.info("CDEvent testSuite started Data {} ", cloudEvent.getData());
        return cloudEvent;
    }

    public CloudEvent createCDTestSuiteFinishedEvent(EiffelTestSuiteFinishedEvent eiffelTestSuiteFinishedEvent)
            throws IOException {
        log.info("Create TestSuiteFinished CDEvent from Eiffel event - {}", eiffelTestSuiteFinishedEvent.getMeta().getType());
        CDEventData cdEventData = new CDEventData();
        eiffelTestSuiteFinishedEvent.getData().getCustomData().forEach(data -> {
            if(data.getKey().equalsIgnoreCase("artifactid")){
                cdEventData.setArtifactName(data.getValue().toString());
            }else if(data.getKey().equalsIgnoreCase("artifactname")){
                cdEventData.setArtifactId(data.getValue().toString());
            }
        });
        cdEventData.setSubject("cdevent_poc");
        log.info("TestSuiteFinished cdEventData is {} ", cdEventData.toString());
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        CloudEvent cloudEvent = CDEventTypes.createTestEvent(CDEventEnums.TestSuiteFinishedEventV1.getEventType(), "testSuiteId", "poc", "3.0.0", objectMapper.writeValueAsString(cdEventData));
        log.info("CDEvent testSuite finished Data {} ", cloudEvent.getData());
        return cloudEvent;
    }

    public HttpURLConnection sendCloudEvent(CloudEvent ceToSend) throws IOException {
        URL url = new URL(CLOUDEVENT_BROKER_URL);
        HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();
        httpUrlConnection.setRequestMethod("POST");
        httpUrlConnection.setDoOutput(true);
        httpUrlConnection.setDoInput(true);
        MessageWriter messageWriter = createMessageWriter(httpUrlConnection);
        messageWriter.writeBinary(ceToSend);

        return httpUrlConnection;
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
