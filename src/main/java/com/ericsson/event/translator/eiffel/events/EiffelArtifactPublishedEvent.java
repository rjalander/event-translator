package com.ericsson.event.translator.eiffel.events;

import com.ericsson.event.translator.eiffel.models.EiffelArtifactPublishedEventParams;
import com.ericsson.event.translator.eiffel.models.EiffelArtifactPublishedMsgParams;

public class EiffelArtifactPublishedEvent {
    public EiffelArtifactPublishedEvent() {
    }

    private EiffelArtifactPublishedMsgParams msgParams = new EiffelArtifactPublishedMsgParams();
    private EiffelArtifactPublishedEventParams eventParams = new EiffelArtifactPublishedEventParams();

    public EiffelArtifactPublishedEventParams getEventParams() {
        return eventParams;
    }

    public void setEventParams(EiffelArtifactPublishedEventParams eventParams) {
        this.eventParams = eventParams;
    }

    public EiffelArtifactPublishedMsgParams getMsgParams() {
        return msgParams;
    }

    public void setMsgParams(EiffelArtifactPublishedMsgParams msgParams) {
        this.msgParams = msgParams;
    }

    @Override
    public String toString() {
        return "EiffelArtifactPublishedEvent{" +
                "eventParams=" + eventParams +
                "} " + super.toString();
    }
}
