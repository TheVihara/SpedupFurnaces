package me.gorenjec.spedupfurnaces.models;

import me.gorenjec.spedupfurnaces.SpedupFurnaces;
import net.minecraft.world.entity.Display;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class HoloTextDisplay {
    private String text;
    private Location location;
    private int range = 10;
    private float yaw;
    private float pitch;
    private Display.BillboardConstraints billboardConstraints;
    private SpedupFurnaces instance;
    private BukkitTask task;

    private int id = 0;
    private Map<Player, Boolean> viewers = new HashMap<>();
    private DisplayPacket displayPacket;

    private Random random = new Random();

    public HoloTextDisplay(SpedupFurnaces instance, String text, Location location, int range, float yaw, float pitch, Display.BillboardConstraints billboardConstraints) {
        this.instance = instance;
        this.text = text;
        this.location = location;
        this.range = range;
        this.yaw = yaw;
        this.pitch = pitch;
        this.billboardConstraints = billboardConstraints;
        this.displayPacket = instance.getDisplayPacket();

        id = 35000 + random.nextInt(2000000000);

        Bukkit.getOnlinePlayers().forEach(this::register);
        startTask();
    }

    public void display(Player player) {
        displayPacket.spawnTextEntity(player, id, UUID.randomUUID(), location, text, range, yaw, pitch, billboardConstraints);
        viewers.put(player, true);
    }

    public void refresh() {
        viewers.forEach((player, isAlive) -> destroy(player));
        task.cancel();

        id = 35000 + random.nextInt(2000000000);

        viewers.forEach((player, isAlive) -> display(player));
        startTask();
    }

    public void startTask() {
        task = Bukkit.getScheduler().runTaskTimerAsynchronously(instance, () -> {
            viewers.forEach((player, isAlive) -> {
                if (player == null || !player.isOnline()) {
                    viewers.remove(player);
                } else if (player.getWorld() == location.getWorld()) {
                    Location playerLocation = player.getLocation();
                    double distance = playerLocation.distanceSquared(location);

                    if (distance <= range * range) {
                        if (!isAlive) {
                            display(player);
                            viewers.replace(player, true);
                        }
                    } else {
                        if (isAlive) {
                            destroy(player);
                            viewers.replace(player, false);
                        }
                    }
                } else if (isAlive) {
                    destroy(player);
                    viewers.replace(player, false);
                }
            });
        }, 10L, 2L);
    }

    public void destroy(Player player) {
        displayPacket.destroyEntity(player, id);
    }

    public void register(Player player) {
        viewers.put(player, false);
        display(player);
    }

    public void unregister(Player player) {
        viewers.remove(player);
        destroy(player);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        refresh();
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public Display.BillboardConstraints getBillboardConstraints() {
        return billboardConstraints;
    }

    public void setBillboardConstraints(Display.BillboardConstraints billboardConstraints) {
        this.billboardConstraints = billboardConstraints;
    }

    public SpedupFurnaces getInstance() {
        return instance;
    }

    public void setInstance(SpedupFurnaces instance) {
        this.instance = instance;
    }

    public BukkitTask getTask() {
        return task;
    }

    public void setTask(BukkitTask task) {
        this.task = task;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Map<Player, Boolean> getViewers() {
        return viewers;
    }

    public void setViewers(Map<Player, Boolean> viewers) {
        this.viewers = viewers;
    }

    public DisplayPacket getDisplayPacket() {
        return displayPacket;
    }

    public void setDisplayPacket(DisplayPacket displayPacket) {
        this.displayPacket = displayPacket;
    }

    public Random getRandom() {
        return random;
    }

    public void setRandom(Random random) {
        this.random = random;
    }
}
