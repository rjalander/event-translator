package com.ericsson.event.translator.eiffel.events;

import com.ericsson.event.translator.eiffel.models.EiffelEventMsgParams;
import com.ericsson.event.translator.eiffel.models.EiffelSourceChangeCreatedEventParams;

public class EiffelSourceChangeCreatedEvent {
    public EiffelSourceChangeCreatedEvent() {
    }

    private EiffelEventMsgParams msgParams = new EiffelEventMsgParams();
    private EiffelSourceChangeCreatedEventParams eventParams = new EiffelSourceChangeCreatedEventParams();

    public EiffelSourceChangeCreatedEventParams getEventParams() {
        return eventParams;
    }

    public void setEventParams(EiffelSourceChangeCreatedEventParams eventParams) {
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
        return "EiffelSourceChangeCreatedEvent{" +
                "msgParams=" + msgParams +
                ", eventParams=" + eventParams +
                '}';
    }
}
