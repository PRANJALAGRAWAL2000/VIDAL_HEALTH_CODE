package com.bajaj.qualifier.model;

public class SubmitRequest {
    private String query;

    public SubmitRequest() {
    }

    public SubmitRequest(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
