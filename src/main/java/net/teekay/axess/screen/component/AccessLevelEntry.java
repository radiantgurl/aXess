package net.teekay.axess.screen.component;

import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.teekay.axess.Axess;
import net.teekay.axess.access.AccessLevel;
import net.teekay.axess.access.AccessNetwork;
import net.teekay.axess.client.AxessClientMenus;

import java.util.function.Consumer;

public class AccessLevelEntry {
    public static ResourceLocation TRASH_BUTTON = ResourceLocation.fromNamespaceAndPath(Axess.MODID, "textures/gui/delete_button.png");

    private static final Component EDIT_TEXT = Component.translatable("gui."+Axess.MODID+".button.edit");
    private static final Component DELETE_TEXT = Component.translatable("gui."+Axess.MODID+".button.delete");

    public AccessLevel level;
    public TexturedButton button;

    public AccessLevelEntry(AccessLevelList list, AccessLevel level, Consumer<AccessLevel> onSelect)
    {
        int pX = list.leftPos;
        int pY = list.topPos;
        int pWidth = list.width;
        int pHeight = list.elemHeight;


        this.button = new TexturedButton(pX, pY, pWidth, pHeight, Component.literal(level.getName()), btn -> {
            onSelect.accept(level);
        });


        this.button.setBounds(pX, pY, pX+pWidth, pY+list.height);

        this.level = level;
    }

}