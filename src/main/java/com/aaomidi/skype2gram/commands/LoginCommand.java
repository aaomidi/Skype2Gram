package com.aaomidi.skype2gram.commands;

import com.aaomidi.skype2gram.Main;
import com.aaomidi.skype2gram.handlers.SkypeHandler;
import com.aaomidi.skype2gram.models.LoginInfo;
import com.aaomidi.skype2gram.models.S2GUser;
import com.aaomidi.skype2gram.models.TelegramCommand;
import com.aaomidi.skype2gram.utils.Lang;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;
import pro.zackpollard.telegrambot.api.user.User;

/**
 * Created by amir on 2016-06-11.
 */
public class LoginCommand extends TelegramCommand {
	public LoginCommand(Main instance, String command, String description, boolean adminOnly) {
		super(instance, command, description, adminOnly);
	}

	@Override
	public void executeCommand(CommandMessageReceivedEvent event) {
		User user = event.getMessage().getSender();
		String username = event.getArgs()[0];
		String password = event.getArgs()[1];

		event.getChat().sendMessage("Logging you in...");

		S2GUser s2GUser = new S2GUser(getInstance(),user.getId(), LoginInfo.builder().username(username).password(password).build());

		SkypeHandler.SkypeStatus status = s2GUser.getSkypeHandler().login();
		switch (status) {
			case NO_ERROR:
				getInstance().getUserHandler().registerUser(s2GUser);
				event.getChat().sendMessage("\tSuccessfully connected your Skype account!");
				break;
			case INVALID_CREDENTIALS:
				event.getChat().sendMessage("\tIncorrect login info.");
				break;
			case CONNECTION_ERROR:
				event.getChat().sendMessage("\tConnection error.");
				break;
			case UNKNOWN_ERROR:
				event.getChat().sendMessage("\tUnknown error. Contact " + Lang.MAINTAINER);
				break;
		}
	}
}
