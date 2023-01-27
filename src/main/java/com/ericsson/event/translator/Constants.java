package com.ericsson.event.translator;

import com.ericsson.eiffel.semantics.events.EiffelActivityFinishedEventMeta;
import com.ericsson.eiffel.semantics.events.EiffelArtifactCreatedEventMeta;
import com.ericsson.eiffel.semantics.events.EiffelArtifactPublishedEventMeta;
import dev.cdevents.CDEventEnums;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Constants {

    public static final String EIFFEL_ART_CREATED = EiffelArtifactCreatedEventMeta.Type.EIFFEL_ARTIFACT_CREATED_EVENT.value();
    public static final String EIFFEL_ART_PUBLISHED = EiffelArtifactPublishedEventMeta.Type.EIFFEL_ARTIFACT_PUBLISHED_EVENT.value();
    public static final String EIFFEL_ACTIVITY_FINISHED = EiffelActivityFinishedEventMeta.Type.EIFFEL_ACTIVITY_FINISHED_EVENT.value();
    public static final String CDEVENTS_ART_CREATED = CDEventEnums.ArtifactCreatedEventV1.getEventType();
    public static final String CDEVENTS_ART_PUBLISHED = CDEventEnums.ArtifactPublishedEventV1.getEventType();
    public static final String CDEVENTS_PIPELINERUN_FINISHED = CDEventEnums.PipelineRunFinishedEventV1.getEventType();

    public static final Map<String, String> CDEVENT_TO_EIFFEL;

    static {
        Map<String, String> eventsMap = new HashMap<>();
        eventsMap.put(CDEVENTS_ART_PUBLISHED, EIFFEL_ART_PUBLISHED);
        eventsMap.put(CDEVENTS_ART_CREATED, EIFFEL_ART_CREATED);
        eventsMap.put(CDEVENTS_PIPELINERUN_FINISHED, EIFFEL_ACTIVITY_FINISHED);

        CDEVENT_TO_EIFFEL = Collections.unmodifiableMap(eventsMap);
    }

}
