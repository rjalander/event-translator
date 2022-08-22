package com.ericsson.event.translator;

import com.ericsson.eiffel.semantics.events.CustomData;
import com.ericsson.eiffel.semantics.events.Link;
import com.ericsson.eiffel.semantics.events.Location;
import com.ericsson.event.translator.cdevent.CDEventCreator;
import com.ericsson.event.translator.eiffel.events.EiffelArtifactCreatedEvent;
import com.ericsson.event.translator.eiffel.events.EiffelArtifactPublishedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.gson.JsonObject;
import dev.cdevents.CDEventEnums;
import dev.cdevents.CDEventTypes;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.message.MessageWriter;
import io.cloudevents.http.HttpMessageFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping(value = "/translate")
public class EventTranslatorController {

    private static final String EIFFEL_ART_PUBLISHED = "EiffelArtifactPublishedEvent";
    private static final String EIFFEL_ART_CREATED = "EiffelArtifactCreatedEvent";
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    CDEventCreator cdEventCreator;

    private RestTemplate resetTemplate = new RestTemplate();

    @RequestMapping("/")
    public String hello() {
        return "Hello EventTranslatorController";
    }

    @RequestMapping(value = "/eiffel", method = RequestMethod.POST)
    public ResponseEntity<Void> translateToEiffelEvent(@RequestBody CloudEvent inputEvent) {
        //1. get the structure of the eiffel event that needs to convert from CDEvent -- EiffelEventBean.java
        //2. Fill in all the required details from CloudEvent and build the Eiffel event (as event.json)
        //3. curl -H "Content-Type: application/json" -X POST --data @event.json  "http://eiffel-remrem-publish:8080/generateAndPublish?mp=eiffelsemantics&msgType=EiffelArtifactPublishedEvent"

        if (inputEvent.getType().equals(CDEventEnums.ArtifactPublishedEventV1.getEventType())) {
            log.info("Received Artifact published CDEvent - {} ", CDEventEnums.ArtifactPublishedEventV1.getEventType());
            String eiffelArtPEventJson = buildEiffelArtifactPublishedEvent(inputEvent);
            if (eiffelArtPEventJson.equals("")){
                log.error("Error translating to eiffelEventJson");
                return ResponseEntity.badRequest().build();
            }
            HttpStatus response = sendEiffelEventToRemRemPublish(eiffelArtPEventJson, EIFFEL_ART_PUBLISHED);
            if (response == HttpStatus.OK) {
                log.info("The result from REMReM Publish is: " + response);
            }else{
                log.error("Error sending Eiffel event to REMReM Publish response {} ", response);
                ResponseEntity.internalServerError().build();
            }
        }else if (inputEvent.getType().equals(CDEventEnums.ArtifactPackagedEventV1.getEventType())) {
            log.info("Received Artifact published CDEvent - {} ", CDEventEnums.ArtifactPackagedEventV1.getEventType());
            String eiffelArtCEventJson = buildEiffelArtifactCreatedEvent(inputEvent);
            if (eiffelArtCEventJson.equals("")){
                log.error("Error translating to eiffelArtCEventJson {} ", EIFFEL_ART_CREATED);
                return ResponseEntity.badRequest().build();
            }
            HttpStatus response = sendEiffelEventToRemRemPublish(eiffelArtCEventJson, EIFFEL_ART_CREATED);
            if (response == HttpStatus.OK) {
                log.info("The result from REMReM Publish is: " + response);
            }else{
                log.error("Error sending Eiffel event to REMReM Publish response is {} ", response);
                ResponseEntity.internalServerError().build();
            }
        }
        return ResponseEntity.ok().build();
    }

    @Value(
            "${REMREM_PUBLISH_URL:http://10.1.0.106:8096/generateAndPublish}")
    private String REMREM_PUBLISH_URL;
    private HttpStatus sendEiffelEventToRemRemPublish(String eiffelEventJson, String eiffelEventType) {
        String remRemPublishPostUrl = REMREM_PUBLISH_URL+"?mp=eiffelsemantics&msgType="+eiffelEventType;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(eiffelEventJson, headers);
        ResponseEntity<String> response = resetTemplate.postForEntity(remRemPublishPostUrl, entity, String.class);

        return response.getStatusCode();
    }

    private String buildEiffelEventJson(CloudEvent inputEvent) {
        String eiffelEventJson = "";
        if (inputEvent.getType().equals(CDEventEnums.ArtifactPublishedEventV1.getEventType())) {
            log.info("Received Artifact published CDEvent - {} ", CDEventEnums.ArtifactPublishedEventV1.getEventType());
            eiffelEventJson = buildEiffelArtifactPublishedEvent(inputEvent);
        } else if(inputEvent.getType().equals(CDEventEnums.ArtifactPackagedEventV1.getEventType())){
            log.info("Received Artifact packaged CDEvent - {} ", CDEventEnums.ArtifactPackagedEventV1.getEventType());
            eiffelEventJson = buildEiffelArtifactCreatedEvent(inputEvent);
        }

        return eiffelEventJson;
    }

    private String buildEiffelArtifactCreatedEvent(CloudEvent inputEvent) {
        String eiffelArtCEventJson = "";
        EiffelArtifactCreatedEvent eiffelArtifactCreatedEvent = new EiffelArtifactCreatedEvent();

        //eiffelArtifactPublishedEvent.getMsgParams().getMeta().setId(UUID.randomUUID().toString());
        eiffelArtifactCreatedEvent.getMsgParams().getMeta().setType(EIFFEL_ART_CREATED);
        eiffelArtifactCreatedEvent.getMsgParams().getMeta().setVersion("3.0.0");

        Link link = new Link();
        link.setType("CAUSE");
        link.setTarget(inputEvent.getId());
        eiffelArtifactCreatedEvent.getEventParams().getLinks().add(link);

        //kind-registry:5000/cdevent/poc@sha256:9f4a3831a7e99ae6c86182eca271c0be07fecf366185b5702e54a407fa788410
        String artifactIdentity = "pkg:github/sig-events/cdevent/poc@sha256:9f4a3831a7e99ae6c86182eca271c0be07fecf366185b5702e54a407fa788410";
        log.info("dummy artifact image artifactIdentity {} ", artifactIdentity);
        if (inputEvent.getExtension("artifactid") != null){
            artifactIdentity = inputEvent.getExtension("artifactid").toString().replace("kind-registry:5000", "pkg:github/sig-events");
            log.info("Received latest artifact image from event {} ", artifactIdentity);
        }
        eiffelArtifactCreatedEvent.getEventParams().getData().setIdentity(artifactIdentity);

        if (inputEvent.getExtension("artifactid") != null){
            CustomData customData = new CustomData();
            customData.setKey("artifactid");
            customData.setValue(inputEvent.getExtension("artifactid"));
            eiffelArtifactCreatedEvent.getEventParams().getData().getCustomData().add(customData);
        }else{
            //Adding dummy values for testing
            CustomData customData = new CustomData();
            customData.setKey("artifactid");
            customData.setValue("kind-registry:5000/cdevent/poc@sha256:9f4a3831a7e99ae6c86182eca271c0be07fecf366185b5702e54a407fa788410");
            eiffelArtifactCreatedEvent.getEventParams().getData().getCustomData().add(customData);
        }
        if (inputEvent.getExtension("artifactname") != null){
            CustomData customData = new CustomData();
            customData.setKey("artifactname");
            customData.setValue(inputEvent.getExtension("artifactname"));
            eiffelArtifactCreatedEvent.getEventParams().getData().getCustomData().add(customData);
        }else{
            //Adding dummy values for testing
            CustomData customData = new CustomData();
            customData.setKey("artifactname");
            customData.setValue("poc");
            eiffelArtifactCreatedEvent.getEventParams().getData().getCustomData().add(customData);
        }

        try {
            eiffelArtCEventJson = objectMapper.writeValueAsString(eiffelArtifactCreatedEvent);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        log.info("Updated eiffelArtCEventJson - {}", eiffelArtCEventJson);

        return eiffelArtCEventJson;
    }

    private String buildEiffelArtifactPublishedEvent(CloudEvent inputEvent) {
        String eiffelArtPEventJson = "";
        EiffelArtifactPublishedEvent eiffelArtifactPublishedEvent = new EiffelArtifactPublishedEvent();

        //eiffelArtifactPublishedEvent.getMsgParams().getMeta().setId(UUID.randomUUID().toString());
        eiffelArtifactPublishedEvent.getMsgParams().getMeta().setType(EIFFEL_ART_PUBLISHED);
        eiffelArtifactPublishedEvent.getMsgParams().getMeta().setVersion("3.0.0");

        Location location = new Location();
        if (inputEvent.getExtension("artifactid") != null){
            location.setUri(inputEvent.getExtension("artifactid").toString());
        }else{
            log.error("CloudEvent extension artifactid is required.");
            location.setUri("dummy_artifactid");
            //return ResponseEntity.badRequest().build();
            //throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "CloudEvent extension artifactid is required");
        }
        if (inputEvent.getExtension("artifactname") != null){
            location.setName(inputEvent.getExtension("artifactname").toString());
        }else{
            log.error("CloudEvent extension artifactname is required.");
            location.setName("dummy_artifactname");
            //return ResponseEntity.badRequest().build();
            //throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "CloudEvent extension artifactname is required");
        }
        location.setType(Location.Type.OTHER);

        eiffelArtifactPublishedEvent.getEventParams().getData().getLocations().add(location);

        Link link = new Link();
        link.setType("ARTIFACT");
        link.setTarget(inputEvent.getId());
        eiffelArtifactPublishedEvent.getEventParams().getLinks().add(link);

        try {
            eiffelArtPEventJson = objectMapper.writeValueAsString(eiffelArtifactPublishedEvent);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        log.info("Updated eiffelArtPEventJson - {}", eiffelArtPEventJson);
        return eiffelArtPEventJson;
    }

    @RequestMapping(value = "/cdevent", method = RequestMethod.POST)
    public ResponseEntity<Void> translateToCDEvent(@RequestBody String eiffelEventJson) {
        //1. get the eiffelEventJson posted by eiffel intelligence on Posting REST API with Subscription to /cdevent
        //2. build cdevent using Java-sdk by using the eiffelEventJson
        //3. send cdevent to knative event broker
        log.info("IN translateToCDEvent received eiffelEventJson {} ", eiffelEventJson);
        String artifactName = "";
        String artifactId = "";
        String contextId = "";
        String triggerId = "";
        try {
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
                System.out.println("customDataMap artifactName---> " +artifactName);
                System.out.println("customDataMap artifactId---> " +artifactId);
            }

            if(eventName.equalsIgnoreCase("EiffelArtifactDeployedEvent")){
                cdEventCreator.createServiceDeployedEvent(eventId, eventName, contextId, triggerId);
            }else if (eventName.equalsIgnoreCase("EiffelTestSuiteStartedEvent")){
                cdEventCreator.createTestSuiteStartedEvent(contextId, triggerId);
            }else if (eventName.equalsIgnoreCase("EiffelTestSuiteFinishedEvent")){
                cdEventCreator.createTestSuiteFinishedEvent(eventId, eventName, contextId, triggerId, artifactName, artifactId);
            }

            JsonNode linkParams = eiffelEventNode.get("links");
            String templateName = eiffelEventNode.get("TemplateName").asText();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok().build();
    }
}
