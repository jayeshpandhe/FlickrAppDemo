package com.web.flickr;

import android.net.Uri;
import android.text.TextUtils;

import com.web.NetworkHandler;
import com.web.flickr.models.request.GetRecentPhotosRequest;
import com.web.flickr.models.response.GetRecentPhotosResponse;
import com.web.flickr.parsers.GetRecentPhotosResponseParser;

public class FlickrWebServicesImpl {

    public GetRecentPhotosResponse executeGetRecentPhotosRequest(int page) throws Exception {
        GetRecentPhotosRequest getRecentPhotosRequest = new GetRecentPhotosRequest("flickr.photos.getRecent", FlickrAPIConstants.FLICKR_API_KEY, page, "json", "1");
        GetRecentPhotosResponse getRecentPhotosResponse = null;
        String request = constructGetRecentPhotosRequest(getRecentPhotosRequest);
        String response = NetworkHandler.executeGETRequest(request);
        if(!TextUtils.isEmpty(response)) {
            getRecentPhotosResponse = new GetRecentPhotosResponseParser().parse(response);
        }
        return getRecentPhotosResponse;
    }

    private String constructGetRecentPhotosRequest(GetRecentPhotosRequest getRecentPhotosRequest) {
        String page = "" + getRecentPhotosRequest.getPage();
        return Uri.parse(FlickrAPIConstants.GET_RECENT_PHOTOS_REQUEST_URL)
                .buildUpon()
                .appendQueryParameter(FlickrAPIConstants.METHOD, getRecentPhotosRequest.getMethod())
                .appendQueryParameter(FlickrAPIConstants.API_KEY, getRecentPhotosRequest.getApiKey())
                .appendQueryParameter(FlickrAPIConstants.PAGE, page)
                .appendQueryParameter(FlickrAPIConstants.FORMAT, getRecentPhotosRequest.getFormat())
                .appendQueryParameter(FlickrAPIConstants.NO_JSON_CALLBACK, getRecentPhotosRequest.getNoJSONCallback())
                .build().toString();
    }
}
