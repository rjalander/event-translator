package com.ericsson.event.translator.eiffel.models;

import com.ericsson.eiffel.semantics.events.Source;

import java.util.ArrayList;
import java.util.List;

public class EiffelEventMeta {

    public EiffelEventMeta() {
    }

    private String type;
    private String version;
    private List<String> tags = new ArrayList();
    private Source source;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }
}
