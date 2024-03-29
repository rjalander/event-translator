package com.ericsson.event.translator.eiffel.events;

import com.ericsson.event.translator.eiffel.models.*;

public class EiffelActivityFinishedEvent {
    public EiffelActivityFinishedEvent() {
    }

    private EiffelEventMsgParams msgParams = new EiffelEventMsgParams();
    private EiffelActivityFinishedEventParams eventParams = new EiffelActivityFinishedEventParams();

    public EiffelActivityFinishedEventParams getEventParams() {
        return eventParams;
    }

    public void setEventParams(EiffelActivityFinishedEventParams eventParams) {
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
        return "EiffelActivityFinishedEvent{" +
                "msgParams=" + msgParams +
                ", eventParams=" + eventParams +
                '}';
    }
}
