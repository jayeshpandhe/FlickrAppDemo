package com.web.flickr;

public interface FlickrAPIConstants {
    String GET_RECENT_PHOTOS_REQUEST_URL = "https://api.flickr.com/services/rest/";
    String FLICKR_API_KEY = "";

    /* Generic constants start */
    String STAT = "stat";
    String MESSAGE = "message";
    String SUCCESS_STATUS = "ok";
    /* Generic constants end */

    /* GetRecentPhotosRequest start */
    String METHOD = "method";
    String API_KEY = "api_key";
    String FORMAT = "format";
    String NO_JSON_CALLBACK = "nojsoncallback";
    /* GetRecentPhotosRequest end */

    /* GetRecentPhotosResponse start */
    String PHOTOS = "photos";
    String PAGE = "page";
    String PAGES = "pages";
    String PER_PAGE = "perpage";
    String TOTAL = "total";
    String PHOTO = "photo";
    /* GetRecentPhotosResponse end */

    /* PhotoInfo start */
    String ID = "id";
    String OWNER = "owner";
    String SECRET = "secret";
    String SERVER = "server";
    String FARM = "farm";
    String TITLE = "title";
    String IS_PUBLIC = "ispublic";
    String IS_FRIEND = "isfriend";
    String IS_FAMILY = "isfamily";
    /* PhotoInfo end */
}
