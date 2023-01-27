package com.ericsson.event.translator.eiffel.events;

import com.ericsson.event.translator.eiffel.models.EiffelEventMsgParams;
import com.ericsson.event.translator.eiffel.models.EiffelTestCaseStartedEventParams;

public class EiffelTestCaseStartedEvent {
    public EiffelTestCaseStartedEvent() {
    }

    private EiffelEventMsgParams msgParams = new EiffelEventMsgParams();
    private EiffelTestCaseStartedEventParams eventParams = new EiffelTestCaseStartedEventParams();

    public EiffelTestCaseStartedEventParams getEventParams() {
        return eventParams;
    }

    public void setEventParams(EiffelTestCaseStartedEventParams eventParams) {
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
        return "EiffelTestCaseStartedEventParams{" +
                "msgParams=" + msgParams +
                ", eventParams=" + eventParams +
                '}';
    }
}
