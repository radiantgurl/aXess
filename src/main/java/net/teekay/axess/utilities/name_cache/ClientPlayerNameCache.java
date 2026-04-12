package net.teekay.axess.utilities.name_cache;

import java.util.HashMap;
import java.util.UUID;

public class ClientPlayerNameCache {

    private static final HashMap<UUID, String> nameRegistry = new HashMap<>();

    public static void loadAllFromServer(ServerPlayerNameCache serverData) {
        nameRegistry.clear();
        nameRegistry.putAll(serverData.getNameRegistry());
    }

    public static String getName(UUID uuid) {
        return nameRegistry.get(uuid);
    }

    public static void setName(UUID uuid, String name) {
        nameRegistry.put(uuid, name);
    }

    public static void removeName(UUID uuid) {
        nameRegistry.remove(uuid);
    }
}
