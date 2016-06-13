package com.aaomidi.skype2gram.models;

import lombok.RequiredArgsConstructor;

import java.util.HashMap;

/**
 * Created by amir on 2016-06-12.
 */
@RequiredArgsConstructor
public class ChatLinks {
	//private final BidiMap<String, String> links = new DualHashBidiMap<>();

	// Stored as TelegramID, SkypeID
	private final HashMap<String, String> telegramToSkypeLink = new HashMap<>();
	// Stored as SkypeID, TelegramID
	private final HashMap<String, String> skypeToTelegramLink = new HashMap<>();

	public void addLink(String telegramID, String skypeID) {
		telegramToSkypeLink.put(telegramID, skypeID);
		skypeToTelegramLink.put(skypeID, telegramID);
	}

	public String getTelegram(String skypeID) {
		return skypeToTelegramLink.get(skypeID);
	}

	public String getSkype(String telegramID) {
		return telegramToSkypeLink.get(telegramID);
	}
}
