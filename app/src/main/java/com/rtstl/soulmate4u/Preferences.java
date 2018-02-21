package com.rtstl.soulmate4u;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class Preferences {
	
	SharedPreferences prefs;
	
	public Preferences(Context context) {
		prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
	}

	public void storeStringPreference(Context context, String key, String value) {

		Editor e = prefs.edit();
		e.putString(key, value);
		e.commit();
	}

	public void clearPreference() {

		Editor e = prefs.edit();
		e.clear();
		e.commit();
	}

	public String getStringPreference(Context context, String key) {

		return prefs.getString(key, "");
	}

	public void storeIntPreference(Context context, String key, int value) {

		Editor e = prefs.edit();
		e.putInt(key, value);
		e.commit();
	}

	public int getIntPreference(Context context, String key) {

		return prefs.getInt(key, 0);
	}

	public void storeBooleanPreference(Context context, String key,
			Boolean value) {

		Editor e = prefs.edit();
		e.putBoolean(key, value);
		e.commit();
	}

	public Boolean getBooleanPreference(Context context, String key) {

		return prefs.getBoolean(key, false);
	}

	public void storeLongPreference(Context context, String key, long value) {

		Editor e = prefs.edit();
		e.putLong(key, value);
		e.commit();
	}

	public long getLongPreference(Context context, String key) {

		return prefs.getLong(key, 0);
	}
}
