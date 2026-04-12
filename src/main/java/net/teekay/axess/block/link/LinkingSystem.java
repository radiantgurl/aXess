package net.teekay.axess.block.link;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.teekay.axess.block.link.payload.AbstractLinkPayload;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Queue;

public class LinkingSystem {

    @Nullable
    public static ILinkableBlockEntity getLinkableAtBlockPos(Level level, BlockPos blockPos) {
        if (blockPos == null) return null;
        BlockEntity be = level.getBlockEntity(blockPos);

        if (be == null) return null;
        if (!(be instanceof ILinkableBlockEntity lb)) return null;
        return lb;
    }

    public static <B extends ILinkableBlockEntity> void validateLinksOnBlock(B block) {
        Level level = block.getBlockEntity().getLevel();
        if (block.getLinks().removeIf(blockLink -> {
            if (!blockLink.getBpA().equals(block.getBlockEntity().getBlockPos())) {
                if (!blockLink.getBpB().equals(block.getBlockEntity().getBlockPos())) {
                    return true;
                }
            }
            if (!blockLink.validate(level)) return true;
            return false;
        }) ) {
            if (block instanceof BlockEntity e) e.setChanged();
        }
    }

    public static <B extends ILinkableBlockEntity> boolean clearLinks(B block) {

        Level level = block.getBlockEntity().getLevel();
        BlockEntity be = block.getBlockEntity();

        if (level == null || be == null) return false;

        BlockPos first = be.getBlockPos();

        validateLinksOnBlock(block);

        for (BlockLink link : block.getLinks()) {
            ILinkableBlockEntity other = getLinkableAtBlockPos(level, link.getOther(first));

            if (other == null) continue;

            other.getLinks().removeIf(link::equals);
            other.getBlockEntity().setChanged();
        }

        block.getLinks().clear();
        block.onClearLinks();
        block.getBlockEntity().setChanged();
        return true;
    }

    public static <B extends ILinkableBlockEntity> boolean linkBlocks(B block1, B block2) {
        BlockLink newBlockLink = new BlockLink(block1.getBlockEntity().getBlockPos(), block2.getBlockEntity().getBlockPos());

        if (block1.getLinks().contains(newBlockLink) && block2.getLinks().contains(newBlockLink)) return false;
        if (!block1.canLinkWith(block2.getBlockEntity()) || !block2.canLinkWith(block1.getBlockEntity())) return false;

        block1.getLinks().add(newBlockLink);
        block2.getLinks().add(newBlockLink);

        block1.onLinkWith(block2.getBlockEntity(), true);
        block2.onLinkWith(block1.getBlockEntity(), false);

        block1.getBlockEntity().setChanged();
        block2.getBlockEntity().setChanged();

        return true;
    }

    public static <B extends ILinkableBlockEntity, P extends AbstractLinkPayload> void emitPayloadToConnections(B origin, P payload) {
        Level level = origin.getBlockEntity().getLevel();

        // run fill algorithm to find all nodes
        BlockPos originPos = origin.getBlockEntity().getBlockPos();
        ArrayList<BlockPos> destinations = new ArrayList<>();

        Queue<BlockPos> positionsToCheck = new PriorityQueue<>();
        positionsToCheck.add(origin.getBlockEntity().getBlockPos());

        while (positionsToCheck.size() > 0) {
            BlockPos top = positionsToCheck.remove();
            if (destinations.contains(top)) continue;
            ILinkableBlockEntity cur = getLinkableAtBlockPos(level, top);

            if (cur == null) return;

            validateLinksOnBlock(cur);

            cur.getLinks().forEach(
                    (blockLink -> {
                        BlockPos otherPos = blockLink.getOther(top);
                        if (destinations.contains(otherPos)) return;
                        positionsToCheck.add(otherPos);
                    })
            );

            destinations.add(top);
        }
        for (BlockPos pos :
                destinations) {
            if (pos.equals(originPos)) continue;
            ILinkableBlockEntity l = getLinkableAtBlockPos(level, pos);
            if (l == null) continue;
            l.acceptPayload(payload);
        }
    }


}
