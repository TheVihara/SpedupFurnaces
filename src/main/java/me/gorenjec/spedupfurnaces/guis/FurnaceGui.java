package me.gorenjec.spedupfurnaces.guis;

import io.github.rysefoxx.inventory.plugin.content.IntelligentItem;
import io.github.rysefoxx.inventory.plugin.content.IntelligentItemError;
import io.github.rysefoxx.inventory.plugin.content.InventoryContents;
import io.github.rysefoxx.inventory.plugin.content.InventoryProvider;
import io.github.rysefoxx.inventory.plugin.pagination.RyseInventory;
import me.gorenjec.spedupfurnaces.SpedupFurnaces;
import me.gorenjec.spedupfurnaces.data.CustomizationFile;
import me.gorenjec.spedupfurnaces.data.FurnacesFile;
import me.gorenjec.spedupfurnaces.models.CustomFurnace;
import me.gorenjec.spedupfurnaces.utils.HexUtils;
import me.gorenjec.spedupfurnaces.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;

public class FurnaceGui {
    private final SpedupFurnaces instance;
    private RyseInventory gui;
    private final FurnacesFile furnacesFile;
    private final CustomizationFile customizationFile;

    public FurnaceGui(SpedupFurnaces instance, CustomFurnace customFurnace) {
        this.instance = instance;
        this.furnacesFile = instance.getFurnacesFile();
        this.customizationFile = instance.getCustomizationFile();

        this.createInventory(customFurnace);
    }

    public void createInventory(CustomFurnace customFurnace) {
        this.gui = RyseInventory.builder()
                .title("")
                .size(27)
                .provider(new InventoryProvider() {

                    @Override
                    public void update(Player player, InventoryContents contents) {
                        ItemStack furnaceItem = new ItemBuilder(customFurnace.getMaterial())
                                .displayName(Objects.requireNonNull(getcConfig().getString("gui.preview.furnace_name"))
                                        .replaceAll("%level%", String.valueOf(customFurnace.getLevel()))
                                        .replaceAll("%duration%", String.valueOf(getSpeed() / 20))
                                        .replaceAll("%next_level%", getNextLevel())
                                        .replaceAll("%duration_next%", getNextSpeed()), true)
                                .lore(getFurnaceLore(), true)
                                .build();

                        ItemStack upgradeItem = new ItemBuilder(Material.EMERALD)
                                .displayName(Objects.requireNonNull(getcConfig().getString("gui.upgrade.name"))
                                        .replaceAll("%level%", String.valueOf(customFurnace.getLevel()))
                                        .replaceAll("%duration%", String.valueOf(getSpeed() / 20))
                                        .replaceAll("%next_level%", getNextLevel())
                                        .replaceAll("%duration_next%", getNextSpeed()), true)
                                .lore(getUpgradeLore(), true)
                                .build();

                        IntelligentItem upgradeInteractiveItem = !Objects.equals(getNextMoneyCost(), "MAX") ? IntelligentItem.of(upgradeItem, event -> {
                            if (player.getLevel() >= Integer.parseInt(getNextExpCost()) && SpedupFurnaces.getEcon().has(player, Integer.parseInt(getNextMoneyCost()))) {
                                player.setLevel(player.getLevel() - Integer.parseInt(getNextExpCost()));
                                SpedupFurnaces.getEcon().withdrawPlayer(player, Integer.parseInt(getNextMoneyCost()));
                                customFurnace.addLevel(1);
                                player.sendMessage(HexUtils.colorify(getcConfig().getString("messages.bought-message")));
                                update(player, contents);
                            } else if (player.getLevel() < Integer.parseInt(getNextExpCost())) {
                                if (!SpedupFurnaces.getEcon().has(player, Integer.parseInt(getNextMoneyCost()))) {
                                    player.sendMessage(HexUtils.colorify(getcConfig().getString("messages.not-enough-money-and-exp")));
                                    return;
                                }
                                player.sendMessage(HexUtils.colorify(getcConfig().getString("messages.not-enough-exp")));
                            } else if (!SpedupFurnaces.getEcon().has(player, Integer.parseInt(getNextMoneyCost()))) {
                                player.sendMessage(HexUtils.colorify(getcConfig().getString("messages.not-enough-money")));
                            }
                        }) : IntelligentItem.of(upgradeItem, event -> {
                            player.sendMessage(HexUtils.colorify(getcConfig().getString("messages.max-level")));
                        });

                        contents.update(13, furnaceItem);
                        contents.update(16, upgradeInteractiveItem);
                    }

                    public String getNextExpCost() {
                        return getfConfig().getInt("furnaces."
                                + customFurnace.getMaterial().name().toLowerCase()
                                + ".max_level") <= customFurnace.getLevel()
                                ? "MAX" : getfConfig().contains("furnaces."
                                + customFurnace.getMaterial().name().toLowerCase()
                                + ".prices."
                                + Integer.valueOf(getNextLevel())
                                + ".exp") ? String.valueOf(
                                getfConfig().getInt("furnaces."
                                        + customFurnace.getMaterial().name().toLowerCase()
                                        + ".prices."
                                        + Integer.valueOf(getNextLevel())
                                        + ".exp")
                        ) : String.valueOf(0);
                    }

                    public int getSpeed() {
                        return getfConfig().getInt(
                                "furnaces."
                                        + customFurnace.getMaterial().name().toLowerCase()
                                        + ".speed." + customFurnace.getLevel()
                        );
                    }

                    public String getNextLevel() {
                        return getfConfig().getInt("furnaces."
                                + customFurnace.getMaterial().name().toLowerCase()
                                + ".max_level") <= customFurnace.getLevel()
                                ? "MAX" : String.valueOf((customFurnace.getLevel() + 1)
                        );
                    }

                    public String getNextMoneyCost() {
                        return getfConfig().getInt("furnaces."
                                + customFurnace.getMaterial().name().toLowerCase()
                                + ".max_level") <= customFurnace.getLevel()
                                ? "MAX" : getfConfig().contains("furnaces."
                                + customFurnace.getMaterial().name().toLowerCase()
                                + ".prices."
                                + Integer.valueOf(getNextLevel())
                                + ".money") ? String.valueOf(
                                getfConfig().getInt("furnaces."
                                        + customFurnace.getMaterial().name().toLowerCase()
                                        + ".prices."
                                        + Integer.valueOf(getNextLevel())
                                        + ".money")
                        ) : String.valueOf(0);
                    }

                    public String getNextSpeed() {
                        return getfConfig().getInt("furnaces."
                                + customFurnace.getMaterial().name().toLowerCase()
                                + ".max_level") <= customFurnace.getLevel()
                                ? "MAX" : String.valueOf(
                                getfConfig().getInt("furnaces."
                                        + customFurnace.getMaterial().name().toLowerCase()
                                        + ".speed."
                                        + Integer.parseInt(getNextLevel())) / 20
                        );
                    }

                    public List<String> getFurnaceLore() {
                        List<String> stringList = getcConfig().getStringList("gui.preview.furnace_lore");
                        stringList.replaceAll(str -> str
                                .replaceAll("%level%", String.valueOf(customFurnace.getLevel()))
                                .replaceAll("%duration%", String.valueOf(getSpeed() / 20))
                                .replaceAll("%next_level%", getNextLevel())
                                .replaceAll("%duration_next%", getNextSpeed())
                                .replaceAll("%money%", getNextMoneyCost())
                                .replaceAll("%exp%", getNextExpCost()));
                        return stringList;
                    }

                    public List<String> getUpgradeLore() {
                        List<String> stringList = getcConfig().getStringList("gui.upgrade.lore");
                        stringList.replaceAll(str -> str
                                .replaceAll("%level%", String.valueOf(customFurnace.getLevel()))
                                .replaceAll("%duration%", String.valueOf(getSpeed() / 20))
                                .replaceAll("%next_level%", getNextLevel())
                                .replaceAll("%duration_next%", getNextSpeed())
                                .replaceAll("%money%", getNextMoneyCost())
                                .replaceAll("%exp%", getNextExpCost()));
                        return stringList;
                    }

                    @Override
                    public void init(Player player, InventoryContents contents) {
                        ItemStack fillerItem = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                                .displayName("&r", true)
                                .build();

                        ItemStack furnaceItem = new ItemBuilder(customFurnace.getMaterial())
                                .displayName(Objects.requireNonNull(getcConfig().getString("gui.preview.furnace_name"))
                                        .replaceAll("%level%", String.valueOf(customFurnace.getLevel()))
                                        .replaceAll("%duration%", String.valueOf(getSpeed() / 20))
                                        .replaceAll("%next_level%", getNextLevel())
                                        .replaceAll("%duration_next%", getNextSpeed()), true)
                                .lore(getFurnaceLore(), true)
                                .build();

                        ItemStack upgradeItem = new ItemBuilder(Material.EMERALD)
                                .displayName(Objects.requireNonNull(getcConfig().getString("gui.upgrade.name"))
                                        .replaceAll("%level%", String.valueOf(customFurnace.getLevel()))
                                        .replaceAll("%duration%", String.valueOf(getSpeed() / 20))
                                        .replaceAll("%next_level%", getNextLevel())
                                        .replaceAll("%duration_next%", getNextSpeed()), true)
                                .lore(getUpgradeLore(), true)
                                .build();

                        IntelligentItem upgradeInteractiveItem = !Objects.equals(getNextMoneyCost(), "MAX") ? IntelligentItem.of(upgradeItem, event -> {
                            if (player.getLevel() >= Integer.parseInt(getNextExpCost()) && SpedupFurnaces.getEcon().has(player, Integer.parseInt(getNextMoneyCost()))) {
                                customFurnace.addLevel(1);
                                player.sendMessage(HexUtils.colorify(getcConfig().getString("messages.bought-message")));
                                player.setLevel(player.getExpToLevel() - Integer.parseInt(getNextExpCost()));
                                SpedupFurnaces.getEcon().withdrawPlayer(player, Integer.parseInt(getNextMoneyCost()));
                                update(player, contents);
                            } else if (player.getLevel() < Integer.parseInt(getNextExpCost())) {
                                if (!SpedupFurnaces.getEcon().has(player, Integer.parseInt(getNextMoneyCost()))) {
                                    player.sendMessage(HexUtils.colorify(getcConfig().getString("messages.not-enough-money-and-exp")));
                                    return;
                                }
                                player.sendMessage(HexUtils.colorify(getcConfig().getString("messages.not-enough-exp")));
                            } else if (!SpedupFurnaces.getEcon().has(player, Integer.parseInt(getNextMoneyCost()))) {
                                player.sendMessage(HexUtils.colorify(getcConfig().getString("messages.not-enough-money")));
                            }
                        }) : IntelligentItem.of(upgradeItem, event -> {
                            player.sendMessage(HexUtils.colorify(getcConfig().getString("messages.max-level")));
                        });

                        contents.fillEmpty(fillerItem);
                        contents.set(13, furnaceItem);
                        contents.set(16, upgradeInteractiveItem);
                    }
                }).build(instance);
    }

    public FileConfiguration getcConfig() {
        return customizationFile.getConfig();
    }

    public FileConfiguration getfConfig() {
        return furnacesFile.getConfig();
    }

    public RyseInventory getGui() {
        return gui;
    }
}
