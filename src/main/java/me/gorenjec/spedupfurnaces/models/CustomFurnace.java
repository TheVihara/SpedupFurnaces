package me.gorenjec.spedupfurnaces.models;

import org.bukkit.Location;
import org.bukkit.Material;

public class CustomFurnace {
    private Location location;
    private Material material;
    private int level;

    public CustomFurnace(Location location, Material material, int level) {
        this.location = location;
        this.material = material;
        this.level = level;
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
}
