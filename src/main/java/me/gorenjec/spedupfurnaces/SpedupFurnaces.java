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
import me.gorenjec.spedupfurnaces.listener.*;
import me.gorenjec.spedupfurnaces.models.DisplayPacket;
import me.gorenjec.spedupfurnaces.storage.SQLStorage;
import me.gorenjec.spedupfurnaces.utils.NBTUtil;
import net.milkbowl.vault.economy.Economy;
import net.minecraft.world.phys.Vec3;
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
    private DisplayPacket displayPacket;
    private static Economy econ = null;
    private boolean griefPrevention;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        if (!setupEconomy()) {
            getLogger().severe("Disabled due to no Vault dependency found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.griefPrevention = setupGriefPrevention();
        this.storage = new SQLStorage(this);
        this.furnacesFile = new FurnacesFile(this);
        this.customizationFile = new CustomizationFile(this);
        this.nbtUtil = new NBTUtil(this);
        this.displayPacket = new DisplayPacket();
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
        pM.registerEvents(new PlayerJoinListener(cache), this);
        pM.registerEvents(new PlayerQuitListener(cache), this);
    }

    public boolean setupGriefPrevention() {
        return getServer().getPluginManager().getPlugin("GriefPrevention") != null;
    }

    public void registerCommands() {
        CommandHandler.register(
                new SpedupFurnaceCommand(),
                new HelpSubCommand(),
                new GiveSubCommand(furnacesFile, cache, nbtUtil),
                new ReloadSubCommand(furnacesFile, customizationFile, cache)
        );
    }

    public boolean hasGriefPrevention() {
        return griefPrevention;
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

    public InMemoryCache getCache() {
        return cache;
    }

    public DisplayPacket getDisplayPacket() {
        return displayPacket;
    }
}
