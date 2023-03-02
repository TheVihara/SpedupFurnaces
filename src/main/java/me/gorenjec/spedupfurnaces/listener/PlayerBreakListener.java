package me.gorenjec.spedupfurnaces.listener;

import me.gorenjec.spedupfurnaces.cache.InMemoryCache;
import me.gorenjec.spedupfurnaces.data.FurnacesFile;
import me.gorenjec.spedupfurnaces.models.CustomFurnace;
import me.gorenjec.spedupfurnaces.utils.HexUtils;
import me.gorenjec.spedupfurnaces.utils.NBTUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.logging.Logger;

public class PlayerBreakListener implements Listener {
    private final FurnacesFile furnacesFile;
    private final InMemoryCache cache;
    private final NBTUtil nbtUtil;

    public PlayerBreakListener(FurnacesFile furnacesFile, InMemoryCache cache, NBTUtil nbtUtil) {
        this.furnacesFile = furnacesFile;
        this.cache = cache;
        this.nbtUtil = nbtUtil;
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Block block = e.getBlock();
        CustomFurnace customFurnace = cache.getFurnace(block.getLocation());
        if (customFurnace == null) {
            if (cache.getInstance().getConfig().getBoolean("settings.furnaces-worldwide")) {
                customFurnace = new CustomFurnace(block.getLocation(), block.getType(), 1);
                cache.cacheFurnace(customFurnace);
            } else {
                return;
            }
        }
        int level = customFurnace.getLevel();
        Material type = customFurnace.getMaterial();

        if (isValidFurnace(type)) {
            ItemStack item = cache.createItem(customFurnace, getSpeed(type, level), nbtUtil);

            e.setDropItems(false);
            block.getWorld().dropItemNaturally(block.getLocation(), item);
            cache.removeFurnace(block.getLocation());
        }
    }

    private boolean isValidFurnace(Material type) {
        FileConfiguration furnacesConfig = furnacesFile.getConfig();
        ConfigurationSection furnacesSection = furnacesConfig.getConfigurationSection("furnaces");

        if (furnacesSection == null) {
            Logger.getGlobal().severe("Furnaces section is null.");
            return false;
        }

        ConfigurationSection furnaceSection = furnacesSection.getConfigurationSection(type.name().toLowerCase());

        if (furnaceSection == null) {
            Logger.getGlobal().severe("Furnace section is null.");
            return false;
        } else {
            return true;
        }
    }

    private int getSpeed(Material type, int level) {
        FileConfiguration fileConfig = furnacesFile.getConfig();
        ConfigurationSection furnacesSection = fileConfig.getConfigurationSection("furnaces");

        if (furnacesSection == null) {
            Logger.getGlobal().severe("Furnaces section is null.");
            return 200;
        }

        ConfigurationSection furnaceSection = furnacesSection.getConfigurationSection(type.name().toLowerCase());

        if (furnaceSection == null) {
            Logger.getGlobal().severe("Furnace section is null.");
            return 200;
        }

        return furnaceSection.getInt("speed." + level);
    }
}
