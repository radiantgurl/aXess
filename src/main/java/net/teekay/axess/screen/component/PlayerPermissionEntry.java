package net.teekay.axess.screen.component;

import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.teekay.axess.Axess;
import net.teekay.axess.access.AccessPermission;
import net.teekay.axess.client.AxessClientMenus;
import net.teekay.axess.utilities.name_cache.ClientPlayerNameCache;

import java.util.EnumSet;
import java.util.UUID;
import java.util.function.Consumer;

public class PlayerPermissionEntry {
    public static ResourceLocation TRASH_BUTTON = ResourceLocation.fromNamespaceAndPath(Axess.MODID, "textures/gui/delete_button.png");

    private static final Component EDIT_TEXT = Component.translatable("gui."+Axess.MODID+".button.edit");
    private static final Component DELETE_TEXT = Component.translatable("gui."+Axess.MODID+".button.delete");

    public UUID playerUUID;
    public EnumSet<AccessPermission> permissions;

    public TexturedButton button;
    public HumbleImageButton trashButton;

    public boolean hasOptions;

    public PlayerPermissionEntry(PlayerPermissionList list, UUID playerUUID, EnumSet<AccessPermission> permissions, Consumer<UUID> onDelete)
    {
        int pX = list.leftPos;
        int pY = list.topPos;
        int pWidth = list.width;
        int pHeight = list.elemHeight;

        String pName = ClientPlayerNameCache.getName(playerUUID);
        if (ClientPlayerNameCache.getName(playerUUID) == null) {
            pName = playerUUID.toString();
        }

        this.button = new TexturedButton(pX, pY, pWidth-21, pHeight, Component.literal(pName), btn -> {
            AxessClientMenus.openPermissionEditorScreen(playerUUID, permissions);
        });

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
                    onDelete.accept(playerUUID);
                });



        this.button.setBounds(pX, pY, pX+pWidth, pY+list.height);
        this.trashButton.setBounds(pX, pY, pX+pWidth, pY+list.height);

        this.trashButton.setTooltip(Tooltip.create(DELETE_TEXT));

        this.playerUUID = playerUUID;
        this.permissions = permissions;
    }

}