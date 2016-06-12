package com.aaomidi.skype2gram.commands;

import com.aaomidi.skype2gram.Main;
import com.aaomidi.skype2gram.model.TelegramCommand;
import com.aaomidi.skype2gram.models.S2GUser;
import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;

/**
 * Created by amir on 2016-06-12.
 */
public class LinkCommand extends TelegramCommand {
	public LinkCommand(Main instance, String command, String description, boolean adminOnly) {
		super(instance, command, description, adminOnly);
	}

	@Override
	public void executeCommand(CommandMessageReceivedEvent event) {
		Chat chat = event.getChat();
		boolean works = false;
		switch (chat.getType()) {
			case GROUP:
			case SUPERGROUP:
				works = true;
				break;
		}

		if (!works) {
			chat.sendMessage("This feature is only active in groups.");
			return;
		}
		if (event.getArgs().length < 1) {
			chat.sendMessage("Please enter the ID of the chat you want to link.");
		}
		String chatID = event.getArgs()[0];

		S2GUser user = getInstance().getUserHandler().getUser(event.getMessage().getSender().getId());
		if (user == null) {
			chat.sendMessage("You have not signed into skype. Please use /help to see the commands.");
			return;
		}

		com.samczsun.skype4j.chat.Chat skypeChat = user.getSkypeHandler().getChatFromIdentity(chatID);
		if (skypeChat == null) {
			chat.sendMessage("Chat not found.");
			return;
		}
		user.getChatLinks().addLink(chat.getId(), skypeChat.getIdentity());

		chat.sendMessage("Link created.");
	}
}
