package com.aaomidi.skype2gram.models;

import com.aaomidi.skype2gram.Main;
import com.aaomidi.skype2gram.handlers.TelegramHandler;
import lombok.Getter;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;

/**
 * Created by amir on 2016-06-11.
 */
public abstract class TelegramCommand {
	@Getter
	private final Main instance;
	@Getter
	private final String command;
	@Getter
	private final String description;
	@Getter
	private final boolean adminOnly;

	public TelegramCommand(Main instance, String command, String description, boolean adminOnly) {
		this.instance = instance;
		this.command = command;
		this.description = description;
		this.adminOnly = adminOnly;

		TelegramHandler.registerCommand(this);
	}

	public abstract void executeCommand(CommandMessageReceivedEvent event);
}
