package net.teekay.axess.network.packets.client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.teekay.axess.network.IAxessPacket;
import net.teekay.axess.utilities.name_cache.ClientPlayerNameCache;

import java.util.UUID;
import java.util.function.Supplier;

public class StCPlayerCacheModifiedPacket implements IAxessPacket {
    public UUID uuid;
    public String name;

    public StCPlayerCacheModifiedPacket(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public StCPlayerCacheModifiedPacket(FriendlyByteBuf buffer) {
        this.uuid = buffer.readUUID();
        this.name = buffer.readUtf();
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUUID(uuid);
        buffer.writeUtf(name, 128);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();


        if (context.getDirection().getReceptionSide().isServer()) {
            context.setPacketHandled(false);
            return;
        }

        ClientPlayerNameCache.setName(uuid, name);
    }
}
