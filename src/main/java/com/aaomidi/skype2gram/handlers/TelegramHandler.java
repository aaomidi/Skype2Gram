package com.aaomidi.skype2gram.handlers;

import com.aaomidi.skype2gram.Main;
import com.aaomidi.skype2gram.commands.*;
import com.aaomidi.skype2gram.models.S2GUser;
import com.aaomidi.skype2gram.models.TelegramCommand;
import lombok.Getter;
import pro.zackpollard.telegrambot.api.TelegramBot;
import pro.zackpollard.telegrambot.api.chat.CallbackQuery;
import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.event.Listener;
import pro.zackpollard.telegrambot.api.event.chat.CallbackQueryReceivedEvent;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;
import pro.zackpollard.telegrambot.api.event.chat.message.MessageReceivedEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by amir on 2016-06-11.
 */
public class TelegramHandler implements Listener {
	private static final Map<String, TelegramCommand> commands = new HashMap<>();

	private final Logger log = Logger.getLogger("Main");

	private final Main instance;
	private final String apiKey;
	@Getter
	private final TelegramBot bot;

	public TelegramHandler(String apiKey, Main instance) {
		this.apiKey = apiKey;
		this.instance = instance;
		bot = TelegramBot.login(apiKey);

		bot.startUpdates(true);
		bot.getEventsManager().register(this);
		registerCommands();
	}

	public static void registerCommand(TelegramCommand command) {
		commands.put(command.getCommand().toLowerCase(), command);
	}

	public void registerCommands() {
		new GroupChatsCommand(instance, "groupchats", "Enable or disable group chats in PM.", false);
		new HelpCommand(instance, "help", "Shows some help text.", false);
		new LoginCommand(instance, "login", "Logs you into skype if your account doesn't exist.", false);
		new LinkCommand(instance, "link", "Links the chat you are in with a skype chat.", false);
		new LoadChatsCommand(instance, "loadchats", "Loads more chats.", false);
		new ListChatsCommand(instance, "listchats", "Lists all the chats currently loaded.", false);
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		Chat chat = event.getChat();
		S2GUser user = instance.getUserHandler().getUser(event.getMessage().getSender().getId());
		if (user == null) {
			chat.sendMessage("You have not signed into skype. Please use /help to see the commands.");
			return;
		}

		user.telegramMessageReceived(event);
	}

	@Override
	public void onCallbackQueryReceivedEvent(CallbackQueryReceivedEvent event) {
		CallbackQuery query = event.getCallbackQuery();
		String uuid = query.getData();
		String data = CallbackCacheHandler.getData(uuid);
		String[] split = data.split("!");
		if (split.length == 0) {
			return;
		}
		switch (split[0].toLowerCase()) {
			case "linkchat": {
				if (split.length != 4) {
					return;
				}
				Long userID = Long.valueOf(split[1]);
				String chatID = split[2];
				String skypeChatIdentity = split[3];

				S2GUser user = instance.getUserHandler().getUser(userID);
				if (user == null) {
					throw new RuntimeException("Unexpected null for user when callback.");
				}
				user.getChatLinks().addLink(chatID, skypeChatIdentity);
				query.answer("Chat link created!", false);
				//bot.getChat(chatID).sendMessage("Chat link created!");
			}
		}
	}


	@Override
	public void onCommandMessageReceived(CommandMessageReceivedEvent event) {
		TelegramCommand command = commands.get(event.getCommand().toLowerCase());
		if (command == null) return;
		// Do admin only check.

		command.executeCommand(event);
	}
}
