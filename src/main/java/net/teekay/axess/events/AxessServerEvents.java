package net.teekay.axess.events;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.teekay.axess.Axess;
import net.teekay.axess.access.AccessNetworkDataServer;
import net.teekay.axess.network.AxessPacketHandler;
import net.teekay.axess.network.packets.client.StCPlayerCacheModifiedPacket;
import net.teekay.axess.network.packets.client.StCSyncAllNetworks;
import net.teekay.axess.network.packets.client.StCSyncPlayerNameCache;
import net.teekay.axess.utilities.name_cache.ServerPlayerNameCache;

import java.util.Objects;

@Mod.EventBusSubscriber(modid = Axess.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AxessServerEvents {

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!event.getEntity().level().isClientSide) {
            Player player = event.getEntity();

            AccessNetworkDataServer networkDataServer = AccessNetworkDataServer.get(Objects.requireNonNull(player.getServer()));

            ServerPlayerNameCache playerNameCacheServer = ServerPlayerNameCache.get(Objects.requireNonNull(player.getServer()));
            playerNameCacheServer.setName(player.getUUID(), player.getName().getString());

            AxessPacketHandler.sendToPlayer(new StCSyncAllNetworks(networkDataServer), (ServerPlayer) player);
            AxessPacketHandler.sendToPlayer(new StCSyncPlayerNameCache(playerNameCacheServer), (ServerPlayer) player);

            AxessPacketHandler.sendToAllClients(
                    new StCPlayerCacheModifiedPacket(
                            player.getUUID(),
                            player.getName().getString())
            );
        }
    }

    @SubscribeEvent
    public static void onWorldSave(LevelEvent.Save event) {
        if (event.getLevel().isClientSide()) return;

        AccessNetworkDataServer data = AccessNetworkDataServer.get(event.getLevel().getServer());
        data.setDirty();

        ServerPlayerNameCache nameCache = ServerPlayerNameCache.get(event.getLevel().getServer());
        nameCache.setDirty();
    }

}
