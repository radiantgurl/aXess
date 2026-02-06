package net.teekay.axess.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.teekay.axess.Axess;
import net.teekay.axess.AxessConfig;
import net.teekay.axess.access.AccessLevel;
import net.teekay.axess.access.AccessNetwork;
import net.teekay.axess.access.AccessNetworkDataClient;
import net.teekay.axess.client.AxessClientMenus;
import net.teekay.axess.screen.component.HelpButton;
import net.teekay.axess.screen.component.HumbleImageButton;
import net.teekay.axess.screen.component.NetworkEntry;
import net.teekay.axess.screen.component.NetworkList;
import net.teekay.axess.utilities.AxessColors;

import java.util.function.BiConsumer;

public class NetworkSelectionScreen extends Screen {

    private static final Component TITLE_LABEL = Component.translatable("gui."+Axess.MODID+".network_selection_screen");

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Axess.MODID, "textures/gui/network_selection_screen.png");


    private final int imageWidth, imageHeight;

    private int leftPos, topPos;

    private NetworkList networkList;

    private BiConsumer<AccessNetwork, AccessLevel> consumer;

    public NetworkSelectionScreen(BiConsumer<AccessNetwork, AccessLevel> consumer) {
        super(TITLE_LABEL);

        this.imageWidth = 201;
        this.imageHeight = 156;

        this.consumer = consumer;
    }

    @Override
    protected void init() {
        super.init();

        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;

        if (this.minecraft == null) return;
        ClientLevel level = this.minecraft.level;
        if (level == null) return;

        this.networkList = new NetworkList(leftPos + 14, topPos + 26, 169, 116,
                (network -> {
                    AxessClientMenus.popGuiLayer();
                    AxessClientMenus.openAccessLevelSelectionScreen(network, (accessLevel) -> {
                        this.consumer.accept(network, accessLevel);
                    });
                }));

        for (AccessNetwork network : AccessNetworkDataClient.getNetworks()) {
            NetworkEntry btn = this.networkList.addElement(network, false);
            addWidget(btn.button);
        }
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pGuiGraphics);

        pGuiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        this.networkList.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        pGuiGraphics.drawString(this.font, TITLE_LABEL, this.leftPos+8, this.topPos+8, AxessColors.MAIN.getRGB(), false);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        this.networkList.scroll((int) (pDelta) * -7);
        return super.mouseScrolled(pMouseX, pMouseY, pDelta);
    }
}
