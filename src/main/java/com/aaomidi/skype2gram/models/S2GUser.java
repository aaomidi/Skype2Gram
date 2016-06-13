package com.aaomidi.skype2gram.models;

import com.aaomidi.skype2gram.Main;
import com.aaomidi.skype2gram.handlers.SkypeHandler;
import com.aaomidi.skype2gram.utils.PhotoUtils;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.samczsun.skype4j.chat.GroupChat;
import com.samczsun.skype4j.chat.IndividualChat;
import com.samczsun.skype4j.chat.messages.ReceivedMessage;
import com.samczsun.skype4j.events.chat.message.MessageReceivedEvent;
import com.samczsun.skype4j.user.User;
import lombok.Getter;
import lombok.Setter;
import pro.zackpollard.telegrambot.api.TelegramBot;
import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.chat.message.Message;
import pro.zackpollard.telegrambot.api.chat.message.content.PhotoContent;
import pro.zackpollard.telegrambot.api.chat.message.content.StickerContent;
import pro.zackpollard.telegrambot.api.chat.message.content.TextContent;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class S2GUser {
	private final transient Logger log = Logger.getLogger("Main");
	@Getter
	private final long userID;
	private final LoginInfo skypeLoginInfo;
	@Getter
	private final ChatLinks chatLinks;
	@Getter
	private final MessageLinks messageLinks;
	@Setter
	private transient Main instance;
	private transient SkypeHandler skypeHandler;

	public S2GUser(Main instance, long userID, LoginInfo skypeLoginInfo) {
		this.instance = instance;
		this.userID = userID;
		this.skypeLoginInfo = skypeLoginInfo;

		chatLinks = new ChatLinks();
		messageLinks = new MessageLinks();

		skypeHandler = new SkypeHandler(skypeLoginInfo, this);
	}

	public SkypeHandler getSkypeHandler() {
		if (skypeHandler != null) {
			return skypeHandler;
		}
		skypeHandler = new SkypeHandler(skypeLoginInfo, this);
		skypeHandler.login();
		return skypeHandler;
	}

	public void skypeMessageReceived(MessageReceivedEvent event) {
		ReceivedMessage message = event.getMessage();
		String text = message.getContent().asPlaintext();
		User sender = message.getSender();

		sendToTelegram(sender, text, event.getChat());
	}

	public void telegramMessageReceived(pro.zackpollard.telegrambot.api.event.chat.message.MessageReceivedEvent event) {
		Chat chat = event.getChat();
		String chatID = chat.getId();
		switch (chat.getType()) {
			case PRIVATE:
				handlePMs(event);
				break;
			case GROUP:
			case SUPERGROUP:
				handleGroups(event);
				break;
			default:
				//nothing
		}
	}

	private void handlePMs(pro.zackpollard.telegrambot.api.event.chat.message.MessageReceivedEvent event) {
		Chat chat = event.getChat();
		Message message = event.getMessage();
		if (getChatLinks().getSkype(chat.getId()) != null) { // If a manual link has been made
			handleGroups(event);
			return;
		}

		if (message.getRepliedTo() == null) {
			chat.sendMessage("You need to reply to a message.");
			return;
		}
		message = message.getRepliedTo();

		String skypeChatID = getMessageLinks().getSkypeChat(String.valueOf(message.getMessageId()));
		if (skypeChatID == null) {
			chat.sendMessage("Could not find a chat with that message. Sorry");
			return;
		}
		com.samczsun.skype4j.chat.Chat c = getSkypeHandler().getChatFromIdentity(skypeChatID);

		sendMessageToSkype(event, c);

	}

	private void handleGroups(pro.zackpollard.telegrambot.api.event.chat.message.MessageReceivedEvent event) {
		Chat chat = event.getChat();
		String chatID = chat.getId();
		String skypeChatID = getChatLinks().getSkype(chatID);
		if (skypeChatID == null) {
			return;
		}
		System.out.println(skypeChatID);
		com.samczsun.skype4j.chat.Chat c = getSkypeHandler().getChatFromIdentity(skypeChatID);
		sendMessageToSkype(event, c);
	}

	public void sendMessageToSkype(pro.zackpollard.telegrambot.api.event.chat.message.MessageReceivedEvent event, com.samczsun.skype4j.chat.Chat c) {
		Chat chat = event.getChat();
		switch (event.getContent().getType()) {
			case PHOTO: {
				PhotoContent content = (PhotoContent) event.getContent();
				File file = PhotoUtils.downloadFile(PhotoUtils.getLargestPhoto(content), getBot());
				try {
					String url = PhotoUtils.uploadToImgur(file);
					try {
						c.sendMessage(url);

						if (content.getCaption() != null && !content.getCaption().isEmpty()) {
							c.sendMessage("Caption for link above: " + content.getCaption());
						}
					} catch (Exception e) {
						e.printStackTrace();
						chat.sendMessage("Error happened: " + e.getMessage());
					}
				} catch (UnirestException e) {
					e.printStackTrace();
				}
				break;
			}
			case STICKER: {
				StickerContent content = (StickerContent) event.getContent();

				try {
					c.sendMessage(String.format("Sent a %s sticker: %s", content.getContent().getEmoji(), content.getContent().getFileDownloadLink(getBot())));
				} catch (Exception e) {
					e.printStackTrace();
					chat.sendMessage("Error happened: " + e.getMessage());
				}
				break;
			}
			case TEXT: {
				TextContent content = (TextContent) event.getContent();
				if (content.getContent().startsWith("/")) break;
				try {
					c.sendMessage(content.getContent());
				} catch (Exception e) {
					e.printStackTrace();
					chat.sendMessage("Error happened: " + e.getMessage());
				}
				break;
			}

		}
	}

	private void sendToTelegram(User user, String text, com.samczsun.skype4j.chat.Chat chat) {
		boolean handleAsGroup = false;
		if (chat instanceof IndividualChat) {
			Chat c = getChat();
			String chatIdentity = chat.getIdentity();
			String telegramChatID = chatLinks.getTelegram(chatIdentity);
			if (telegramChatID != null) {
				handleAsGroup = true; // If manual link is made
			} else {
				try {
					Message m = c.sendMessage(String.format("%s (%s): %s", user.getDisplayName(), user.getUsername(), text));
					messageLinks.addLink(String.valueOf(m.getMessageId()), chat.getIdentity());
				} catch (Exception e) {
					c.sendMessage("Something wrong happened when getting a message.");
				}
			}
		}
		if (chat instanceof GroupChat || handleAsGroup) {
			String chatIdentity = chat.getIdentity();
			String telegramChatID = chatLinks.getTelegram(chatIdentity);
			Chat teleChat = getChat(telegramChatID);

			if (teleChat == null) {
				log.log(Level.SEVERE, "Chat was null.");
				return;
			}

			try {
				teleChat.sendMessage(String.format("%s (%s): %s", user.getDisplayName(), user.getUsername(), text));
			} catch (Exception e) {
				teleChat.sendMessage("Something wrong happened when getting a message.");
			}
		}
	}


	private TelegramBot getBot() {
		return instance.getTelegramHandler().getBot();
	}

	private Chat getChat() {
		return getChat(String.valueOf(userID));
	}

	private Chat getChat(String id) {
		return getBot().getChat(id);
	}
}
