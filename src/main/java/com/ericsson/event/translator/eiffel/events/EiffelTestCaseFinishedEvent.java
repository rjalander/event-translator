package com.ericsson.event.translator.eiffel.events;

import com.ericsson.event.translator.eiffel.models.EiffelEventMsgParams;
import com.ericsson.event.translator.eiffel.models.EiffelTestCaseFinishedEventParams;

public class EiffelTestCaseFinishedEvent {
    public EiffelTestCaseFinishedEvent() {
    }

    private EiffelEventMsgParams msgParams = new EiffelEventMsgParams();
    private EiffelTestCaseFinishedEventParams eventParams = new EiffelTestCaseFinishedEventParams();

    public EiffelTestCaseFinishedEventParams getEventParams() {
        return eventParams;
    }

    public void setEventParams(EiffelTestCaseFinishedEventParams eventParams) {
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
