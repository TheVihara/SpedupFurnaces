package me.gorenjec.spedupfurnaces.cache;

import io.github.rysefoxx.inventory.plugin.pagination.RyseInventory;
import me.gorenjec.spedupfurnaces.SpedupFurnaces;
import me.gorenjec.spedupfurnaces.data.CustomizationFile;
import me.gorenjec.spedupfurnaces.guis.FurnaceGui;
import me.gorenjec.spedupfurnaces.models.CustomFurnace;
import me.gorenjec.spedupfurnaces.storage.SQLStorage;
import me.gorenjec.spedupfurnaces.utils.HexUtils;
import me.gorenjec.spedupfurnaces.utils.NBTUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryCache {
    private SpedupFurnaces instance;
    private final CustomizationFile customizationFile;
    private Map<Location, CustomFurnace> furnaceMap = new HashMap<>();
    private Map<Location, CustomFurnace> updatedFurnaceMap = new HashMap<>();
    private Map<Location, CustomFurnace> addedFurnaceMap = new HashMap<>();
    private Map<Location, CustomFurnace> removedFurnaceMap = new HashMap<>();
    private Map<CustomFurnace, RyseInventory> furnaceGuiMap = new HashMap<>();

    public InMemoryCache(SpedupFurnaces instance, CustomizationFile customizationFile) {
        this.instance = instance;
        this.customizationFile = customizationFile;
        this.cacheInitialData();
        this.startTimedFlushing();
    }

    public void flush() {
        SQLStorage storage = instance.getStorage();

        updatedFurnaceMap.forEach((location, customFurnace) -> {
            storage.updateFurnace(customFurnace);
        });
        addedFurnaceMap.forEach((location, customFurnace) -> {
            storage.addFurnace(customFurnace);
            updatedFurnaceMap.put(location, customFurnace);
        });
        removedFurnaceMap.forEach((location, customFurnace) -> {
            storage.removeFurnace(location);
        });

        addedFurnaceMap.clear();
        removedFurnaceMap.clear();
    }

    private void startTimedFlushing() {
        new BukkitRunnable() {
            @Override
            public void run() {
                flush();
            }
        }.runTaskTimerAsynchronously(instance, 0L, 1200L);
    }

    private void cacheInitialData() {
        furnaceMap = instance.getStorage().getFurnaces();
        updatedFurnaceMap = furnaceMap;
    }

    public void cacheFurnace(CustomFurnace customFurnace) {
        addFurnace(customFurnace.getLocation(), customFurnace);
    }

    public void addGui(CustomFurnace customFurnace) {
        furnaceGuiMap.put(customFurnace, new FurnaceGui(instance, customFurnace).getGui());
    }

    public RyseInventory getGui(CustomFurnace customFurnace) {
        return this.furnaceGuiMap.get(customFurnace);
    }

    public CustomFurnace getFurnace(Location loc) {
        return furnaceMap.get(loc);
    }

    public ItemStack createItem(CustomFurnace customFurnace, int speed, NBTUtil nbtUtil) {
        ItemStack item = new ItemStack(customFurnace.getMaterial());
        ItemMeta itemMeta = item.getItemMeta();

        if (itemMeta == null) {
            return null;
        }

        FileConfiguration fileConfig = customizationFile.getConfig();

        List<String> lore = fileConfig.getStringList("items." + customFurnace.getMaterial().name().toLowerCase() + ".lore");
        List<String> newLore = new ArrayList<>();
        lore.forEach(s -> {
            newLore.add(HexUtils.colorify(s)
                    .replaceAll("%level%", String.valueOf(customFurnace.getLevel()))
                    .replaceAll("%duration%", String.valueOf(speed / 20)));
        });

        itemMeta.setDisplayName(HexUtils.colorify(fileConfig.getString("items." + customFurnace.getMaterial().name().toLowerCase() + ".name")
                .replaceAll("%level%", String.valueOf(customFurnace.getLevel()))
                .replaceAll("%duration%", String.valueOf(speed / 20))));
        itemMeta.setLore(newLore);
        itemMeta = nbtUtil.setInt(itemMeta, customFurnace.getLevel(), "furnace-level");
        item.setItemMeta(itemMeta);

        return item;
    }

    public void addFurnace(Location location, CustomFurnace customFurnace) {
        furnaceMap.put(location, customFurnace);
        addedFurnaceMap.put(location, customFurnace);
    }

    public void removeFurnace(Location location) {
        furnaceGuiMap.remove(furnaceMap.get(location));
        updatedFurnaceMap.remove(location);
        removedFurnaceMap.put(location, furnaceMap.get(location));
        addedFurnaceMap.remove(location);
        furnaceMap.remove(location);
    }

    public ItemStack createItem(Material material, int level, int amount, int speed, NBTUtil nbtUtil) {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta itemMeta = item.getItemMeta();

        if (itemMeta == null) {
            return null;
        }

        FileConfiguration fileConfig = customizationFile.getConfig();

        List<String> lore = fileConfig.getStringList("items." + material.name().toLowerCase() + ".lore");
        List<String> newLore = new ArrayList<>();
        lore.forEach(s -> {
            newLore.add(HexUtils.colorify(s)
                    .replaceAll("%level%", String.valueOf(level))
                    .replaceAll("%duration%", String.valueOf(speed / 20)));
        });

        itemMeta.setDisplayName(HexUtils.colorify(fileConfig.getString("items." + material.name().toLowerCase() + ".name"))
                .replaceAll("%level%", String.valueOf(level))
                .replaceAll("%duration%", String.valueOf(speed / 20)));
        itemMeta.setLore(newLore);
        itemMeta = nbtUtil.setInt(itemMeta, level, "furnace-level");
        item.setItemMeta(itemMeta);

        return item;
    }

    public SpedupFurnaces getInstance() {
        return instance;
    }
}
