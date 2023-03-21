package me.gorenjec.spedupfurnaces.listener;

import me.gorenjec.spedupfurnaces.SpedupFurnaces;
import me.gorenjec.spedupfurnaces.cache.InMemoryCache;
import me.gorenjec.spedupfurnaces.data.CustomizationFile;
import me.gorenjec.spedupfurnaces.data.FurnacesFile;
import me.gorenjec.spedupfurnaces.models.CustomFurnace;
import me.gorenjec.spedupfurnaces.utils.NBTUtil;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.Objects;
import java.util.logging.Logger;

public class PlayerInteractListener implements Listener {
    private final FurnacesFile furnacesFile;
    private final SpedupFurnaces instance;
    private final InMemoryCache cache;

    public PlayerInteractListener(FurnacesFile furnacesFile, InMemoryCache cache) {
        this.furnacesFile = furnacesFile;
        this.cache = cache;
        this.instance = cache.getInstance();
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Block block = e.getClickedBlock();

        // Check for null block and return early
        if (block == null || !player.isSneaking() || e.getHand() == null || e.getHand() != EquipmentSlot.HAND || e.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        if (instance.hasGriefPrevention()) {
            String noBuildReason = GriefPrevention.instance.allowBuild(player, block.getLocation());
            if (noBuildReason != null) {
                e.setCancelled(true);
                return;
            }
        }

        Location loc = block.getLocation();
        CustomFurnace customFurnace = cache.getFurnace(loc);

        // Create new furnace if not cached and cache it
        if (customFurnace == null) {
            if (instance.getConfig().getBoolean("settings.furnaces-worldwide")) {
                customFurnace = new CustomFurnace(loc, block.getType(), 1);
                cache.cacheFurnace(customFurnace);
                cache.addGui(customFurnace);
            } else {
                return;
            }
        } else if (cache.getGui(customFurnace) == null) {
            cache.addGui(customFurnace);
        }

        FileConfiguration fileConfig = furnacesFile.getConfig();
        ConfigurationSection furnacesSection = fileConfig.getConfigurationSection("furnaces");

        // Log error and return early if furnaces section is null
        if (furnacesSection == null) {
            Logger.getGlobal().severe("Furnaces section is null.");
            return;
        }

        ConfigurationSection furnaceSection = furnacesSection.getConfigurationSection(block.getType().name().toLowerCase());

        // Log error and return early if furnace section is null
        if (furnaceSection == null) {
            Logger.getGlobal().severe("Furnace section is null.");
            return;
        }

        e.setCancelled(true);
        cache.getGui(customFurnace).open(player);
    }
}
