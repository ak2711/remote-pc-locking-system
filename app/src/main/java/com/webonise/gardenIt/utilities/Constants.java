package com.webonise.gardenIt.utilities;

public class Constants {

    public static final String KEY_PREF_IS_USER_LOGGED_IN = "isUserLoggedIn";
    public static final String KEY_PREF_IS_GARDEN_CREATED = "isGardenCreated";
    public static final String KEY_PREF_IS_PLANT_ADDED = "isPlantAdded";
    public static final String KEY_PREF_USER = "user";
    public static final String KEY_PREF_GARDEN_DETAILS = "garden";
    public static final String KEY_PREF_USER_GARDEN_PLANTS = "user_garden_plants";
    public static final String KEY_PREF_USER_ISSUES = "user_issues";


    public static final String BASE_URL = "http://test-uf.weboapps.com";
    public static final String BASE_URL_API = BASE_URL + "/apis/";
    public static final String REGISTER_URL = BASE_URL_API +  "register";
    public static final String CREATE_GARDEN_URL = BASE_URL_API +  "create_garden";
    public static final String ADD_PLANT_URL = BASE_URL_API +  "create_plant";
    public static final String SIGN_IN_URL = BASE_URL_API +  "sign_in";
    public static final String CREATE_ISSUE_URL = BASE_URL_API +  "create_issue";
    public static final String REQUEST_SERVICE_URL = BASE_URL_API +  "request_gardener";
    public static final String REQUEST_LIST_URL = BASE_URL_API + "service_requests";
    public static final String ISSUES_LIST_URL = BASE_URL_API + "issues";

    public static final String REQUEST_KEY_EMAIl = "email";
    public static final String REQUEST_KEY_NAME = "name";
    public static final String REQUEST_KEY_PHONE_NUMBER = "phone_number";
    public static final String REQUEST_KEY_DESCRIPTION = "description";
    public static final String REQUEST_KEY_LATITUDE = "latitude";
    public static final String REQUEST_KEY_LONGITUDE = "longitude";
    public static final String REQUEST_KEY_ADDRESS = "address";
    public static final String REQUEST_KEY_GARDEN_TYPE = "garden_type";
    public static final String REQUEST_KEY_GARDEN_ID = "garden_id";
    public static final String REQUEST_KEY_PLANT_IMAGE = "plant_image";
    public static final String REQUEST_ADDITIONAL_PARAMTER_FOR_IMAGE = "data:image/jpeg;base64,";
    public static final int RESPONSE_CODE_200 = 200;

    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String APPLICATION_JSON = "application/json";
    public static final String HEADER_ACCEPT = "Accept";

    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1;
    public static final int PICK_IMAGE = CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE + 1;

    public static final String BUNDLE_KEY_SHOW_BACK_ICON = "show_back_icon";
    public static final String BUNDLE_KEY_PLANT_ID = "plantId";
    public static final String BUNDLE_KEY_GARDEN_ID = "gardenId";
}
