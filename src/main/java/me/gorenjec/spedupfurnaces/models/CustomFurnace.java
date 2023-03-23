package me.gorenjec.spedupfurnaces.models;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;

public class CustomFurnace {
    private Location location;
    private Material material;
    private HoloTextDisplay holoTextDisplay;
    private int level;

    public CustomFurnace(Location location, Material material, int level, HoloTextDisplay holoTextDisplay) {
        this.location = location;
        this.material = material;
        this.level = level;
        this.holoTextDisplay = holoTextDisplay;
    }

    public Material getMaterial() {
        return material;
    }

    public int getLevel() {
        return level;
    }

    public Location getLocation() {
        return location;
    }

    public void setLevel(int level) {
        this.level = level;
        holoTextDisplay.refresh();
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void addLevel(int amount) {
        this.level = this.level + amount;
    }

    public HoloTextDisplay getHoloTextDisplay() {
        return holoTextDisplay;
    }

    public BlockFace getFacing() {
        Block block = location.getBlock();
        BlockData blockData = block.getBlockData();
        BlockFace blockFace = ((Directional) blockData).getFacing();

        return blockFace;
    }
}
