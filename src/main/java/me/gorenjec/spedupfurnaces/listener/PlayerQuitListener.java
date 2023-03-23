package me.gorenjec.spedupfurnaces.listener;

import me.gorenjec.spedupfurnaces.cache.InMemoryCache;
import me.gorenjec.spedupfurnaces.models.HoloTextDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {
    private InMemoryCache inMemoryCache;

    public PlayerQuitListener(InMemoryCache inMemoryCache) {
        this.inMemoryCache = inMemoryCache;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        for (HoloTextDisplay holoTextDisplay : inMemoryCache.getHoloTextDisplays()) {
            holoTextDisplay.unregister(player);
        }
    }
}
