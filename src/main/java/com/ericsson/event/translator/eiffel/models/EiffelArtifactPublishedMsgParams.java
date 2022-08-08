package com.ericsson.event.translator.eiffel.models;

public class EiffelArtifactPublishedMsgParams {

    public EiffelArtifactPublishedMsgParams() {
    }

    private EiffelArtifactPublishedEventMeta meta = new EiffelArtifactPublishedEventMeta();

    public EiffelArtifactPublishedEventMeta getMeta() {
        return meta;
    }

    public void setMeta(EiffelArtifactPublishedEventMeta meta) {
        this.meta = meta;
    }

    @Override
    public String toString() {
        return "MsgParams{" +
                "meta=" + meta +
                '}';
    }
}
