package net.teekay.axess.block.link.payload;

import net.teekay.axess.block.link.ILinkableBlockEntity;

public class ReaderUpdateLinkPayload extends AbstractLinkPayload {

    private boolean newState;

    public ReaderUpdateLinkPayload(ILinkableBlockEntity origin, boolean newState) {
        super(origin);
        this.newState = newState;
    }

    public boolean getNewState() {
        return newState;
    }
}
