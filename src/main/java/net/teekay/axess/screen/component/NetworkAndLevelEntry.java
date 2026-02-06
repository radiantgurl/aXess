package net.teekay.axess.screen.component;

import com.ibm.icu.impl.Pair;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.teekay.axess.Axess;
import net.teekay.axess.access.AccessLevel;
import net.teekay.axess.access.AccessNetwork;
import net.teekay.axess.client.AxessClientMenus;

import java.util.function.Consumer;

public class NetworkAndLevelEntry {
    public static ResourceLocation TRASH_BUTTON = ResourceLocation.fromNamespaceAndPath(Axess.MODID, "textures/gui/delete_button.png");

    private static final Component EDIT_TEXT = Component.translatable("gui."+Axess.MODID+".button.edit");
    private static final Component DELETE_TEXT = Component.translatable("gui."+Axess.MODID+".button.delete");

    public Pair<AccessNetwork, AccessLevel> pair;
    public TexturedButton button;
    public HumbleImageButton trashButton;

    public boolean hasOptions;

    public NetworkAndLevelEntry(NetworkAndLevelList list, Pair<AccessNetwork, AccessLevel> pair, boolean withOptions, Consumer<Pair<AccessNetwork, AccessLevel>> onDelete)
    {
        int pX = list.leftPos;
        int pY = list.topPos;
        int pWidth = list.width;
        int pHeight = list.elemHeight;

        hasOptions = withOptions;

        this.button = new TexturedButton(pX, pY, pWidth-21, pHeight, Component.literal(pair.first.getName()+" - "+pair.second.getName()), btn -> {});

        this.trashButton = new HumbleImageButton(
                pX + pWidth - 20,
                pY,
                20,
                20,
                0,
                0,
                20,
                TRASH_BUTTON,
                32, 64,
                btn -> {
                    onDelete.accept(pair);
                });



        this.button.setBounds(pX, pY, pX+pWidth, pY+list.height);
        this.trashButton.setBounds(pX, pY, pX+pWidth, pY+list.height);

        this.trashButton.setTooltip(Tooltip.create(DELETE_TEXT));

        this.pair = pair;
    }

}