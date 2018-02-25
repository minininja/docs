package org.dorkmaster.docs.index.api;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Document {
    private String id;
    private String index;
    private String bucket;
    private String fileId;
    private Set<String> tags = new HashSet<>();
    private Map<String, String> fields = new HashMap<>();

    public String getId() {
        return id;
    }

    public Document setId(String id) {
        this.id = id;
        return this;
    }

    public String getIndex() {
        return index;
    }

    public Document setIndex(String index) {
        this.index = index;
        return this;
    }

    public String getBucket() {
        return bucket;
    }

    public Document setBucket(String bucket) {
        this.bucket = bucket;
        return this;
    }

    public String getFileId() {
        return fileId;
    }

    public Document setFileId(String fileId) {
        this.fileId = fileId;
        return this;
    }

    public Set<String> getTags() {
        return tags;
    }

    public Document setTags(Set<String> tags) {
        this.tags = tags;
        return this;
    }

    public Map<String, String> getFields() {
        return fields;
    }

    public Document setFields(Map<String, String> fields) {
        this.fields = fields;
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Document{");
        sb.append("id='").append(id).append('\'');
        sb.append(", index='").append(index).append('\'');
        sb.append(", bucket='").append(bucket).append('\'');
        sb.append(", fileId='").append(fileId).append('\'');
        sb.append(", tags=").append(tags);
        sb.append(", fields=").append(fields);
        sb.append('}');
        return sb.toString();
    }
}
