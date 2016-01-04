package com.webonise.gardenIt.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

public class SharedPreferenceManager {

    public static final String SHARED_PREF_NAME = "miles2share";
    private SharedPreferences mSharedPreference;

    public SharedPreferenceManager(Context mContext) {
        mSharedPreference = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
    }

    public SharedPreferenceManager(Context mContext, String prefFileName) {
        mSharedPreference = mContext.getSharedPreferences(prefFileName, Context.MODE_PRIVATE);
    }
    /**
     * This method is used to set String values in Preference file.
     *
     * @param key
     * @param value - String value to be set.
     */
    public void setStringValue(String key, String value) {
        mSharedPreference.edit().putString(key, value).commit();
    }

    /**
     * This method is used to get the String value from Preference file.
     *
     * @param key
     * @return value
     * @throws NullPointerException
     */
    public String getStringValue(String key) throws NullPointerException {
        return mSharedPreference.getString(key, null);
    }

    /**
     * This method is used to set Int values in Preference file.
     *
     * @param key
     * @param value - Int value to be set.
     */
    public void setIntValue(String key, int value) {
        mSharedPreference.edit().putInt(key, value).commit();
    }

    /**
     * This method is used to get the Int value from Preference file. If key is not found, -1 will be returned.
     *
     * @param key
     * @return value
     */
    public int getIntValue(String key) {
        return mSharedPreference.getInt(key, 0);
    }

    /**
     * This method is used to set Boolean values in Preference file.
     *
     * @param key
     * @param value - Boolean value to be set.
     */
    public void setBooleanValue(String key, boolean value) {
        mSharedPreference.edit().putBoolean(key, value).commit();
    }

    /**
     * This method is used to get the Boolean value from Preference file. If key is not found, false will be returned.
     *
     * @param key
     * @return value
     */
    public boolean getBooleanValue(String key) {
        return mSharedPreference.getBoolean(key, false);
    }

    /**
     * This method is used to clear all the data from shared preference.
     * This method should be called when user logs out of the application
     */
    public void clearSharedPreference() {
        mSharedPreference.edit().clear().commit();
    }

    /**
     * Function to save object in shared preferences
     *
     * @param key
     * @param object
     */
    public void putObject(String key, Object object) {
        setStringValue(key, new Gson().toJson(object));
    }

    /**
     * function to get object from shared preferences
     *
     * @param key
     * @param a
     * @param <T>
     * @return
     */
    public <T> T getObject(String key, Class<T> a) {

        String gson = getStringValue(key);
        try {
            return new Gson().fromJson(gson, a);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * This method is used to set long values in Preference file.
     *
     * @param key
     * @param value - Int value to be set.
     */
    public void setLongValue(String key, long value) {
        mSharedPreference.edit().putLong(key, value).commit();
    }

    /**
     * This method is used to get the long value from Preference file. If key is not found, 0 will be returned.
     *
     * @param key
     * @return value
     */
    public long getLongValue(String key) {
        return mSharedPreference.getLong(key, 0);
    }
}
