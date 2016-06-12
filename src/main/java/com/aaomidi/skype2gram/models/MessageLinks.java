package com.aaomidi.skype2gram.models;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

import java.util.HashMap;

/**
 * Created by amir on 2016-06-12.
 */
public class MessageLinks {
	private final HashMap<String, String> map = new HashMap<>(); // MessageID, ChatID

	public void addLink(String messageID, String chatIdentity) {
		map.put(messageID, chatIdentity);
	}

	public String getSkypeChat(String messageID) {
		return map.get(messageID);
	}
}
