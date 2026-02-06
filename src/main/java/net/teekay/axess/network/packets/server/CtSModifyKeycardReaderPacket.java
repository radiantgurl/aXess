package net.teekay.axess.network.packets.server;

import com.ibm.icu.impl.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.teekay.axess.access.*;
import net.teekay.axess.block.readers.KeycardReaderBlockEntity;
import net.teekay.axess.network.IAxessPacket;
import net.teekay.axess.registry.AxessIconRegistry;
import net.teekay.axess.screen.KeycardReaderMenu;
import net.teekay.axess.utilities.AccessUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Supplier;

public class CtSModifyKeycardReaderPacket implements IAxessPacket {
    public BlockPos blockEntityPos;
    public UUID networkUUID;
    public ArrayList<UUID> accessLevelsUUIDs;
    public ArrayList<Pair<UUID, UUID>> overrideAccessLevelsUUIDs;
    public AccessCompareMode compareMode;
    public AccessActivationMode activationMode;
    public int pulseDurationTicks;
    public boolean overrideDisplay;
    public AxessIconRegistry.AxessIcon overrideIcon;
    public Color overrideColor;

    public CtSModifyKeycardReaderPacket(BlockPos blockEntityPos, AccessNetwork network, ArrayList<AccessLevel> levels, ArrayList<Pair<AccessNetwork, AccessLevel>> overrideLevels, AccessCompareMode compareMode, AccessActivationMode activationMode, int pulseDurationTicks, boolean overrideDisplay, AxessIconRegistry.AxessIcon overrideIcon, Color overrideColor) {
        this.blockEntityPos = blockEntityPos;
        this.networkUUID = network.getUUID();
        this.accessLevelsUUIDs = new ArrayList<>();
        this.overrideAccessLevelsUUIDs = new ArrayList<>();

        this.compareMode = compareMode;
        this.activationMode = activationMode;
        this.pulseDurationTicks = pulseDurationTicks;

        for (AccessLevel level :
                levels) {
            this.accessLevelsUUIDs.add(level.getUUID());
        }

        for (Pair<AccessNetwork, AccessLevel> pair :
                overrideLevels) {
            this.overrideAccessLevelsUUIDs.add(Pair.of(pair.first.getUUID(), pair.second.getUUID()));
        }

        this.overrideDisplay = overrideDisplay;
        this.overrideIcon = overrideIcon;
        this.overrideColor = overrideColor;
    }

    public CtSModifyKeycardReaderPacket(FriendlyByteBuf buffer) {
        this.blockEntityPos = buffer.readBlockPos();
        this.networkUUID = buffer.readUUID();

        this.accessLevelsUUIDs = new ArrayList<>();
        this.overrideAccessLevelsUUIDs = new ArrayList<>();

        CompoundTag dataTag = buffer.readNbt();

        ListTag list = (ListTag) dataTag.get("Levels");

        for (int i = 0; i < list.size(); i++) {
            UUID uuid = ((CompoundTag)list.get(i)).getUUID("UUID");
            this.accessLevelsUUIDs.add(uuid);
        }

        ListTag olist = (ListTag)dataTag.get("OverrideLevels");

        for (int i = 0; i < olist.size(); i++) {
            UUID netUuid = ((CompoundTag)olist.get(i)).getUUID("NetworkUUID");
            UUID levelUuid = ((CompoundTag)olist.get(i)).getUUID("LevelUUID");
            this.overrideAccessLevelsUUIDs.add(Pair.of(netUuid, levelUuid));
        }

        this.compareMode = buffer.readEnum(AccessCompareMode.class);
        this.activationMode = buffer.readEnum(AccessActivationMode.class);
        this.pulseDurationTicks = buffer.readInt();

        this.overrideDisplay = buffer.readBoolean();
        this.overrideIcon = AxessIconRegistry.getIcon(buffer.readUtf());
        this.overrideColor = new Color(buffer.readInt());
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(blockEntityPos);
        buffer.writeUUID(networkUUID);

        ListTag listTag = new ListTag();

        for (UUID uuid :
                accessLevelsUUIDs) {
            CompoundTag newTag = new CompoundTag();
            newTag.putUUID("UUID", uuid);
            listTag.add(newTag);
        }

        ListTag olistTag = new ListTag();

        for (Pair<UUID,UUID> pair :
                overrideAccessLevelsUUIDs) {
            CompoundTag newTag = new CompoundTag();
            newTag.putUUID("NetworkUUID", pair.first);
            newTag.putUUID("LevelUUID", pair.second);
            olistTag.add(newTag);
        }

        CompoundTag dataTag = new CompoundTag();
        dataTag.put("Levels", listTag);
        dataTag.put("OverrideLevels", olistTag);
        buffer.writeNbt(dataTag);

        buffer.writeEnum(compareMode);
        buffer.writeEnum(activationMode);
        buffer.writeInt(pulseDurationTicks);

        buffer.writeBoolean(overrideDisplay);
        buffer.writeUtf(overrideIcon.ID);
        buffer.writeInt(overrideColor.getRGB());
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();

        if (context.getDirection().getReceptionSide().isClient()) {
            context.setPacketHandled(false);
            return;
        }

        context.enqueueWork(() -> {
            try {
                ServerPlayer player = context.getSender();
                if (player == null) return;

                KeycardReaderBlockEntity keycardEditor;
                if (player.containerMenu instanceof KeycardReaderMenu menu) {
                    keycardEditor = menu.blockEntity;
                } else if (player.level().getBlockEntity(blockEntityPos) instanceof KeycardReaderBlockEntity blockEntity) {
                    keycardEditor = blockEntity;
                } else return;

                AccessNetwork prevNetwork = keycardEditor.getAccessNetwork();
                if (prevNetwork != null) if (!AccessUtils.canPlayerEditNetwork(player, prevNetwork)) return;

                AccessNetworkDataServer serverNetworkData = AccessNetworkDataServer.get(player.getServer());
                AccessNetwork network = serverNetworkData.getNetwork(networkUUID);
                if (network == null) return;

                ArrayList<AccessLevel> accessLevels = new ArrayList<>();
                for (UUID uuid :
                        accessLevelsUUIDs) {
                    AccessLevel accessLevel = network.getAccessLevel(uuid);
                    if (accessLevel == null) return;
                    accessLevels.add(accessLevel);
                }

                ArrayList<Pair<AccessNetwork, AccessLevel>> overrideAccessLevels = new ArrayList<>();
                for (Pair<UUID, UUID> pair :
                        overrideAccessLevelsUUIDs) {
                    AccessNetwork net = serverNetworkData.getNetwork(pair.first);
                    if (net == null) continue;
                    AccessLevel level = net.getAccessLevel(pair.second);
                    if (level == null) continue;
                    overrideAccessLevels.add(Pair.of(net, level));
                }

                if (compareMode != null)
                    keycardEditor.setCompareMode(compareMode);

                if (activationMode != null) {
                    keycardEditor.setActivationMode(activationMode);
                    keycardEditor.execOnReaderPair(p -> p.setActivationMode(activationMode));
                }

                if (pulseDurationTicks != 0) {
                    keycardEditor.setPulseDurationTicks(pulseDurationTicks);
                    keycardEditor.execOnReaderPair(p -> p.setPulseDurationTicks(pulseDurationTicks));
                }

                keycardEditor.setOverrideDisplay(overrideDisplay);
                keycardEditor.setOverrideIcon(overrideIcon);
                keycardEditor.setOverrideColor(overrideColor);

                keycardEditor.setAccessNetwork(network);
                keycardEditor.setAccessLevels(accessLevels);

                keycardEditor.setOverrideAccessLevels(overrideAccessLevels);

                keycardEditor.setChanged();
                keycardEditor.execOnReaderPair(KeycardReaderBlockEntity::setChanged);
                keycardEditor.deactivate();
                keycardEditor.execOnReaderPair(KeycardReaderBlockEntity::deactivate);
            } catch (Exception e) {e.printStackTrace();}
        });
    }
}
