package com.ericsson.event.translator.eiffel.events;

import com.ericsson.event.translator.eiffel.models.EiffelArtifactCreatedEventParams;
import com.ericsson.event.translator.eiffel.models.EiffelEventMsgParams;

public class EiffelArtifactCreatedEvent {
    public EiffelArtifactCreatedEvent() {
    }

    private EiffelEventMsgParams msgParams = new EiffelEventMsgParams();
    private EiffelArtifactCreatedEventParams eventParams = new EiffelArtifactCreatedEventParams();

    public EiffelArtifactCreatedEventParams getEventParams() {
        return eventParams;
    }

    public void setEventParams(EiffelArtifactCreatedEventParams eventParams) {
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
        return "EiffelArtifactCreatedEvent{" +
                "msgParams=" + msgParams +
                ", eventParams=" + eventParams +
                '}';
    }
}
