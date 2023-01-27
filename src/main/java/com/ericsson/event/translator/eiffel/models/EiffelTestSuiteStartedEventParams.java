package com.ericsson.event.translator.eiffel.models;

import com.ericsson.eiffel.semantics.events.EiffelTestSuiteStartedEventData;
import com.ericsson.eiffel.semantics.events.Link;

import java.util.ArrayList;

public class EiffelTestSuiteStartedEventParams {

    private EiffelTestSuiteStartedEventData data = new EiffelTestSuiteStartedEventData();

    private ArrayList<Link> links = new ArrayList<>();

    public EiffelTestSuiteStartedEventData getData() {
        return data;
    }

    public void setData(EiffelTestSuiteStartedEventData data) {
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
        return "EiffelTestSuiteStartedEventParams{" +
                "data=" + data +
                ", links=" + links +
                '}';
    }
}
