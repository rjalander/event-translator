package com.ericsson.event.translator.eiffel.models;

public class EiffelEventMsgParams {

    public EiffelEventMsgParams() {
    }

    private EiffelEventMeta meta = new EiffelEventMeta();

    public EiffelEventMeta getMeta() {
        return meta;
    }

    public void setMeta(EiffelEventMeta meta) {
        this.meta = meta;
    }

    @Override
    public String toString() {
        return "EiffelSourceChangeCreatedMsgParams{" +
                "meta=" + meta +
                '}';
    }
}
