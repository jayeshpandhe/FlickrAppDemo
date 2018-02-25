package com.web.flickr.models.response;

import com.bgcomm.models.Response;

import java.util.List;

public class GetRecentPhotosResponse implements Response {
    // "page": 1, "pages": 10, "perpage": 100, "total": "1000"
    private int page;
    private int pages;
    private int perPagePhotos;
    private int total;
    private List<FlickrPhotoInfo> photosInfo;
    private boolean success;
    private String message;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public int getPerPagePhotos() {
        return perPagePhotos;
    }

    public void setPerPagePhotos(int perPagePhotos) {
        this.perPagePhotos = perPagePhotos;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<FlickrPhotoInfo> getPhotosInfo() {
        return photosInfo;
    }

    public void setPhotosInfo(List<FlickrPhotoInfo> photosInfo) {
        this.photosInfo = photosInfo;
    }

    @Override
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
