package com.webonise.gardenIt.utilities;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Typeface;

public class TypefaceProvider {
	private static final String TAG = "TypefaceProvider";
	
	private static Map<String, Typeface> typefaces = new HashMap<String, Typeface>();

	public static Typeface getTypeFace(Context context, String asset, int defStyle) {
		Typeface typeface = typefaces.get(asset);
		if (typeface == null) {
			try {
				if (defStyle == Typeface.BOLD) {
					typeface = Typeface.createFromAsset(context.getAssets(), "fonts/sans_bold.ttf");
				} else {
					typeface = Typeface.createFromAsset(context.getAssets(), "fonts/sans_regular.ttf");
				}
								
			}  catch (Exception ex) {
				LogUtils.LOGE(TAG, "Could not load typeface: "+ex.getMessage() );
			}
			typefaces.put(asset, typeface);
		}
		
		return typeface;
	}
}
