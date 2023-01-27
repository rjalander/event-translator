package com.ericsson.event.translator.eiffel.models;

import com.ericsson.eiffel.semantics.events.EiffelTestCaseFinishedEventData;
import com.ericsson.eiffel.semantics.events.Link;

import java.util.ArrayList;

public class EiffelTestCaseFinishedEventParams {

    private EiffelTestCaseFinishedEventData data = new EiffelTestCaseFinishedEventData();

    private ArrayList<Link> links = new ArrayList<>();

    public EiffelTestCaseFinishedEventData getData() {
        return data;
    }

    public void setData(EiffelTestCaseFinishedEventData data) {
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
        return "EiffelTestCaseStartedEventParams{" +
                "data=" + data +
                ", links=" + links +
                '}';
    }
}
