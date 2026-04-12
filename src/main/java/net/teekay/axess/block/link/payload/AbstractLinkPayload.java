package net.teekay.axess.block.link.payload;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.teekay.axess.block.link.ILinkableBlockEntity;

public class AbstractLinkPayload {

    private ILinkableBlockEntity origin;

    public AbstractLinkPayload(ILinkableBlockEntity origin) {
        this.origin = origin;
    }

    public ILinkableBlockEntity getOrigin() {
        return origin;
    }
}
