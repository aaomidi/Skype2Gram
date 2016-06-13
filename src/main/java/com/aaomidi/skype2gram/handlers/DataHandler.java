package com.aaomidi.skype2gram.handlers;

import com.aaomidi.skype2gram.Main;
import com.aaomidi.skype2gram.models.S2GUser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by amir on 2016-06-13.
 */
public class DataHandler extends TimerTask {
	@Getter
	private final Main instance;
	private final File directory;
	private final Gson gson;

	public DataHandler(Main instance) {
		this.instance = instance;
		gson = new GsonBuilder().setPrettyPrinting().create();
		String currentPath = System.getProperty("user.dir");
		currentPath = String.format("%s%sData%s", currentPath, File.separator, File.separator);

		directory = new File(currentPath);

		setupDirectory();
		readData();

		Timer timer = new Timer();
		timer.schedule(this, 10, 5 * 1000);
	}

	public void setupDirectory() {
		if (!directory.exists()) {
			if (!directory.mkdirs()) {
				throw new RuntimeException("Can not make directory!");
			}
		}
	}

	public void readData() {
		try {
			for (File file : directory.listFiles()) {
				if (file.getName().contains("!json")) continue;
				FileReader reader = new FileReader(file);
				S2GUser user = gson.fromJson(reader, S2GUser.class);
				if (user == null) {
					System.out.println("Something wrong happened when reading " + file.getName());
					continue;
				}
				instance.getUserHandler().registerUser(user);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void save() {
		try {
			Map<Long, S2GUser> users = instance.getUserHandler().getUsers();
			for (S2GUser user : users.values()) {
				File outputFile = new File(directory, user.getUserID() + ".json");

				FileWriter writer = new FileWriter(outputFile);
				String jsonString = gson.toJson(user, S2GUser.class);
				writer.write(jsonString);
				writer.flush();
				writer.close();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void run() {
		//new Thread(this::save).start();
		this.save();
	}
}
