package com.aaomidi.skype2gram.handlers;

import java.util.HashMap;

/**
 * Created by amir on 2016-06-14.
 */
public class CallbackCacheHandler {
	private static final HashMap<String, String> info = new HashMap<>();

	public static void storeData(String uuid, String data) {
		info.put(uuid, data);
	}

	public static String getData(String uuid) {
		return info.get(uuid);
	}
}
