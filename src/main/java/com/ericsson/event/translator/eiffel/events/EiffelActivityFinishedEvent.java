package com.ericsson.event.translator.eiffel.events;

import com.ericsson.event.translator.eiffel.models.EiffelActivityFinishedEventParams;
import com.ericsson.event.translator.eiffel.models.EiffelActivityFinishedMsgParams;
import com.ericsson.event.translator.eiffel.models.EiffelArtifactPublishedEventParams;
import com.ericsson.event.translator.eiffel.models.EiffelArtifactPublishedMsgParams;

public class EiffelActivityFinishedEvent {
    public EiffelActivityFinishedEvent() {
    }

    private EiffelActivityFinishedMsgParams msgParams = new EiffelActivityFinishedMsgParams();
    private EiffelActivityFinishedEventParams eventParams = new EiffelActivityFinishedEventParams();

    public EiffelActivityFinishedEventParams getEventParams() {
        return eventParams;
    }

    public void setEventParams(EiffelActivityFinishedEventParams eventParams) {
        this.eventParams = eventParams;
    }

    public EiffelActivityFinishedMsgParams getMsgParams() {
        return msgParams;
    }

    public void setMsgParams(EiffelActivityFinishedMsgParams msgParams) {
        this.msgParams = msgParams;
    }

    @Override
    public String toString() {
        return "EiffelArtifactPublishedEvent{" +
                "eventParams=" + eventParams +
                "} " + super.toString();
    }
}
