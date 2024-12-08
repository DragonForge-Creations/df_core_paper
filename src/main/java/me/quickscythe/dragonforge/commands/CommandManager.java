package me.quickscythe.dragonforge.commands;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.quickscythe.dragonforge.commands.executors.UpdateCommand;
import me.quickscythe.dragonforge.utils.CoreUtils;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CommandManager {
    public static void init() {

        new CommandBuilder(new UpdateCommand(CoreUtils.plugin())).setDescription("Test desc").setAliases("getnew").register();
//        new CommandBuilder(new ConfigCommand(ShadowUtils.getPlugin())).setDescription("Edit ShadowCore config files").register();
//        new CommandBuilder(new EntityCommand(ShadowUtils.getPlugin())).setDescription("Edit ShadowCore config files").setAliases("centity", "ientity", "ce", "ie").register();
    }

    public static class CommandBuilder {
        CustomCommand cmd;
        String desc = "";
        String[] aliases = new String[]{};


        @CheckReturnValue
        public CommandBuilder(CustomCommand executor) {
            this.cmd = executor;
        }

        @CheckReturnValue
        public CommandBuilder setDescription(String desc) {
            this.desc = desc;
            return this;
        }

        @CheckReturnValue
        public CommandBuilder setAliases(String... aliases) {
            this.aliases = aliases;
            return this;
        }

        public void register() {
            @NotNull LifecycleEventManager<Plugin> manager = cmd.getPlugin().getLifecycleManager();
            manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
                final Commands commands = event.registrar();
                commands.register(cmd.getNode(), desc, List.of(aliases));
            });
        }
    }
}
