package com.aaomidi.skype2gram.utils;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.Setter;
import pro.zackpollard.telegrambot.api.TelegramBot;
import pro.zackpollard.telegrambot.api.chat.message.content.PhotoContent;
import pro.zackpollard.telegrambot.api.chat.message.content.type.PhotoSize;

import java.io.File;
import java.util.UUID;

/**
 * Created by amir on 2016-06-11.
 */
public class PhotoUtils {
	private static final String BASE_URL = "https://api.imgur.com/3/image.json";
	@Setter
	private static String clientID;

	public static PhotoSize getLargestPhoto(PhotoContent x) {
		PhotoSize largest = null;
		int m = -1; // MAX
		for (PhotoSize p : x.getContent()) {
			int c = getDimensions(p); // CURRENT

			if (largest == null || c > m) {
				largest = p;
				m = c;
			}
		}
		return largest;
	}

	public static int getDimensions(PhotoSize p) {
		return p.getHeight() * p.getWidth();
	}

	public static File downloadFile(pro.zackpollard.telegrambot.api.chat.message.content.type.File f, TelegramBot bot) {
		String path = System.getProperty("java.io.tempdir");
		File dir = new File(path, "images/");
		dir.mkdir();

		String randomString = UUID.randomUUID().toString();
		File file = new File(dir, randomString + ".png");

		f.downloadFile(bot, file);

		return file;
	}

	public static String uploadToImgur(File file) throws UnirestException {
		HttpResponse<JsonNode> response = Unirest
				.post(BASE_URL)
				.header("Authorization", "Client-ID " + clientID)
				.field("image", file)
				.asJson();

		return (String) response.getBody().getObject().getJSONObject("data").get("link");
	}

}
