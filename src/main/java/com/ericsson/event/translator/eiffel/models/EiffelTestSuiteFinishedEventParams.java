package com.ericsson.event.translator.eiffel.models;

import com.ericsson.eiffel.semantics.events.EiffelTestSuiteFinishedEventData;
import com.ericsson.eiffel.semantics.events.Link;

import java.util.ArrayList;

public class EiffelTestSuiteFinishedEventParams {

    private EiffelTestSuiteFinishedEventData data = new EiffelTestSuiteFinishedEventData();

    private ArrayList<Link> links = new ArrayList<>();

    public EiffelTestSuiteFinishedEventData getData() {
        return data;
    }

    public void setData(EiffelTestSuiteFinishedEventData data) {
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
        return "EiffelTestSuiteFinishedEventData{" +
                "data=" + data +
                ", links=" + links +
                '}';
    }
}
