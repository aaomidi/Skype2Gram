package com.aaomidi.skype2gram.commands;

import com.aaomidi.skype2gram.Main;
import com.aaomidi.skype2gram.models.S2GUser;
import com.aaomidi.skype2gram.models.TelegramCommand;
import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;

/**
 * Created by amir on 2016-06-13.
 */
public class GroupChatsCommand extends TelegramCommand {
	public GroupChatsCommand(Main instance, String command, String description, boolean adminOnly) {
		super(instance, command, description, adminOnly);
	}

	@Override
	public void executeCommand(CommandMessageReceivedEvent event) {
		Chat chat = event.getChat();

		S2GUser user = getInstance().getUserHandler().getUser(event.getMessage().getSender().getId());
		if (user == null) {
			chat.sendMessage("You have not signed into skype. Please use /help to see the commands.");
			return;
		}

		if (event.getArgs().length == 0) {
			chat.sendMessage("Please enter /groupchats enable or /groupchats disable");
			return;
		}

		String answer = event.getArgs()[0];
		boolean enable;
		if (answer.equalsIgnoreCase("enable")) {
			enable = true;
		} else if (answer.equalsIgnoreCase("disable")) {
			enable = false;
		} else {
			chat.sendMessage("Please enter /groupchats enable or /groupchats disable");
			return;
		}

		user.setGroupChats(enable);
		chat.sendMessage("Group chats are " + ((enable) ? "enabled" : "disabled"));
	}
}
