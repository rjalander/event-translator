package com.ericsson.event.translator;

import com.ericsson.eiffel.semantics.events.*;
import dev.cdevents.CDEventEnums;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Constants {

    public static final String EIFFEL_ART_CREATED = EiffelArtifactCreatedEventMeta.Type.EIFFEL_ARTIFACT_CREATED_EVENT.value();
    public static final String EIFFEL_ART_PUBLISHED = EiffelArtifactPublishedEventMeta.Type.EIFFEL_ARTIFACT_PUBLISHED_EVENT.value();
    public static final String EIFFEL_ACTIVITY_FINISHED = EiffelActivityFinishedEventMeta.Type.EIFFEL_ACTIVITY_FINISHED_EVENT.value();
    public static final String EIFFEL_TESTCASE_STARTED = EiffelTestCaseStartedEventMeta.Type.EIFFEL_TEST_CASE_STARTED_EVENT.value();
    public static final String EIFFEL_TESTCASE_FINISHED = EiffelTestCaseFinishedEventMeta.Type.EIFFEL_TEST_CASE_FINISHED_EVENT.value();
    public static final String EIFFEL_TESTSUITE_STARTED = EiffelTestSuiteStartedEventMeta.Type.EIFFEL_TEST_SUITE_STARTED_EVENT.value();
    public static final String EIFFEL_TESTSUITE_FINISHED = EiffelTestSuiteFinishedEventMeta.Type.EIFFEL_TEST_SUITE_FINISHED_EVENT.value();
    public static final String EIFFEL_SOURCECHANGE_CREATED = EiffelSourceChangeCreatedEventMeta.Type.EIFFEL_SOURCE_CHANGE_CREATED_EVENT.value();
    public static final String EIFFEL_SOURCECHANGE_SUBMITTED = EiffelSourceChangeSubmittedEventMeta.Type.EIFFEL_SOURCE_CHANGE_SUBMITTED_EVENT.value();

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
