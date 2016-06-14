package com.aaomidi.skype2gram.commands;

import com.aaomidi.skype2gram.Main;
import com.aaomidi.skype2gram.model.TelegramCommand;
import com.aaomidi.skype2gram.models.S2GUser;
import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;

/**
 * Created by amir on 2016-06-13.
 */
public class LoadChatsCommand extends TelegramCommand {
	public LoadChatsCommand(Main instance, String command, String description, boolean adminOnly) {
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
		try {
			user.getSkypeHandler().getSkype().loadMoreChats(10);
			chat.sendMessage("10 more chats loaded.");
		} catch (Exception e) {
			e.printStackTrace();
			chat.sendMessage("Unable to load more chats.");
		}
	}
}
