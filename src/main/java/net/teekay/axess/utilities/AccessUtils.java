package net.teekay.axess.utilities;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.teekay.axess.access.AccessNetwork;
import net.teekay.axess.access.AccessNetworkDataClient;
import net.teekay.axess.access.AccessNetworkDataServer;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class AccessUtils {

    public static boolean canPlayerEditNetwork(Player player, AccessNetwork network) {
        if (network == null) return true;

        return (network.getOwnerUUID().equals(player.getUUID()) || (player.isCreative() && player.hasPermissions(4)));
    }

    @Nullable
    public static AccessNetwork getAccessNetworkFromID(UUID id, Level refLevel) {
        if (refLevel == null || refLevel.isClientSide) {
            return AccessNetworkDataClient.getNetwork(id);
        } else {
            return AccessNetworkDataServer.get(refLevel.getServer()).getNetwork(id);
        }
    }

}
