package com.web.flickr;

import android.text.TextUtils;

import com.web.FlickrURLBuilder;
import com.web.NetworkHandler;
import com.web.flickr.models.response.GetRecentPhotosResponse;
import com.web.flickr.parsers.GetRecentPhotosResponseParser;

public class FlickrWebServicesImpl {
    public GetRecentPhotosResponse executeGetRecentPhotosRequest(int page) throws Exception {
        GetRecentPhotosResponse getRecentPhotosResponse = null;
        String request = FlickrURLBuilder.buildRecentPhotosURL(page);
        String response = NetworkHandler.executeGETRequest(request);
        if (!TextUtils.isEmpty(response)) {
            getRecentPhotosResponse = new GetRecentPhotosResponseParser().parse(response);
        }
        return getRecentPhotosResponse;
    }
}
