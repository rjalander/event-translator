package com.ericsson.event.translator.eiffel.models;

import com.ericsson.eiffel.semantics.events.Link;
import com.ericsson.eiffel.semantics.events.EiffelArtifactPublishedEventData;
import java.util.ArrayList;

public class EiffelArtifactPublishedEventParams {

    private EiffelArtifactPublishedEventData data = new EiffelArtifactPublishedEventData();

    private ArrayList<Link> links = new ArrayList<>();

    public EiffelArtifactPublishedEventData getData() {
        return data;
    }

    public void setData(EiffelArtifactPublishedEventData data) {
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
