package schauweg.unsuspiciousstew;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.util.List;

@EventBusSubscriber(modid = Main.MOD_ID, value = Dist.CLIENT)
public final class ClientEvents {
    private ClientEvents() {
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        // Vanilla already lists these effects in creative-mode tooltips.
        if (!event.getFlags().isCreative()) {
            event.getToolTip().addAll(StewInfo.getEffectLines(
                    event.getItemStack(), event.getContext().tickRate()));
        }
    }

    @SubscribeEvent
    public static void onSelectedItemName(RenderGuiLayerEvent.Post event) {
        if (!event.getName().equals(VanillaGuiLayers.SELECTED_ITEM_NAME)) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.level == null || minecraft.gameMode == null
                || minecraft.options.hideGui || minecraft.player.isSpectator()) {
            return;
        }

        Gui gui = minecraft.gui;
        if (gui.toolHighlightTimer <= 0) {
            return;
        }
        List<Component> lines = StewInfo.getEffectLines(gui.lastToolHighlight,
                minecraft.level.tickRateManager().tickrate());
        if (lines.isEmpty()) {
            return;
        }

        int alpha = Math.min(255, gui.toolHighlightTimer * 256 / 10);
        // Match vanilla's alpha cutoff; smaller values are treated as opaque by Font.
        if (alpha <= 3) {
            return;
        }
        GuiGraphics graphics = event.getGuiGraphics();
        int y = graphics.guiHeight()
                - Math.max(Math.max(gui.leftHeight, gui.rightHeight), 59);
        if (!minecraft.gameMode.canHurtPlayer()) {
            y += 14;
        }

        int lineHeight = minecraft.font.lineHeight + 5;
        for (Component line : lines) {
            y -= lineHeight;
            int width = minecraft.font.width(line);
            int x = (graphics.guiWidth() - width) / 2;
            graphics.drawString(minecraft.font, line, x, y, (alpha << 24) | 0xCCCCCC);
        }
    }
}
