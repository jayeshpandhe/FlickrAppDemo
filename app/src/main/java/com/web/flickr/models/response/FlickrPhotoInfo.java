package com.web.flickr.models.response;

public class FlickrPhotoInfo {
    //"id":"25541309597","owner":"154034706@N05","secret":"3313c3a3b1","server":"4704","farm":5,"title":"#NiceWiseWords Share & Follow for more!","ispublic":1,"isfriend":0,"isfamily":0
    private String id;
    private String owner;
    private String secret;
    private String server;
    private int farm;
    private String title;
    private int isPublic;
    private int friend;
    private int family;

    public FlickrPhotoInfo(String id, String owner, String secret, String server, int farm, String title, int isPublic, int friend, int family) {
        this.id = id;
        this.owner = owner;
        this.secret = secret;
        this.server = server;
        this.farm = farm;
        this.title = title;
        this.isPublic = isPublic;
        this.friend = friend;
        this.family = family;
    }

    public String getId() {
        return id;
    }

    public String getOwner() {
        return owner;
    }

    public String getSecret() {
        return secret;
    }

    public String getServer() {
        return server;
    }

    public int getFarm() {
        return farm;
    }

    public String getTitle() {
        return title;
    }

    public int isPublic() {
        return isPublic;
    }

    public int isFriend() {
        return friend;
    }

    public int isFamily() {
        return family;
    }
}
