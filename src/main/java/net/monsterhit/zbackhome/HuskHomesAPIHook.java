package net.monsterhit.zbackhome;

import net.william278.huskhomes.api.HuskHomesAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.UUID;

public class HuskHomesAPIHook implements Listener {
    public HuskHomesAPI huskHomesAPI;

    public HuskHomesAPIHook(ZBackHome plugin) {
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent e) {
        this.huskHomesAPI = HuskHomesAPI.getInstance();

        if (e.getMessage().contains("back")) {

            UUID userUUID = e.getPlayer().getUniqueId();
            e.getPlayer().sendMessage(userUUID.toString());
            Database database = Database.getInstance();
            String serverName = database.searchServerName(userUUID);

            if (serverName.equalsIgnoreCase("Dungeon") || serverName.equalsIgnoreCase("Farm")) {
                e.setCancelled(true);
                e.getPlayer().sendMessage("Non puoi ritornare alla tua ultima posizione nel server " + serverName);
            }
        }
    }
}
