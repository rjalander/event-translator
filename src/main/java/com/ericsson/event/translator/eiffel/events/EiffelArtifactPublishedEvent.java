package com.ericsson.event.translator.eiffel.events;

import com.ericsson.event.translator.eiffel.models.EiffelArtifactPublishedEventParams;
import com.ericsson.event.translator.eiffel.models.EiffelEventMsgParams;

public class EiffelArtifactPublishedEvent {
    public EiffelArtifactPublishedEvent() {
    }

    private EiffelEventMsgParams msgParams = new EiffelEventMsgParams();
    private EiffelArtifactPublishedEventParams eventParams = new EiffelArtifactPublishedEventParams();

    public EiffelArtifactPublishedEventParams getEventParams() {
        return eventParams;
    }

    public void setEventParams(EiffelArtifactPublishedEventParams eventParams) {
        this.eventParams = eventParams;
    }

    public EiffelEventMsgParams getMsgParams() {
        return msgParams;
    }

    public void setMsgParams(EiffelEventMsgParams msgParams) {
        this.msgParams = msgParams;
    }

    @Override
    public String toString() {
        return "EiffelArtifactPublishedEvent{" +
                "msgParams=" + msgParams +
                ", eventParams=" + eventParams +
                '}';
    }
}
