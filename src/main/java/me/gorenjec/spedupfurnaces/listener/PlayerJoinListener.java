package me.gorenjec.spedupfurnaces.listener;

import me.gorenjec.spedupfurnaces.cache.InMemoryCache;
import me.gorenjec.spedupfurnaces.models.HoloTextDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
    private InMemoryCache inMemoryCache;

    public PlayerJoinListener(InMemoryCache inMemoryCache) {
        this.inMemoryCache = inMemoryCache;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        for (HoloTextDisplay holoTextDisplay : inMemoryCache.getHoloTextDisplays()) {
            holoTextDisplay.register(player);
        }
    }
}
