package me.gorenjec.spedupfurnaces.models;

import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class DisplayPacket {
    public void spawnTextEntity(Player player, int entityId, UUID uuid, Location location, String text, float viewRange, float yaw, float pitch, Display.BillboardConstraints billboardConstraints) {
        ClientboundAddEntityPacket packet = new ClientboundAddEntityPacket(
                entityId,
                uuid,
                location.getX(),
                location.getY(),
                location.getZ(),
                pitch,
                yaw,
                EntityType.TEXT_DISPLAY,
                0,
                new Vec3(location.getDirection().getX(), location.getDirection().getY(), location.getDirection().getZ()),
                0
        );
        ((CraftPlayer) player).getHandle().connection.send(packet);
        sendEntityMetadata(player, entityId, List.of(
                getMetaEntityText(text),
                getMetaEntityViewRange(viewRange),
                getMetaEntityBillboard(billboardConstraints)
        ));
    }

    public void destroyEntity(Player player, int entityId) {
        ClientboundRemoveEntitiesPacket packet = new ClientboundRemoveEntitiesPacket(entityId);
        ((CraftPlayer) player).getHandle().connection.send(packet);
    }

    public SynchedEntityData.DataValue<Component> getMetaEntityText(String text) {
        return new SynchedEntityData.DataValue<>(22, EntityDataSerializers.COMPONENT, Component.translatable(text));
    }

    public SynchedEntityData.DataValue<Byte> getMetaEntityBillboard(Display.BillboardConstraints billboardConstraints) {
        byte value = 0;
        switch (billboardConstraints) {
            case VERTICAL -> value = 1;
            case HORIZONTAL -> value = 2;
            case CENTER -> value = 3;
        }
        return new SynchedEntityData.DataValue<>(14, EntityDataSerializers.BYTE, value);
    }

    public SynchedEntityData.DataValue<Float> getMetaEntityViewRange(float viewRange) {
        return new SynchedEntityData.DataValue<>(16, EntityDataSerializers.FLOAT, viewRange);
    }

    public void sendEntityMetadata(Player player, int entityId, List<SynchedEntityData.DataValue<?>> dataValue) {
        ClientboundSetEntityDataPacket packet = new ClientboundSetEntityDataPacket(
                entityId,
                dataValue
        );
        ((CraftPlayer) player).getHandle().connection.send(packet);
    }
}
