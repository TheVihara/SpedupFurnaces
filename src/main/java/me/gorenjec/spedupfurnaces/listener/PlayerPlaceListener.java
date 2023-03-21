package me.gorenjec.spedupfurnaces.listener;

import me.gorenjec.spedupfurnaces.cache.InMemoryCache;
import me.gorenjec.spedupfurnaces.data.FurnacesFile;
import me.gorenjec.spedupfurnaces.models.CustomFurnace;
import me.gorenjec.spedupfurnaces.utils.NBTUtil;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerPlaceListener implements Listener {
    private final FurnacesFile furnacesFile;
    private final InMemoryCache cache;
    private final NBTUtil nbtUtil;

    public PlayerPlaceListener(FurnacesFile furnacesFile, InMemoryCache cache, NBTUtil nbtUtil) {
        this.furnacesFile = furnacesFile;
        this.cache = cache;
        this.nbtUtil = nbtUtil;
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        Block block = e.getBlockPlaced();
        if (cache.getInstance().hasGriefPrevention()) {
            String noBuildReason = GriefPrevention.instance.allowBuild(e.getPlayer(), block.getLocation());

            if (noBuildReason != null) {
                e.setCancelled(true);
                return;
            }
        }

        FileConfiguration furnacesConfig = furnacesFile.getConfig();
        ConfigurationSection furnacesSection = furnacesConfig.getConfigurationSection("furnaces");

        // Check if the block type is a valid furnace type
        if (furnacesSection == null || !furnacesSection.contains(block.getType().name().toLowerCase())) {
            return;
        }

        // Get the furnace level from the item NBT or default to 1
        ItemStack furnaceItem = e.getItemInHand();
        int level = nbtUtil.getInt(furnaceItem, "furnace-level");
        if (level == 0) {
            if (cache.getInstance().getConfig().getBoolean("settings.furnaces-worldwide")) {
                level = 1;
            } else {
                return;
            }
        }

        // Create and cache a custom furnace object
        CustomFurnace customFurnace = new CustomFurnace(block.getLocation(), block.getType(), level);
        cache.cacheFurnace(customFurnace);
        cache.addGui(customFurnace);
    }
}
