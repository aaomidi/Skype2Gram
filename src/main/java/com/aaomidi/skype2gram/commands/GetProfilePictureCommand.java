package com.aaomidi.skype2gram.commands;

import com.aaomidi.skype2gram.Main;
import com.aaomidi.skype2gram.model.TelegramCommand;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;

/**
 * Created by amir on 2016-06-12.
 */
public class GetProfilePictureCommand extends TelegramCommand {
	public GetProfilePictureCommand(Main instance, String command, String description, boolean adminOnly) {
		super(instance, command, description, adminOnly);
	}

	@Override
	public void executeCommand(CommandMessageReceivedEvent event) {

	}
}
