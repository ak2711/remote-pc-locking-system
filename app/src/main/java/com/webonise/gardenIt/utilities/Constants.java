package com.webonise.gardenIt.utilities;

public class Constants {

    public static final String KEY_PREF_IS_USER_LOGGED_IN = "isUserLoggedIn";
    public static final String KEY_PREF_IS_GARDEN_CREATED = "isGardenCreated";
    public static final String KEY_PREF_IS_PLANT_ADDED = "isPlantAdded";
    public static final String KEY_PREF_USER = "user";
    public static final String KEY_PREF_GARDEN_DETAILS = "garden";
    public static final String KEY_PREF_USER_GARDEN_PLANTS = "user_garden_plants";
    public static final String KEY_PREF_USER_PHONE_NUMBER = "user_phone_number";
    public static final String KEY_PREF_USER_PASSWORD = "password";
    public static final String KEY_PREF_USER_ISSUES = "user_issues";
    public static final String KEY_PREF_USER_REQUEST = "user_requests";
    public static final String KEY_PREF_GARDEN_ID = "gardenID";

    public static final String BASE_URL = "http://test-uf.weboapps.com";
    public static final String BASE_URL_API = BASE_URL + "/apis/";
    public static final String REGISTER_URL = BASE_URL_API + "register";
    public static final String CREATE_GARDEN_URL = BASE_URL_API + "create_garden";
    public static final String ADD_PLANT_URL = BASE_URL_API + "create_plant";
    public static final String SIGN_IN_URL = BASE_URL_API + "sign_in";
    public static final String CREATE_ISSUE_URL = BASE_URL_API + "create_issue";
    public static final String REQUEST_SERVICE_URL = BASE_URL_API + "request_gardener";
    public static final String REQUEST_LIST_URL = BASE_URL_API + "service_requests";
    public static final String ISSUES_LIST_URL = BASE_URL_API + "issues";
    public static final String ADD_LOG_URL = BASE_URL_API + "add_log";
    public static final String GET_PLANT_DETAILS_URL = BASE_URL_API + "get_plant";

    public static final String REQUEST_KEY_EMAIl = "email";
    public static final String REQUEST_KEY_NAME = "name";
    public static final String REQUEST_KEY_REFERRED_BY = "referred_by";
    public static final String REQUEST_KEY_PHONE_NUMBER = "phone_number";
    public static final String REQUEST_KEY_DESCRIPTION = "description";
    public static final String REQUEST_KEY_LATITUDE = "latitude";
    public static final String REQUEST_KEY_LONGITUDE = "longitude";
    public static final String REQUEST_KEY_ADDRESS = "address";
    public static final String REQUEST_KEY_GARDEN_TYPE = "garden_type";
    public static final String REQUEST_KEY_GARDEN_ID = "garden_id";
    public static final String REQUEST_KEY_PLANT_IMAGE = "plant_image";
    public static final String REQUEST_KEY_PLANT_ID = "plant_id";
    public static final String REQUEST_ADDITIONAL_PARAMETER_FOR_IMAGE = "data:image/jpeg;base64,";
    public static final String REQUEST_KEY_PASSWORD = "password";
    public static final int RESPONSE_CODE_200 = 200;

    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String APPLICATION_JSON = "application/json";
    public static final String HEADER_ACCEPT = "Accept";

    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1;
    public static final int PICK_IMAGE = CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE + 1;

    public static final String BUNDLE_KEY_SHOW_BACK_ICON = "show_back_icon";
    public static final String BUNDLE_KEY_PLANT_ID = "plantId";
    public static final String BUNDLE_KEY_GARDEN_ID = "gardenId";
    public static final String BUNDLE_KEY_TYPE = "type";
    public static final String BUNDLE_KEY_POSITION = "position";
    public static final String BUNDLE_KEY_TOP = "top";
    public static final String BUNDLE_KEY_WIDTH = "width";
    public static final String BUNDLE_KEY_LEFT = "left";
    public static final String BUNDLE_KEY_HEIGHT = "height";
    public static final String BUNDLE_KEY_TITLE = "title";
    public static final String BUNDLE_KEY_IMAGE_URL = "image_url";

    public static final int CREATE_ISSUE = 0;
    public static final int REQUEST_SERVICE = CREATE_ISSUE + 1;


    public static class ScreenName {
        public static final String SPLASH_SCREEN = "Splash Screen";
        public static final String SIGN_IN_SCREEN = "Sign In Screen";
        public static final String SIGN_UP_SCREEN = "Sign Up Screen";
        public static final String CREATE_GARDEN_SCREEN = "Create Garden Screen";
        public static final String ADD_PLANT_SCREEN = "Add Plant Screen";
        public static final String REQUEST_SERVICE_SCREEN = "Request Service Screen";
        public static final String GET_ADVICE_SCREEN = "Get Advice Screen";
        public static final String ADD_LOGS_SCREEN = "Add Logs Screen";
        public static final String DASHBOARD_SCREEN = "Dashboard Screen";
        public static final String PLANT_DETAILS_SCREEN = "Plant Details Screen";
        public static final String ADVICE_LIST_SCREEN = "Advice List Screen";
        public static final String REQUESTED_SERVICE_LIST_SCREEN = "Requested Service ListScreen";
    }

    public static final int SUCCESS_STATE_VISIBLE_TIME = 3000; //3 seconds
}
