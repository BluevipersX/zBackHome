package net.monsterhit.zbackhome;

import net.william278.huskhomes.api.HuskHomesAPI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class ZBackHome extends JavaPlugin {

    public HuskHomesAPIHook huskHomesHook;
    @Override
    public void onEnable() {
        if (Bukkit.getPluginManager().getPlugin("HuskHomes") != null) {
            this.huskHomesHook = new HuskHomesAPIHook(this);
            this.huskHomesHook.huskHomesAPI = HuskHomesAPI.getInstance();
            getServer().getPluginManager().registerEvents(huskHomesHook, this);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
