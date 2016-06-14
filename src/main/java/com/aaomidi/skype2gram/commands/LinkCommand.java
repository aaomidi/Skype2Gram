package com.aaomidi.skype2gram.commands;

import com.aaomidi.skype2gram.Main;
import com.aaomidi.skype2gram.handlers.CallbackCacheHandler;
import com.aaomidi.skype2gram.models.S2GUser;
import com.aaomidi.skype2gram.models.TelegramCommand;
import com.samczsun.skype4j.Skype;
import com.samczsun.skype4j.chat.GroupChat;
import com.samczsun.skype4j.chat.IndividualChat;
import org.apache.commons.lang3.RandomStringUtils;
import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;
import pro.zackpollard.telegrambot.api.keyboards.InlineKeyboardButton;
import pro.zackpollard.telegrambot.api.keyboards.InlineKeyboardMarkup;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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

		S2GUser user = getInstance().getUserHandler().getUser(event.getMessage().getSender().getId());
		if (user == null) {
			chat.sendMessage("You have not signed into skype. Please use /help to see the commands.");
			return;
		}

		/* if (user.getChatLinks().getSkype(chat.getId()) != null) {
			chat.sendMessage("This chat is already linked to another chat.");
			return;
		} */

		Skype skype = user.getSkypeHandler().getSkype();
		Map<String, String> chats = new LinkedHashMap<>();
		List<InlineKeyboardButton> buttons = new LinkedList<>();
		int i = 0;
		for (com.samczsun.skype4j.chat.Chat skypeChat : skype.getAllChats()) {
			if (i++ > 9) {
				break;
			}

			String uuid = RandomStringUtils.randomAlphanumeric(10);
			String data = "LinkChat" + "!" + event.getMessage().getSender().getId() + "!" + chat.getId() + "!" + skypeChat.getIdentity();
			System.out.println(data);

			CallbackCacheHandler.storeData(uuid, data);
			try {
				skype.getOrLoadChat(skypeChat.getIdentity());
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (skypeChat instanceof GroupChat) {
				String chatName = ((GroupChat) skypeChat).getTopic();
				if (chatName == null || chatName.isEmpty()) {
					chatName = skypeChat.getIdentity().substring(3, 10);
				}

				chats.put(((GroupChat) skypeChat).getTopic(), skypeChat.getIdentity());
				buttons.add(InlineKeyboardButton.builder()
						.callbackData(uuid)
						.text(chatName)
						.build());
			} else if (skypeChat instanceof IndividualChat) {
				chats.put(skypeChat.getIdentity().substring(2), skypeChat.getIdentity());

				buttons.add(InlineKeyboardButton.builder()
						.callbackData(uuid)
						.text(skypeChat.getIdentity().substring(2))
						.build());
			}
		}

		InlineKeyboardMarkup.InlineKeyboardMarkupBuilder builder = InlineKeyboardMarkup.builder();
		for (InlineKeyboardButton b : buttons) {
			builder.addRow(b);
		}

		chat.sendMessage(SendableTextMessage.builder().message("Select a chat:").replyMarkup(builder.build()).build());
	}
}
