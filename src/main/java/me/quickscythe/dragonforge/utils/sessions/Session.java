package me.quickscythe.dragonforge.utils.sessions;

import json2.JSONObject;
import me.quickscythe.dragonforge.utils.CoreUtils;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class Session {

    private final JSONObject data = new JSONObject();
    private Optional<Runnable> onEnd = Optional.empty();
    private Optional<ItemStack> reward = Optional.empty();
    private Optional<Advancement> task = Optional.empty();
    private long startTime = 0;

    public Session() {
    }

    public Session start() {
        startTime = System.currentTimeMillis();
        return this;
    }

    public long started() {
        return startTime;
    }

    public Session end() {
        if(onEnd.isPresent()) onEnd.get().run();
        System.out.println("Session ended");
        System.out.println(data.toString(2));
//        onEnd.ifPresent(Runnable::run);
        return this;
    }

    public Session end(@NotNull Runnable onEnd) {
        this.onEnd = Optional.of(onEnd);
        return this;
    }

    public Session reward(@NotNull ItemStack reward) {
        this.reward = Optional.of(reward);
        return this;
    }

    public Optional<ItemStack> reward() {
        return reward;
    }

    public Session task(@NotNull Advancement task) {
        this.task = Optional.of(task);
        return this;
    }

    public Optional<Advancement> task() {
        return task;
    }


    public Session track(@NotNull Player player) {
        if (!data.has(String.valueOf(player.getUniqueId()))) {
            data.put(String.valueOf(player.getUniqueId()), new JSONObject());
        }
        return this;
    }

    public JSONObject data(Player player) {
        return data.getJSONObject(String.valueOf(player.getUniqueId()));
    }

    public JSONObject data(){
        return data;
    }
}
