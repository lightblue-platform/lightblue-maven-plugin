package com.redhat.lightblue.build.plugin.maven;

import java.util.Date;

public class TestEntity {

    public final static String ENTITY_NAME = "test";

    private String _id;
    private String hostname;
    private String value;
    private Date creationDate;

    public TestEntity() {
    }

    public TestEntity(String hostname, String value) {
        this.hostname = hostname;
        this.value = value;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

}
