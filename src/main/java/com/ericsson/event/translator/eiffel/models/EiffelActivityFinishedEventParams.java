package com.ericsson.event.translator.eiffel.models;

import com.ericsson.eiffel.semantics.events.EiffelActivityFinishedEventData;
import com.ericsson.eiffel.semantics.events.Link;

import java.util.ArrayList;

public class EiffelActivityFinishedEventParams {

    private EiffelActivityFinishedEventData data = new EiffelActivityFinishedEventData();

    private ArrayList<Link> links = new ArrayList<>();

    public EiffelActivityFinishedEventData getData() {
        return data;
    }

    public void setData(EiffelActivityFinishedEventData data) {
        this.data = data;
    }

    public ArrayList<Link> getLinks() {
        return links;
    }

    public void setLinks(ArrayList<Link> links) {
        this.links = links;
    }

    @Override
    public String toString() {
        return "EiffelArtifactPublishedEventParams{" +
                "data=" + data +
                ", links=" + links +
                '}';
    }
}
