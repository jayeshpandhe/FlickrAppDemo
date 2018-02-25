package com.web.flickr.parsers;

import com.bgcomm.parsers.ResponseParser;
import com.web.flickr.FlickrAPIConstants;
import com.web.flickr.models.response.FlickrPhotoInfo;
import com.web.flickr.models.response.GetRecentPhotosResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GetRecentPhotosResponseParser implements ResponseParser {

    @Override
    public GetRecentPhotosResponse parse(String res) throws JSONException {
        JSONObject json = new JSONObject(res);
        boolean success = json.optString(FlickrAPIConstants.STAT).equalsIgnoreCase(FlickrAPIConstants.SUCCESS_STATUS);
        String message = json.optString(FlickrAPIConstants.MESSAGE);
        JSONObject photosJSON = json.optJSONObject(FlickrAPIConstants.PHOTOS);
        GetRecentPhotosResponse getRecentPhotosResponse = new GetRecentPhotosResponse();
        getRecentPhotosResponse.setMessage(message);
        getRecentPhotosResponse.setSuccess(success);
        if(photosJSON != null && success) {
            getRecentPhotosResponse.setPage(photosJSON.optInt(FlickrAPIConstants.PAGE));
            getRecentPhotosResponse.setPages(photosJSON.optInt(FlickrAPIConstants.PAGES));
            getRecentPhotosResponse.setPerPagePhotos(photosJSON.optInt(FlickrAPIConstants.PER_PAGE));
            getRecentPhotosResponse.setTotal(photosJSON.optInt(FlickrAPIConstants.TOTAL));
            JSONArray photosArray = photosJSON.optJSONArray(FlickrAPIConstants.PHOTO);
            getRecentPhotosResponse.setPhotosInfo(parsePhotoInfoList(photosArray));
        }
        return getRecentPhotosResponse;
    }

    private List<FlickrPhotoInfo> parsePhotoInfoList(JSONArray photosArray) throws JSONException {
        List<FlickrPhotoInfo> photoInfoList = null;
        if(photosArray != null) {
            photoInfoList = new ArrayList<>();
            for(int i = 0; i < photosArray.length(); i++) {
                JSONObject photoJSON = photosArray.getJSONObject(i);
                FlickrPhotoInfo flickrPhotoInfo = parsePhoto(photoJSON);
                if(flickrPhotoInfo != null) {
                    photoInfoList.add(flickrPhotoInfo);
                }
            }
        }
        return photoInfoList;
    }

    private FlickrPhotoInfo parsePhoto(JSONObject photoJSON) {
        FlickrPhotoInfo flickrPhotoInfo = null;
        if(photoJSON != null) {
            String id = photoJSON.optString(FlickrAPIConstants.ID);
            String owner = photoJSON.optString(FlickrAPIConstants.OWNER);
            String secret = photoJSON.optString(FlickrAPIConstants.SECRET);
            String server = photoJSON.optString(FlickrAPIConstants.SERVER);
            int farm = photoJSON.optInt(FlickrAPIConstants.FARM);
            String title = photoJSON.optString(FlickrAPIConstants.TITLE);
            int isPublic = photoJSON.optInt(FlickrAPIConstants.IS_PUBLIC);
            int isFriend = photoJSON.optInt(FlickrAPIConstants.IS_FRIEND);
            int isFamily = photoJSON.optInt(FlickrAPIConstants.IS_FAMILY);
            flickrPhotoInfo = new FlickrPhotoInfo(id, owner, secret, server, farm, title, isPublic, isFriend, isFamily);
        }
        return flickrPhotoInfo;
    }
}
