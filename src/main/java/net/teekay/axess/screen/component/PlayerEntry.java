package net.teekay.axess.screen.component;

import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.teekay.axess.Axess;
import net.teekay.axess.access.AccessNetwork;
import net.teekay.axess.client.AxessClientMenus;

import java.util.UUID;
import java.util.function.Consumer;

public class PlayerEntry {
    public UUID playerUUID;
    public String playerName;
    public TexturedButton button;

    public PlayerEntry(PlayerList list, UUID playerUUID, String playerName, Consumer<UUID> onSelect)
    {
        int pX = list.leftPos;
        int pY = list.topPos;
        int pWidth = list.width;
        int pHeight = list.elemHeight;

        this.playerUUID = playerUUID;
        this.playerName = playerName;

        this.button = new TexturedButton(pX, pY, pWidth-21, pHeight, Component.literal(playerName), btn -> {
            onSelect.accept(playerUUID);
        });

        this.button.setBounds(pX, pY, pX+pWidth, pY+list.height);
        this.button.setWidth(pWidth);


    }

}