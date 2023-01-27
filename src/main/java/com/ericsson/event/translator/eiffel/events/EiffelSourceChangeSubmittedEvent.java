package com.ericsson.event.translator.eiffel.events;

import com.ericsson.event.translator.eiffel.models.EiffelEventMsgParams;
import com.ericsson.event.translator.eiffel.models.EiffelSourceChangeSubmittedEventParams;

public class EiffelSourceChangeSubmittedEvent {
    public EiffelSourceChangeSubmittedEvent() {
    }

    private EiffelEventMsgParams msgParams = new EiffelEventMsgParams();
    private EiffelSourceChangeSubmittedEventParams eventParams = new EiffelSourceChangeSubmittedEventParams();

    public EiffelSourceChangeSubmittedEventParams getEventParams() {
        return eventParams;
    }

    public void setEventParams(EiffelSourceChangeSubmittedEventParams eventParams) {
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
        return "EiffelSourceChangeSubmittedEvent{" +
                "msgParams=" + msgParams +
                ", eventParams=" + eventParams +
                '}';
    }
}
