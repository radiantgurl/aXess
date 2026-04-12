package net.teekay.axess.network.packets.client;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.teekay.axess.network.IAxessPacket;
import net.teekay.axess.utilities.name_cache.ClientPlayerNameCache;
import net.teekay.axess.utilities.name_cache.ServerPlayerNameCache;

import java.util.Objects;
import java.util.function.Supplier;

public class StCSyncPlayerNameCache implements IAxessPacket {
    public ServerPlayerNameCache serverDataModel;

    public StCSyncPlayerNameCache(ServerPlayerNameCache serverDataModel) {
        this.serverDataModel = serverDataModel;
    }

    public StCSyncPlayerNameCache(FriendlyByteBuf buffer) {
        this.serverDataModel = ServerPlayerNameCache.load(Objects.requireNonNull(buffer.readNbt()));
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeNbt(serverDataModel.save(new CompoundTag()));
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();

        if (context.getDirection().getReceptionSide().isServer()) {
            context.setPacketHandled(false);
            return;
        }

        ClientPlayerNameCache.loadAllFromServer(serverDataModel);
    }
}
