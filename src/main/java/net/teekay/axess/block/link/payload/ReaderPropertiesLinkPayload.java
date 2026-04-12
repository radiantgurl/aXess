package net.teekay.axess.block.link.payload;

import net.teekay.axess.access.AccessActivationMode;
import net.teekay.axess.access.AccessCompareMode;
import net.teekay.axess.block.link.ILinkableBlockEntity;

public class ReaderPropertiesLinkPayload extends AbstractLinkPayload{

    private AccessCompareMode compareMode;
    private AccessActivationMode activationMode;
    private int pulseDurationTicks;

    public ReaderPropertiesLinkPayload(ILinkableBlockEntity origin, AccessCompareMode compareMode, AccessActivationMode activationMode, int pulseDurationTicks) {
        super(origin);
        this.compareMode = compareMode;
        this.activationMode = activationMode;
        this.pulseDurationTicks = pulseDurationTicks;
    }

    public AccessCompareMode getCompareMode() {
        return compareMode;
    }

    public AccessActivationMode getActivationMode() {
        return activationMode;
    }

    public int getPulseDurationTicks() {
        return pulseDurationTicks;
    }
}
