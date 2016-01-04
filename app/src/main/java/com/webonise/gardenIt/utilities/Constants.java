package com.webonise.gardenIt.utilities;

public class Constants {

    public static final String KEY_PREF_IS_USER_LOGGED_IN = "isUserLoggedIn";
    public static final String KEY_PREF_IS_GARDEN_CREATED = "isGardenCreated";
    public static final String KEY_PREF_USER = "user";
    public static final String KEY_PREF_USER_PHONE_NUMBER = "phone_number";

    private static final String BASE_URL = "http://test-uf.weboapps.com/apis/";
    public static final String REGISTER_URL = BASE_URL +  "register";
    public static final String CREATE_GARDEN = BASE_URL +  "create_garden";

    public static final String REQUEST_KEY_EMAIl = "email";
    public static final String REQUEST_KEY_NAME = "name";
    public static final String REQUEST_KEY_PHONE_NUMBER = "phone_number";
    public static final String REQUEST_KEY_DESCRIPTION = "description";
    public static final String REQUEST_KEY_LATITUDE = "latitude";
    public static final String REQUEST_KEY_LONGITUDE = "longitude";
    public static final String REQUEST_KEY_ADDRESS = "address";
    public static final String REQUEST_KEY_GARDEN_TYPE = "garden_type";
    public static final int RESPONSE_CODE_200 = 200;

    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String APPLICATION_JSON = "application/json";
    public static final String HEADER_ACCEPT = "Accept";

}
