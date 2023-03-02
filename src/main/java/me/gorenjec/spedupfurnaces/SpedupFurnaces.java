package me.gorenjec.spedupfurnaces;

import io.github.rysefoxx.inventory.plugin.pagination.InventoryManager;
import me.gorenjec.spedupfurnaces.cache.InMemoryCache;
import me.gorenjec.spedupfurnaces.commands.CommandHandler;
import me.gorenjec.spedupfurnaces.commands.furnace.GiveSubCommand;
import me.gorenjec.spedupfurnaces.commands.furnace.HelpSubCommand;
import me.gorenjec.spedupfurnaces.commands.furnace.ReloadSubCommand;
import me.gorenjec.spedupfurnaces.commands.furnace.SpedupFurnaceCommand;
import me.gorenjec.spedupfurnaces.data.CustomizationFile;
import me.gorenjec.spedupfurnaces.data.FurnacesFile;
import me.gorenjec.spedupfurnaces.listener.FurnaceStartSmeltListener;
import me.gorenjec.spedupfurnaces.listener.PlayerBreakListener;
import me.gorenjec.spedupfurnaces.listener.PlayerInteractListener;
import me.gorenjec.spedupfurnaces.listener.PlayerPlaceListener;
import me.gorenjec.spedupfurnaces.storage.SQLStorage;
import me.gorenjec.spedupfurnaces.utils.NBTUtil;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class SpedupFurnaces extends JavaPlugin {
    private FurnacesFile furnacesFile;
    private CustomizationFile customizationFile;
    private NBTUtil nbtUtil;
    private InMemoryCache cache;
    private CommandHandler commandHandler;
    private SQLStorage storage;
    private InventoryManager manager;
    private static Economy econ = null;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        if (!setupEconomy()) {
            getLogger().severe("Disabled due to no Vault dependency found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.storage = new SQLStorage(this);
        this.furnacesFile = new FurnacesFile(this);
        this.customizationFile = new CustomizationFile(this);
        this.nbtUtil = new NBTUtil(this);
        this.commandHandler = new CommandHandler(this);
        this.manager = new InventoryManager(this);
        manager.invoke();
        this.cache = new InMemoryCache(this, customizationFile);

        this.registerListeners();
        this.registerCommands();
    }

    @Override
    public void onDisable() {
        cache.flush();
    }

    public boolean setupEconomy(){
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    private void registerListeners() {
        PluginManager pM = getServer().getPluginManager();
        pM.registerEvents(new PlayerPlaceListener(furnacesFile, cache, nbtUtil), this);
        pM.registerEvents(new FurnaceStartSmeltListener(cache, furnacesFile), this);
        pM.registerEvents(new PlayerBreakListener(furnacesFile, cache, nbtUtil), this);
        pM.registerEvents(new PlayerInteractListener(furnacesFile, cache), this);
    }

    public void registerCommands() {
        CommandHandler.register(
                new SpedupFurnaceCommand(),
                new HelpSubCommand(),
                new GiveSubCommand(furnacesFile, cache, nbtUtil),
                new ReloadSubCommand(furnacesFile, customizationFile, cache)
        );
    }

    public CustomizationFile getCustomizationFile() {
        return customizationFile;
    }

    public static Economy getEcon() {
        return econ;
    }

    public FurnacesFile getFurnacesFile() {
        return furnacesFile;
    }

    public SQLStorage getStorage() {
        return storage;
    }
}
