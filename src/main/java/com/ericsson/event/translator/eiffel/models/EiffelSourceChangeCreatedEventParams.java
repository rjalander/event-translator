package com.ericsson.event.translator.eiffel.models;

import com.ericsson.eiffel.semantics.events.EiffelSourceChangeCreatedEventData;
import com.ericsson.eiffel.semantics.events.Link;

import java.util.ArrayList;

public class EiffelSourceChangeCreatedEventParams {

    private EiffelSourceChangeCreatedEventData data = new EiffelSourceChangeCreatedEventData();

    private ArrayList<Link> links = new ArrayList<>();

    public EiffelSourceChangeCreatedEventData getData() {
        return data;
    }

    public void setData(EiffelSourceChangeCreatedEventData data) {
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
        return "EiffelSourceChangeCreatedEventParams{" +
                "data=" + data +
                ", links=" + links +
                '}';
    }
}
