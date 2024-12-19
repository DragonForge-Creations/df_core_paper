package me.quickscythe.dragonforge.utils.chat;


import json2.JSONObject;
import me.quickscythe.dragonforge.utils.CoreUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

public class MessageUtils {

    private static final File file = new File(CoreUtils.plugin().getDataFolder() + "/messages.json");
    private static JSONObject messages = new JSONObject();

    public static void start() {
        createDefaultMessages();
        StringBuilder data = new StringBuilder();
        try {
            if (!file.exists())
                CoreUtils.logger().log(Logger.LogLevel.INFO, "MessageUtils", "Creating file: " + file.createNewFile());
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                data.append(scanner.nextLine());
            }
            scanner.close();
        } catch (IOException e) {
            CoreUtils.logger().error("MessageUtils", e);
        }
        JSONObject loaded = data.toString().isEmpty() ? new JSONObject() : new JSONObject(data.toString());
        //messages = what is in memory
        //need to check what is in memory and hasn't been loaded then add it to file
        boolean discrepency = false;
        for(Map.Entry<String, Object> entry : messages.toMap().entrySet()){
            String key = entry.getKey();
            String text = (String) entry.getValue();
            if(!loaded.has(key)){
                discrepency = true;
                loaded.put(key,text);
            }
        }
        messages = loaded;
        if(discrepency) loadChangesToFile();
    }

    public static void loadChangesToFile() {
        try {
            FileWriter f2 = new FileWriter(file, false);
            f2.write(messages.toString(2));
            f2.close();
        } catch (IOException e) {
            CoreUtils.logger().error("MessageUtils", e);
        }
    }

    public static void addMessage(String key, String value){
        if(!messages.has(key)) messages.put(key, value);
    }

    private static void createDefaultMessages() {
        addMessage("cmd.error.no_player", "&cSorry \"[0]\" couldn't be find. If the player is offline their username must be typed exactly.");
        addMessage("cmd.error.no_perm", "&cSorry, you don't have the permission to run that command.");
        addMessage("gui.error.not_exist", "&cThere was an error opening that GUI. Does it exist?");
        addMessage("cmd.error.no_command", "&cSorry, couldn't find the command \"[0]\". Please check your spelling and try again.");
    }

    public static String getMessage(String key, Object... replacements){
        String a = getMessage(key);
        for(int i=0;i!=replacements.length;i++)
            a = a.replaceFirst("\\[" + i + "]", replacements[i].toString());
        return a;
    }

    private static String getMessage(String key) {
        return messages.has(key) ? messages.getString(key) : key;
    }
}
