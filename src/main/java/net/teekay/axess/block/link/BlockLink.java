package net.teekay.axess.block.link;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;

public class BlockLink {

    private UUID uuid;
    private BlockPos bpA;
    private BlockPos bpB;

    public BlockLink(BlockPos bp1, BlockPos bp2, UUID uuid) {
        this.bpA = bp1;
        this.bpB = bp2;
        this.uuid = uuid;
    }

    public BlockLink(BlockPos bp1, BlockPos bp2) {
        this(bp1, bp2, UUID.randomUUID());
    }

    public UUID getUUID() {
        return uuid;
    }

    public CompoundTag toNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putUUID("UUID", uuid);
        tag.putLong("BlockPosA", bpA.asLong());
        tag.putLong("BlockPosB", bpB.asLong());
        return tag;
    }

    public static BlockLink fromNBT(CompoundTag tag) {
        BlockPos bpA = BlockPos.of(tag.getLong("BlockPosA"));
        BlockPos bpB = BlockPos.of(tag.getLong("BlockPosB"));
        UUID uuid = tag.getUUID("UUID");
        return new BlockLink(bpA, bpB, uuid);
    }

    @Nullable
    public BlockPos getOther(BlockPos bp) {
        if (bp.equals(bpA)) return bpB;
        if (bp.equals(bpB)) return bpA;
        return null;
    }

    public BlockPos getBpA() {
        return bpA;
    }

    public BlockPos getBpB() {
        return bpB;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BlockLink l) {
            if (l.getUUID().equals(getUUID())) {
                return true;
            } else if (l.getBpA().equals(getBpA()) && l.getBpB().equals(getBpB())) {
                return true;
            } else if (l.getBpA().equals(getBpB()) && l.getBpB().equals(getBpA())) {
                return true;
            }
            return false;
        }
        return super.equals(obj);
    }

    public boolean validate(Level level) {
        if (bpA == null || bpB == null) return false;

        ILinkableBlockEntity lbA = LinkingSystem.getLinkableAtBlockPos(level, bpA);
        ILinkableBlockEntity lbB = LinkingSystem.getLinkableAtBlockPos(level, bpB);

        if (lbA == null || lbB == null) return false;
        if (!lbA.getLinks().contains(this) || !lbB.getLinks().contains(this)) return false;

        return true;
    }
}
