package me.gorenjec.spedupfurnaces.listener;

import me.gorenjec.spedupfurnaces.cache.InMemoryCache;
import me.gorenjec.spedupfurnaces.data.FurnacesFile;
import me.gorenjec.spedupfurnaces.models.CustomFurnace;
import me.gorenjec.spedupfurnaces.models.HoloTextDisplay;
import net.minecraft.world.entity.Display;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceStartSmeltEvent;
import org.bukkit.util.Vector;

import java.util.logging.Logger;

public class FurnaceStartSmeltListener implements Listener {
    private final InMemoryCache cache;
    private final FurnacesFile furnacesFile;

    public FurnaceStartSmeltListener(InMemoryCache cache, FurnacesFile furnacesFile) {
        this.cache = cache;
        this.furnacesFile = furnacesFile;
    }

    @EventHandler
    public void onSmeltStart(FurnaceStartSmeltEvent e) {
        Block block = e.getBlock();
        CustomFurnace customFurnace = cache.getFurnace(block.getLocation());
        if (customFurnace == null) {
            if (cache.getInstance().getConfig().getBoolean("settings.furnaces-worldwide")) {
                customFurnace = cache.getFurnace(block.getLocation(), 1);
                cache.cacheFurnace(customFurnace);
            } else {
                return;
            }
        }
        int speed = getSpeed(block.getType(), customFurnace.getLevel());

        if (speed > 0) {
            e.setTotalCookTime(speed);
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
