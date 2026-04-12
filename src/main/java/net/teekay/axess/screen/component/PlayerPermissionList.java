package net.teekay.axess.screen.component;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.teekay.axess.Axess;
import net.teekay.axess.access.AccessLevel;
import net.teekay.axess.access.AccessNetwork;
import net.teekay.axess.access.AccessPermission;
import net.teekay.axess.utilities.AxessColors;
import net.teekay.axess.utilities.MathUtilities;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class PlayerPermissionList {
    private final List<PlayerPermissionEntry> buttons = new ArrayList<>();

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Axess.MODID, "textures/gui/network_manager.png");

    private int scrollPos = 0;
    private int maxScrollPos = 0;

    public int width, height;
    public int leftPos, topPos;
    public int elemHeight = 20;
    public int padding = 1;

    private int scrollerWidth = 4;

    Consumer<UUID> onDelete;

    public PlayerPermissionList(int leftPos, int topPos, int width, int height, Consumer<UUID> onDelete) {
        this.width = width;
        this.height = height;
        this.leftPos = leftPos;
        this.topPos = topPos;

        this.onDelete = onDelete;
    }

    private void updateMaxScroll() {
        int totalHeight = buttons.size() * (elemHeight + padding) - padding;
        this.maxScrollPos = totalHeight - height;
    }

    public PlayerPermissionEntry addElement(UUID playerUUID, EnumSet<AccessPermission> permissions) {
        PlayerPermissionEntry newButton = new PlayerPermissionEntry(this, playerUUID, permissions,
                (toDelete) -> {
                        onDelete.accept(toDelete);
                });

        buttons.add(newButton);
        updateMaxScroll();
        return newButton;
    }

    public void removeElement(UUID uuid, Consumer<PlayerPermissionEntry> toRemove){
        buttons.removeIf((elem) -> {
            boolean a = elem.playerUUID == uuid;
            if (a) toRemove.accept(elem);
            return a;
        });
        updateMaxScroll();
    }


    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.enableScissor(leftPos, topPos, leftPos+width+scrollerWidth+1, topPos+height);

        int scrollerHeight = MathUtilities.calcScrollHeight(height, maxScrollPos);
        int scrollerPos = MathUtilities.calcScrollPos(height, scrollerHeight, scrollPos, maxScrollPos);

        graphics.fill(leftPos+width+1, topPos+scrollerPos, leftPos+width+1+scrollerWidth, topPos+scrollerPos+scrollerHeight, AxessColors.MAIN.getRGB());

        for (int index = 0; index < buttons.size(); index++) {
            PlayerPermissionEntry button = buttons.get(index);

            int yPos = topPos + index * elemHeight - scrollPos + index * padding;

            button.button.setY(yPos);
            button.trashButton.setY(yPos);

            button.button.render(graphics, mouseX, mouseY, partialTick);
            button.trashButton.render(graphics, mouseX, mouseY, partialTick);

            //graphics.drawString(Minecraft.getInstance().font, network.getName(), leftPos + 4, yPos + (elemHeight - 7) / 2, 0xFFFFFF);
        }

        graphics.disableScissor();


    }

    public void scroll(int delta) {
        this.scrollPos = Math.max(Math.min(scrollPos + delta, maxScrollPos), 0);
    }

    public int getSize() {
        return this.buttons.size();
    }
}