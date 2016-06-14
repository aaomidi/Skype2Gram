package com.aaomidi.skype2gram.commands;

import com.aaomidi.skype2gram.Main;
import com.aaomidi.skype2gram.models.S2GUser;
import com.aaomidi.skype2gram.models.TelegramCommand;
import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;

import java.util.Collection;

/**
 * Created by amir on 2016-06-13.
 */
public class ListChatsCommand extends TelegramCommand {
	public ListChatsCommand(Main instance, String command, String description, boolean adminOnly) {
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

		Collection<com.samczsun.skype4j.chat.Chat> chats = user.getSkypeHandler().getSkype().getAllChats();

		StringBuilder sb = new StringBuilder("Chats: \n");
		for (com.samczsun.skype4j.chat.Chat c : chats) {
			sb.append(c.getIdentity() + "\n");
		}

		chat.sendMessage(sb.toString());
	}
}
