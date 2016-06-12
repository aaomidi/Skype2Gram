package com.aaomidi.skype2gram.handlers;

import com.aaomidi.skype2gram.models.S2GUser;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by amir on 2016-06-11.
 */
public class UserHandler {
	private final Map<Long, S2GUser> users = new HashMap<>();

	public void registerUser(S2GUser user) {
		users.put(user.getUserID(), user);
	}

	public S2GUser getUser(long id) {
		return users.get(id);
	}
}
