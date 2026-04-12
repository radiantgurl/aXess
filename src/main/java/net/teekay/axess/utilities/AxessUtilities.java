package net.teekay.axess.utilities;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.teekay.axess.access.AccessNetwork;
import net.teekay.axess.access.AccessNetworkDataClient;
import net.teekay.axess.access.AccessNetworkDataServer;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.UUID;
import java.util.function.BiFunction;

public class AxessUtilities {

    @Nullable
    public static AccessNetwork getAccessNetworkFromID(UUID id, Level refLevel) {
        if (refLevel == null || refLevel.isClientSide) {
            return AccessNetworkDataClient.getNetwork(id);
        } else {
            return AccessNetworkDataServer.get(refLevel.getServer()).getNetwork(id);
        }
    }

    public static <T> boolean getDiff(ArrayList<T> first, ArrayList<T> second, BiFunction<T, T, Boolean> comp) {
        boolean diff = true;
        if (first.size() == second.size()) {
            int cnt = 0;
            for (T fObj : first) {
                boolean found = false;
                for (T sObj : second) {
                    if (comp.apply(fObj, sObj)) {
                        found = true;
                        break;
                    }
                }
                if (found) cnt++;
            }

            if (cnt == first.size()) diff = false;
        }
        return diff;
    }

    public static JsonObject nbtToJson(CompoundTag tag) {
        JsonObject json = new JsonObject();

        for (String key : tag.getAllKeys()) {
            Tag value = tag.get(key);

            if (value instanceof CompoundTag compound) {
                json.add(key, nbtToJson(compound));
            } else if (value instanceof ListTag list) {
                JsonArray array = new JsonArray();
                for (Tag t : list) {
                    if (t instanceof CompoundTag c) {
                        array.add(nbtToJson(c));
                    } else {
                        array.add(t.getAsString());
                    }
                }
                json.add(key, array);
            } else {
                json.addProperty(key, value.getAsString());
            }
        }

        return json;
    }

}
