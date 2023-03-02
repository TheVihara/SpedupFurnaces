package me.gorenjec.spedupfurnaces.utils;

import me.gorenjec.spedupfurnaces.SpedupFurnaces;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class NBTUtil {
    private SpedupFurnaces instance;

    public NBTUtil(SpedupFurnaces instance) {
        this.instance = instance;
    }

    public Integer getInt(ItemStack item, String dataContainer){
        NamespacedKey key = new NamespacedKey(instance, dataContainer);
        ItemMeta itemMeta = item.getItemMeta();
        PersistentDataContainer tagContainer = itemMeta.getPersistentDataContainer();
        if(tagContainer.has(key, PersistentDataType.INTEGER)) {
            int foundValue = tagContainer.get(key, PersistentDataType.INTEGER);
            return foundValue;
        }
        return 0;
    }

    public String getString(ItemStack item, String dataContainer){
        NamespacedKey key = new NamespacedKey(instance, dataContainer);
        ItemMeta itemMeta = item.getItemMeta();
        PersistentDataContainer tagContainer = itemMeta.getPersistentDataContainer();
        if(tagContainer.has(key, PersistentDataType.STRING)) {
            String foundValue = tagContainer.get(key, PersistentDataType.STRING);
            return foundValue;
        }
        return "none";
    }

    public ItemMeta setInt(ItemMeta meta, int number, String dataContainer){
        NamespacedKey key = new NamespacedKey(instance, dataContainer);
        try {
            PersistentDataContainer tagContainer = meta.getPersistentDataContainer();
            tagContainer.set(key, PersistentDataType.INTEGER, number);
        }catch (NullPointerException error){
            error.getCause();
            error.fillInStackTrace();
        }
        return meta;
    }

    public ItemMeta setString(ItemMeta meta, String string, String dataContainer){
        NamespacedKey key = new NamespacedKey(instance, dataContainer);
        try {
            PersistentDataContainer tagContainer = meta.getPersistentDataContainer();
            tagContainer.set(key, PersistentDataType.STRING, string);
        }catch (NullPointerException error){
            error.getCause();
            error.fillInStackTrace();
        }
        return meta;
    }
}
