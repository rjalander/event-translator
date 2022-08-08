package com.ericsson.event.translator;

import com.ericsson.eiffel.semantics.events.Link;
import com.ericsson.eiffel.semantics.events.Location;
import com.ericsson.event.translator.eiffel.events.EiffelArtifactPublishedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.CloudEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@Slf4j
@RequestMapping(value = "/translate")
public class EventTranslatorController {

    @Autowired
    private ObjectMapper objectMapper;

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
        if (inputEvent.getType().equals(CD_ARTIFACT_PUBLISHED_EVENT_TYPE)) {
            log.info("RJR Received Event with type - {} ", CD_ARTIFACT_PUBLISHED_EVENT_TYPE);
            EiffelArtifactPublishedEvent eiffelArtifactPublishedEvent = new EiffelArtifactPublishedEvent();

            //eiffelArtifactPublishedEvent.getMsgParams().getMeta().setId(UUID.randomUUID().toString());
            eiffelArtifactPublishedEvent.getMsgParams().getMeta().setType("EiffelArtifactPublishedEvent");
            eiffelArtifactPublishedEvent.getMsgParams().getMeta().setVersion("3.0.0");

            Location location = new Location();
            location.setName(inputEvent.getExtension("artifactname").toString());
            location.setType(Location.Type.OTHER);
            location.setUri(inputEvent.getExtension("artifactid").toString());
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
            String remRemPublishPostUrl = "http://eiffel-remrem-publish:8080/generateAndPublish?mp=eiffelsemantics&msgType=EiffelArtifactPublishedEvent";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(eiffelArtPEventJson, headers);
            ResponseEntity<String> response = resetTemplate.postForEntity(remRemPublishPostUrl, entity, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                log.info("RJR The result from REMReM Publish is: " + response.getStatusCodeValue());
            }
            //3. curl -H "Content-Type: application/json" -X POST --data @event.json  "http://eiffel-remrem-publish:8080/generateAndPublish?mp=eiffelsemantics&msgType=EiffelArtifactPublishedEvent"
        }

        return ResponseEntity.ok().build();
    }
}
