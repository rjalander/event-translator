package com.ericsson.event.translator.eiffel;


import com.ericsson.eiffel.semantics.events.*;
import com.ericsson.event.translator.Constants;
import com.ericsson.event.translator.eiffel.events.EiffelActivityFinishedEvent;
import com.ericsson.event.translator.eiffel.events.EiffelArtifactCreatedEvent;
import com.ericsson.event.translator.eiffel.events.EiffelArtifactPublishedEvent;
import com.ericsson.event.translator.eiffel.events.EiffelSourceChangeCreatedEvent;
import com.ericsson.event.translator.eiffel.events.EiffelSourceChangeSubmittedEvent;
import com.ericsson.event.translator.eiffel.events.EiffelTestCaseStartedEvent;
import com.ericsson.event.translator.rabbitmq.RabbitMQMsgSender;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.cdevents.CDEventEnums;
import io.cloudevents.CloudEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import static com.ericsson.event.translator.Constants.CDEVENT_TO_EIFFEL;

@Slf4j
@Component
public class EiffelEventsTranslator {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RestTemplate resetTemplate;

    @Autowired
    private RabbitMQMsgSender rabbitMQMsgSender;

    @Value("${remrem.publish.url}")
    private String REMREM_PUBLISH_URL;

    public boolean translateToEiffelEvent(CloudEvent cdevent) {
        String cdeventType = cdevent.getType();
        String eiffelEventType = CDEVENT_TO_EIFFEL.get(cdeventType);
        log.info("Translating from CDEvent {} to EiffelEvent {}", cdeventType, eiffelEventType);
        String eiffelEventJson = buildEiffelEventJson(cdevent, cdeventType);
        if (StringUtils.isEmpty(eiffelEventJson)){
            log.error("Error translating to EiffelEvent from CDEvent type {} ", cdeventType);
            return false;
        }
        //rabbitMQMsgSender.send(eiffelEventJson);
        HttpStatus response = sendEiffelEventToRemRemPublish(eiffelEventJson, eiffelEventType);
        if (response == HttpStatus.OK) {
            log.info("Eiffel event {} is published to RemRem Publish URL - {}", eiffelEventType, REMREM_PUBLISH_URL);
        }else{
            log.error("Error sending Eiffel event to REMReM Publish response is {} ", response);
            return false;
        }
        return true;
    }


    public HttpStatus sendEiffelEventToRemRemPublish(String eiffelEventJson, String eiffelEventType) {
        String remRemPublishPostUrl = REMREM_PUBLISH_URL+"?mp=eiffelsemantics&msgType="+eiffelEventType;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(eiffelEventJson, headers);
        ResponseEntity<String> response = resetTemplate.postForEntity(remRemPublishPostUrl, entity, String.class);

        return response.getStatusCode();
    }

    private String buildEiffelEventJson(CloudEvent cdevent, String cdeventType) {
        String eiffelEventJson = "";
        if (cdeventType.equals(Constants.CDEVENTS_ART_PUBLISHED)) {
            eiffelEventJson = buildEiffelArtifactPublishedEvent(cdevent);
        } else if(cdeventType.equals(Constants.CDEVENTS_ART_CREATED)){
            eiffelEventJson = buildEiffelArtifactCreatedEvent(cdevent);
        } else if(cdeventType.equals(Constants.CDEVENTS_PIPELINERUN_FINISHED)){
            eiffelEventJson = buildEiffelActivityFinishedEvent(cdevent);
        } else if(cdeventType.equals(CDEventEnums.ChangeCreatedEventV1.getEventType())){
            eiffelEventJson = buildEiffelSourceChangeCreatedEvent(cdevent);
        } else if(cdeventType.equals(CDEventEnums.ChangeMergedEventV1.getEventType())){
            eiffelEventJson = buildEiffelSourceChangeSubmittedEvent(cdevent);
        } else if (cdeventType.equals(CDEventEnums.TestCaseStartedEventV1.getEventType())) {
            eiffelEventJson = buildEiffelTestCaseStartedEvent(cdevent);
        }
        return eiffelEventJson;
    }

    private String buildEiffelActivityFinishedEvent(CloudEvent cdevent) {
        String eiffelActFEventJson = "";
        try {
            EiffelActivityFinishedEvent eiffelActFEvent = new EiffelActivityFinishedEvent();
            eiffelActFEvent.getMsgParams().getMeta().setType(EiffelActivityFinishedEventMeta.Type.EIFFEL_ACTIVITY_FINISHED_EVENT.value());
            eiffelActFEvent.getMsgParams().getMeta().setVersion(EiffelActivityFinishedEventMeta.Version._3_0_0.value());
            //To know the event is published by this translator
            eiffelActFEvent.getMsgParams().getMeta().getTags().add("Published");

            eiffelActFEvent.getEventParams().getLinks().add(getLinkParams("ACTIVITY_EXECUTION", cdevent.getId()));

            EiffelActivityFinishedEventOutcome outcome = new EiffelActivityFinishedEventOutcome();
            outcome.setConclusion(EiffelActivityFinishedEventOutcome.Conclusion.SUCCESSFUL);
            outcome.setDescription("Activity execution is success");
            eiffelActFEvent.getEventParams().getData().setOutcome(outcome);

            eiffelActFEventJson = objectMapper.writeValueAsString(eiffelActFEvent);
            log.info("Updated eiffelArtPEventJson - {}", eiffelActFEventJson);

        } catch (Exception e) {
            log.error("Exception occurred while building EiffelActivityFinishedEvent from CDEvent {} ", e.getMessage());
        }

        return eiffelActFEventJson;
    }

    private String buildEiffelTestCaseStartedEvent(CloudEvent inputEvent) {
        String eiffelTCSEventJson = "";
        try {
            EiffelTestCaseStartedEvent eiffelTestCaseStartedEvent = new EiffelTestCaseStartedEvent();
            eiffelTestCaseStartedEvent.getMsgParams().getMeta().setType(EiffelTestCaseStartedEventMeta.Type.EIFFEL_TEST_CASE_STARTED_EVENT.value());
            eiffelTestCaseStartedEvent.getMsgParams().getMeta().setVersion(EiffelTestCaseStartedEventMeta.Version._3_0_0.value());

            eiffelTestCaseStartedEvent.getEventParams().getLinks().add(getLinkParams("CAUSE", inputEvent.getId()));

            if (inputEvent.getExtension("id") != null){
                String id = inputEvent.getExtension("id").toString();
                log.info("Received test id from event {} ", id);
                ///?????? Where should eventID goes into Eiffel test case created event
                // eiffelTestCaseStartedEvent.getMsgParams().getMeta().getSource().setUri(id);
            }

            if (inputEvent.getExtension("source") != null){
                String artifactSource = inputEvent.getExtension("source").toString();
                log.info("Received data source from event {} ", artifactSource);
                eiffelTestCaseStartedEvent.getMsgParams().getMeta().getSource().setUri(artifactSource);
            }
            eiffelTCSEventJson = objectMapper.writeValueAsString(eiffelTestCaseStartedEvent);
            log.info("Updated eiffelArtPEventJson - {}", eiffelTCSEventJson);

        } catch (Exception e) {
            log.error("Exception occurred while building EiffelTestCaseStartedEvent from CDEvent {} ", e.getMessage());
        }
        return eiffelTCSEventJson;
    }

    private String buildEiffelSourceChangeSubmittedEvent(CloudEvent inputEvent) {
        String eiffelSCSEventJson = "";
        try {
            EiffelSourceChangeSubmittedEvent eiffelSourceChangeSubmittedEvent = new EiffelSourceChangeSubmittedEvent();
            eiffelSourceChangeSubmittedEvent.getMsgParams().getMeta().setType(EiffelSourceChangeSubmittedEventMeta.Type.EIFFEL_SOURCE_CHANGE_SUBMITTED_EVENT.value());
            eiffelSourceChangeSubmittedEvent.getMsgParams().getMeta().setVersion(EiffelSourceChangeSubmittedEventMeta.Version._3_0_0.value());

            eiffelSourceChangeSubmittedEvent.getEventParams().getLinks().add(getLinkParams("CAUSE", inputEvent.getId()));

            if (inputEvent.getExtension("source") != null){
                String artifactSource = inputEvent.getExtension("source").toString();
                log.info("Received data source from event {} ", artifactSource);
                eiffelSourceChangeSubmittedEvent.getMsgParams().getMeta().getSource().setUri(artifactSource);
            }
            //Repository repository = inputEvent.getExtension("repository");
            eiffelSourceChangeSubmittedEvent.getEventParams().getData().getGitIdentifier().setCommitId("repository.getId()");
            eiffelSourceChangeSubmittedEvent.getEventParams().getData().getGitIdentifier().setRepoName("repository.getName()");
            eiffelSourceChangeSubmittedEvent.getEventParams().getData().getGitIdentifier().setRepoUri("repository.getUrl()");

            eiffelSCSEventJson = objectMapper.writeValueAsString(eiffelSourceChangeSubmittedEvent);
            log.info("Updated eiffelArtPEventJson - {}", eiffelSCSEventJson);
        }catch (Exception e) {
            log.error("Exception occurred while building EiffelSourceChangeSubmittedEvent from CDEvent {} ", e.getMessage());
        }
        return eiffelSCSEventJson;
    }

    private String buildEiffelSourceChangeCreatedEvent(CloudEvent inputEvent) {
        String eiffelSCCEventJson = "";
        try {
            EiffelSourceChangeCreatedEvent eiffelSourceChangeCreatedEvent = new EiffelSourceChangeCreatedEvent();
            eiffelSourceChangeCreatedEvent.getMsgParams().getMeta().setType(EiffelSourceChangeCreatedEventMeta.Type.EIFFEL_SOURCE_CHANGE_CREATED_EVENT.value());
            eiffelSourceChangeCreatedEvent.getMsgParams().getMeta().setVersion(EiffelSourceChangeCreatedEventMeta.Version._4_0_0.value());

            eiffelSourceChangeCreatedEvent.getEventParams().getLinks().add(getLinkParams("CAUSE", inputEvent.getId()));

            if (inputEvent.getExtension("source") != null){
                String artifactSource = inputEvent.getExtension("source").toString();
                log.info("Received data source from event {} ", artifactSource);
                eiffelSourceChangeCreatedEvent.getMsgParams().getMeta().getSource().setUri(artifactSource);
            }
            //Repository repository = inputEvent.getExtension("repository");
            eiffelSourceChangeCreatedEvent.getEventParams().getData().getGitIdentifier().setCommitId("repository.getId()");
            eiffelSourceChangeCreatedEvent.getEventParams().getData().getGitIdentifier().setRepoName("repository.getName()");
            eiffelSourceChangeCreatedEvent.getEventParams().getData().getGitIdentifier().setRepoUri("repository.getUrl()");

            eiffelSCCEventJson = objectMapper.writeValueAsString(eiffelSourceChangeCreatedEvent);
            log.info("Updated eiffelArtPEventJson - {}", eiffelSCCEventJson);
        }catch (Exception e) {
            log.error("Exception occurred while building EiffelSourceChangeCreatedEvent from CDEvent {} ", e.getMessage());
        }
        return eiffelSCCEventJson;
    }

    public String buildEiffelArtifactCreatedEvent(CloudEvent inputEvent) {
        String eiffelArtCEventJson = "";
        try {
            EiffelArtifactCreatedEvent eiffelArtifactCreatedEvent = new EiffelArtifactCreatedEvent();

            eiffelArtifactCreatedEvent.getMsgParams().getMeta().setType(EiffelArtifactCreatedEventMeta.Type.EIFFEL_ARTIFACT_CREATED_EVENT.value());
            eiffelArtifactCreatedEvent.getMsgParams().getMeta().setVersion(EiffelArtifactCreatedEventMeta.Version._3_0_0.value());

            eiffelArtifactCreatedEvent.getEventParams().getLinks().add(getLinkParams("CAUSE", inputEvent.getId()));

            if (inputEvent.getExtension("source") != null){
                String artifactSource = inputEvent.getExtension("source").toString();
                log.info("Received data source from event {} ", artifactSource);
                eiffelArtifactCreatedEvent.getMsgParams().getMeta().getSource().setUri(artifactSource);
            }

            if (inputEvent.getExtension("id") != null){
                String artifactIdentity = inputEvent.getExtension("id").toString();
                log.info("Received data identity from event {} ", artifactIdentity);
                eiffelArtifactCreatedEvent.getEventParams().getData().setIdentity(artifactIdentity);
            }

            eiffelArtCEventJson = objectMapper.writeValueAsString(eiffelArtifactCreatedEvent);
            log.info("Updated eiffelArtPEventJson - {}", eiffelArtCEventJson);
        } catch (Exception e) {
            log.error("Exception occurred while building EiffelArtifactCreatedEvent from CDEvent {} ", e.getMessage());
        }

        return eiffelArtCEventJson;
    }

    private Link getLinkParams(String linkType, String target) {
        Link link = new Link();
        link.setType(linkType);
        link.setTarget(target);
        return link;
    }

    public String buildEiffelArtifactPublishedEvent(CloudEvent inputEvent) {
        String eiffelArtPEventJson = "";

        try {
            EiffelArtifactPublishedEvent eiffelArtifactPublishedEvent = new EiffelArtifactPublishedEvent();

            eiffelArtifactPublishedEvent.getMsgParams().getMeta().setType(EiffelArtifactPublishedEventMeta.Type.EIFFEL_ARTIFACT_PUBLISHED_EVENT.value());
            eiffelArtifactPublishedEvent.getMsgParams().getMeta().setVersion(EiffelArtifactPublishedEventMeta.Version._3_1_0.value());

            eiffelArtifactPublishedEvent.getEventParams().getLinks().add(getLinkParams("ARTIFACT", inputEvent.getId()));

            if (inputEvent.getExtension("source") != null){
                String artifactSource = inputEvent.getExtension("source").toString();
                log.info("Received data source from event {} ", artifactSource);
                eiffelArtifactPublishedEvent.getMsgParams().getMeta().getSource().setUri(artifactSource);
            }

            if (inputEvent.getExtension("id") != null){
                Location location = new Location();
                location.setUri(inputEvent.getExtension("id").toString());
                location.setType(Location.Type.OTHER);
                eiffelArtifactPublishedEvent.getEventParams().getData().getLocations().add(location);
            }
            eiffelArtPEventJson = objectMapper.writeValueAsString(eiffelArtifactPublishedEvent);
            log.info("Updated eiffelArtPEventJson - {}", eiffelArtPEventJson);
        } catch (Exception e) {
            log.error("Exception occurred while building EiffelArtifactPublishedEvent from CDEvent {} ", e.getMessage());
        }
        return eiffelArtPEventJson;
    }
}
