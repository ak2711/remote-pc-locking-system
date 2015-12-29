package com.webonise.urbanfarmers.utilities;

import android.util.Log;

public class LogUtils {

	public static void LOGD(final String tag, String message) {
		if (BuildConfig.DEBUG) {
			Log.d(tag, message);
		}
	}

	public static void LOGV(final String tag, String message) {
		if (BuildConfig.DEBUG) {
			Log.v(tag, message);
		}
	}

	public static void LOGI(final String tag, String message) {
		if (BuildConfig.DEBUG) {
			Log.i(tag, message);
		}
	}

	public static void LOGW(final String tag, String message) {
		if (BuildConfig.DEBUG) {
			Log.w(tag, message);
		}
	}

	public static void LOGE(final String tag, String message) {
		if (BuildConfig.DEBUG) {
			Log.e(tag, message);
		}
	}

	public static void LOGE(final String tag, String message, Exception e) {
		if (BuildConfig.DEBUG) {
			Log.e(tag, message , e);
		}
	}

}
