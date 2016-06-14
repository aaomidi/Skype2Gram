package com.aaomidi.skype2gram.handlers;

import com.aaomidi.skype2gram.models.LoginInfo;
import com.aaomidi.skype2gram.models.S2GUser;
import com.samczsun.skype4j.Skype;
import com.samczsun.skype4j.SkypeBuilder;
import com.samczsun.skype4j.chat.Chat;
import com.samczsun.skype4j.events.EventHandler;
import com.samczsun.skype4j.events.Listener;
import com.samczsun.skype4j.events.chat.message.MessageReceivedEvent;
import com.samczsun.skype4j.exceptions.ConnectionException;
import com.samczsun.skype4j.exceptions.InvalidCredentialsException;
import lombok.Getter;

import java.util.logging.Logger;

/**
 * Created by amir on 2016-06-11.
 */
public class SkypeHandler implements Listener {
	private transient final Logger log = Logger.getLogger("Main");

	private final S2GUser user;
	private final String username;
	private final String password;
	@Getter
	private transient final Skype skype;

	public SkypeHandler(String username, String password, S2GUser s2GUser) {
		this.username = username;
		this.password = password;
		this.user = s2GUser;

		skype = new SkypeBuilder(username, password).withAllResources().build();
	}

	public SkypeHandler(LoginInfo loginInfo, S2GUser s2GUser) {
		this(loginInfo.getUsername(), loginInfo.getPassword(), s2GUser);
	}

	public SkypeStatus login() {
		try {
			skype.login();
			skype.subscribe();
			skype.getEventDispatcher().registerListener(this);
			for (String identity : user.getCachedIdentities()) {
				skype.getOrLoadChat(identity);
			}
		} catch (InvalidCredentialsException ex) {
			return SkypeStatus.INVALID_CREDENTIALS;
		} catch (ConnectionException ex) {
			return SkypeStatus.CONNECTION_ERROR;
		} catch (Exception ex) {
			return SkypeStatus.UNKNOWN_ERROR;
		}
		return SkypeStatus.NO_ERROR;
	}

	@EventHandler
	public void onMessage(MessageReceivedEvent event) {
		System.out.println(event.getMessage().getChat().getIdentity());
		this.user.skypeMessageReceived(event);
	}

	public Chat getChatFromIdentity(String identity) {
		try {
			return skype.getOrLoadChat(identity);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public enum SkypeStatus {
		NO_ERROR,
		INVALID_CREDENTIALS,
		CONNECTION_ERROR,
		UNKNOWN_ERROR;
	}
}
