package com.aaomidi.skype2gram.models;

import lombok.Builder;
import lombok.Getter;

/**
 * Created by amir on 2016-06-11.
 */
@Builder
public class LoginInfo {
	@Getter
	private final String username;
	@Getter
	private final String password;
}
