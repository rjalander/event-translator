package com.ericsson.event.translator.eiffel.events;

import com.ericsson.event.translator.eiffel.models.EiffelEventMsgParams;
import com.ericsson.event.translator.eiffel.models.EiffelTestSuiteStartedEventParams;

public class EiffelTestSuiteStartedEvent {
    public EiffelTestSuiteStartedEvent() {
    }

    private EiffelEventMsgParams msgParams = new EiffelEventMsgParams();
    private EiffelTestSuiteStartedEventParams eventParams = new EiffelTestSuiteStartedEventParams();

    public EiffelTestSuiteStartedEventParams getEventParams() {
        return eventParams;
    }

    public void setEventParams(EiffelTestSuiteStartedEventParams eventParams) {
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
        return "EiffelTestSuiteStartedEventParams{" +
                "msgParams=" + msgParams +
                ", eventParams=" + eventParams +
                '}';
    }
}
