package com.ericsson.event.translator.eiffel.events;

import com.ericsson.event.translator.eiffel.models.EiffelEventMsgParams;
import com.ericsson.event.translator.eiffel.models.EiffelTestSuiteFinishedEventParams;

public class EiffelTestSuiteFinishedEvent {
    public EiffelTestSuiteFinishedEvent() {
    }

    private EiffelEventMsgParams msgParams = new EiffelEventMsgParams();
    private EiffelTestSuiteFinishedEventParams eventParams = new EiffelTestSuiteFinishedEventParams();

    public EiffelTestSuiteFinishedEventParams getEventParams() {
        return eventParams;
    }

    public void setEventParams(EiffelTestSuiteFinishedEventParams eventParams) {
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
