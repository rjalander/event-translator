package com.ericsson.event.translator;

import com.ericsson.eiffel.semantics.events.Link;
import com.ericsson.eiffel.semantics.events.Location;
import com.ericsson.event.translator.cdevent.CDEventCreator;
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

@RestController
@Slf4j
@RequestMapping(value = "/translate")
public class EventTranslatorController {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    CDEventCreator cdEventCreator;

    private static final String CD_ARTIFACT_PUBLISHED_EVENT_TYPE = "cd.artifact.published.v1";
    private RestTemplate resetTemplate = new RestTemplate();

    @RequestMapping("/")
    public String hello() {
        return "Hello EventTranslatorController";
    }

    @RequestMapping(value = "/eiffel", method = RequestMethod.POST)
    public ResponseEntity<Void> translateToEiffelEvent(@RequestBody CloudEvent inputEvent) {

        String eiffelArtPEventJson = "";
        //1. get the structure of the eiffel event that needs to convert from CDEvent -- EiffelEventBean.java
        //2. Fill in all the required details from CloudEvent and build the Eiffel event (as event.json)
        //3. curl -H "Content-Type: application/json" -X POST --data @event.json  "http://eiffel-remrem-publish:8080/generateAndPublish?mp=eiffelsemantics&msgType=EiffelArtifactPublishedEvent"
        if (inputEvent.getType().equals(CD_ARTIFACT_PUBLISHED_EVENT_TYPE)) {
            log.info("RJR Received Event with type - {} ", CD_ARTIFACT_PUBLISHED_EVENT_TYPE);
            EiffelArtifactPublishedEvent eiffelArtifactPublishedEvent = new EiffelArtifactPublishedEvent();

            //eiffelArtifactPublishedEvent.getMsgParams().getMeta().setId(UUID.randomUUID().toString());
            eiffelArtifactPublishedEvent.getMsgParams().getMeta().setType("EiffelArtifactPublishedEvent");
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
            log.info("RJR Updated eiffelArtPEventJson - {}", eiffelArtPEventJson);
            String remRemPublishPostUrl = "http://10.1.0.106:8096/generateAndPublish?mp=eiffelsemantics&msgType=EiffelArtifactPublishedEvent";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(eiffelArtPEventJson, headers);
            ResponseEntity<String> response = resetTemplate.postForEntity(remRemPublishPostUrl, entity, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                log.info("RJR The result from REMReM Publish is: " + response.getStatusCodeValue());
            }

        }

        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/cdevent", method = RequestMethod.POST)
    public ResponseEntity<Void> translateToCDEvent(@RequestBody String eiffelEventJson) {
        //1. get the eiffelEventJson posted by eiffel intelligence on Posting REST API with Subscription to /cdevent
        //2. build cdevent using Java-sdk by using the eiffelEventJson
        //3. send cdevent to knative event broker
        log.info("IN translateToCDEvent received eiffelEventJson {} ", eiffelEventJson);
        try {
            JsonNode jsonNode = objectMapper.readTree(eiffelEventJson);
            JsonNode metaList = jsonNode.get("mydata").get(0);
            String eiffelEvent = metaList.get("fullaggregation").textValue();
            JsonNode eiffelEventNode = objectMapper.readTree(eiffelEvent);
            JsonNode metaParams = eiffelEventNode.get("meta");
            String metaType = metaParams.get("type").asText();
            log.info("RJR metaType received {} ", metaType);
            String metaId = metaParams.get("id").asText();
            log.info("RJR metaId received {} ", metaId);
            if(metaType.equalsIgnoreCase("EiffelArtifactDeployedEvent")){
                cdEventCreator.createServiceDeployedEvent(metaId, metaType, "", "");
            }else if (metaType.equalsIgnoreCase("EiffelTestSuiteStartedEvent")){
                cdEventCreator.createTestSuiteStartedEvent("", "");
            }else if (metaType.equalsIgnoreCase("EiffelTestSuiteFinishedEvent")){
                cdEventCreator.createTestSuiteFinishedEvent("", "");
            }

            JsonNode dataParams = eiffelEventNode.get("data");
            JsonNode linkParams = eiffelEventNode.get("links");
            String templateName = eiffelEventNode.get("TemplateName").asText();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok().build();
    }
}
