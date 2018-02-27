package com.web;

import android.net.Uri;

import com.web.flickr.FlickrAPIConstants;
import com.web.flickr.models.request.GetRecentPhotosRequest;
import com.web.flickr.models.response.FlickrPhotoInfo;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Builder class to build Flickr API URLs
 */
public class FlickrURLBuilder {

    public static String buildRecentPhotosURL(int pageToLoad) {
        GetRecentPhotosRequest getRecentPhotosRequest = new GetRecentPhotosRequest("flickr.photos.getRecent", FlickrAPIConstants.FLICKR_API_KEY, pageToLoad, "json", "1");
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

    public static String buildPhotoThumbnailURL(FlickrPhotoInfo flickrPhotoInfo) {
        int farm = flickrPhotoInfo.getFarm();
        String server = encode(flickrPhotoInfo.getServer());
        String id_secret = encode(flickrPhotoInfo.getId() + "_" + flickrPhotoInfo.getSecret() + "_z");
        return "https://farm" + farm + ".staticflickr.com/" + server + "/" + id_secret + ".jpg";
    }

    public static String buildPhotoURL(FlickrPhotoInfo flickrPhotoInfo) {
        int farm = flickrPhotoInfo.getFarm();
        String server = flickrPhotoInfo.getServer();
        String id_secret = flickrPhotoInfo.getId() + "_" + flickrPhotoInfo.getSecret();
        return "https://farm" + farm + ".staticflickr.com/" + server + "/" + id_secret + ".jpg";
    }

    private static String encode(String param) {
        try {
            param = URLEncoder.encode(param, "utf-8");
        } catch (UnsupportedEncodingException e) {}
        return param;
    }
}
