package me.quickscythe.dragonforge;

import me.quickscythe.dragonforge.commands.CommandManager;
import me.quickscythe.dragonforge.listener.PlayerListener;
import me.quickscythe.dragonforge.utils.CoreUtils;
import me.quickscythe.dragonforge.utils.chat.Logger;
import me.quickscythe.dragonforge.utils.chat.MessageUtils;
import me.quickscythe.dragonforge.utils.storage.DataManager;
import org.bukkit.plugin.java.JavaPlugin;

import static net.kyori.adventure.text.Component.text;

public final class Core extends JavaPlugin {

    @Override
    public void onEnable() {
        CoreUtils.init(this);
        CommandManager.init();

        new PlayerListener(this);

        MessageUtils.addMessage("test.test", text("test: [0] [1]"));
        CoreUtils.logger().log(Logger.LogLevel.INFO, "Test", MessageUtils.getMessage("test.test", "hello", "world"));
    }

    @Override
    public void onDisable() {
        DataManager.end();
        MessageUtils.loadChangesToFile();
        // Plugin shutdown logic
    }
}
