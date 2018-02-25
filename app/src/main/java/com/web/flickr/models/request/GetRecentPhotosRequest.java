package com.web.flickr.models.request;

import com.bgcomm.models.Request;

public class GetRecentPhotosRequest implements Request {
    private String method;
    private String apiKey;
    private int page;
    private String format;
    private String noJSONCallback;

    public GetRecentPhotosRequest(String method, String apiKey, int page, String format, String noJSONCallback) {
        this.method = method;
        this.apiKey = apiKey;
        this.page = page;
        this.format = format;
        this.noJSONCallback = noJSONCallback;
    }

    public String getMethod() {
        return method;
    }

    public String getApiKey() {
        return apiKey;
    }

    public int getPage() {
        return page;
    }

    public String getFormat() {
        return format;
    }

    public String getNoJSONCallback() {
        return noJSONCallback;
    }
}
