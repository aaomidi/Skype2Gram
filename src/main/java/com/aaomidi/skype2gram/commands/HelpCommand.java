package com.aaomidi.skype2gram.commands;

import com.aaomidi.skype2gram.Main;
import com.aaomidi.skype2gram.model.TelegramCommand;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;

/**
 * Created by amir on 2016-06-13.
 */
public class HelpCommand extends TelegramCommand {
	public HelpCommand(Main instance, String command, String description, boolean adminOnly) {
		super(instance, command, description, adminOnly);
	}

	@Override
	public void executeCommand(CommandMessageReceivedEvent event) {
		event.getChat().sendMessage("Hey there!\n" +
				"To use the bot you do the following: /login [username] [password]\n" +
				"All private messages will be received in a private chat with the bot.\n" +
				"Group chats will be ignored by default (unless you enable them with /groupchats enable).\n" +
				"\n" +
				"If you make a group with yourself and the bot, you can /link [chatID] so that the messages for that specific conversation gets sent to that specific group.\n" +
				"\n" +
				"To get the \"ChatID\" of the specific chat you're after, skype is pretty stupid so I can't show you the chat ID and the chat name at the current moment. So you're just going to have to either figure it out from /listchats or turn on group chats and see what messages are sent from what group.\n");
	}
}
