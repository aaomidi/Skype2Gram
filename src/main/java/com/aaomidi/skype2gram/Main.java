package com.aaomidi.skype2gram;

import com.aaomidi.skype2gram.handlers.DataHandler;
import com.aaomidi.skype2gram.handlers.TelegramHandler;
import com.aaomidi.skype2gram.handlers.UserHandler;
import com.aaomidi.skype2gram.utils.PhotoUtils;
import lombok.Getter;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by amir on 2016-06-11.
 */
public class Main {
	private final Logger log = Logger.getLogger("Main");
	@Getter
	private DataHandler dataHandler;
	@Getter
	private TelegramHandler telegramHandler;
	@Getter
	private UserHandler userHandler;

	public Main(String... args) {
		if (args.length != 2) {
			throw new RuntimeException("Not enough arguments.");
		}
		String telegramAPIKey = args[0];
		String clientID = args[1];

		this.setupUserHandler();
		this.setupData();
		this.connectToTelegram(telegramAPIKey);
		this.connectToImgur(clientID);
	}

	public static void main(String... args) {
		new Main(args);
	}

	private void setupData() {
		log.log(Level.INFO, "Setting up data handler.");
		dataHandler = new DataHandler(this);
		log.log(Level.INFO, "\t... setup.");
	}

	private void connectToTelegram(String key) {
		log.log(Level.INFO, "Connecting to a Telegram.");
		telegramHandler = new TelegramHandler(key, this);
		log.log(Level.INFO, "\t... Connected.");
	}

	public void connectToImgur(String clientID) {
		log.log(Level.INFO, "Setting up imgur uploader.");
		PhotoUtils.setClientID(clientID);
		log.log(Level.INFO, "\t... Connected.");
	}

	public void setupUserHandler() {
		log.log(Level.INFO, "Setting up UserHandler.");
		userHandler = new UserHandler();
		log.log(Level.INFO, "\t... Done.");
	}
}
