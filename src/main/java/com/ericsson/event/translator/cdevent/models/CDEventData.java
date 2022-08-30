package com.ericsson.event.translator.cdevent.models;

public class CDEventData {

    private String eventId;
    private String eventName;
    private String contextId;
    private String triggerId;
    private String subject;

    private String artifactId;

    private String artifactName;

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getContextId() {
        return contextId;
    }

    public void setContextId(String contextId) {
        this.contextId = contextId;
    }

    public String getTriggerId() {
        return triggerId;
    }

    public void setTriggerId(String triggerId) {
        this.triggerId = triggerId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getArtifactName() {
        return artifactName;
    }

    public void setArtifactName(String artifactName) {
        this.artifactName = artifactName;
    }

    @Override
    public String toString() {
        return "CDEventData{" +
                "eventId='" + eventId + '\'' +
                ", eventName='" + eventName + '\'' +
                ", contextId='" + contextId + '\'' +
                ", triggerId='" + triggerId + '\'' +
                ", subject='" + subject + '\'' +
                ", artifactId='" + artifactId + '\'' +
                ", artifactName='" + artifactName + '\'' +
                '}';
    }
}
