package com.aaomidi.skype2gram.models;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

/**
 * Created by amir on 2016-06-12.
 */
@RequiredArgsConstructor
public class ChatLinks {
	// Stored as TelegramID, SkypeID
	private final BidiMap<String, String> links = new DualHashBidiMap<>();

	public void addLink(String telegramID, String skypeID) {
		links.put(telegramID, skypeID);
	}
	public String getTelegram(String skypeID){
		return links.getKey(skypeID);
	}
	public String getSkype(String telegramID){
		return links.get(telegramID);
	}
}
