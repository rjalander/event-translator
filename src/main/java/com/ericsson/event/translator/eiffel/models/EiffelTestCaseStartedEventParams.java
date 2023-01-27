package com.ericsson.event.translator.eiffel.models;

import com.ericsson.eiffel.semantics.events.EiffelTestCaseStartedEventData;
import com.ericsson.eiffel.semantics.events.Link;

import java.util.ArrayList;

public class EiffelTestCaseStartedEventParams {

    private EiffelTestCaseStartedEventData data = new EiffelTestCaseStartedEventData();

    private ArrayList<Link> links = new ArrayList<>();

    public EiffelTestCaseStartedEventData getData() {
        return data;
    }

    public void setData(EiffelTestCaseStartedEventData data) {
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
