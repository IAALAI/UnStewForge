package schauweg.unsuspiciousstew;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = Main.MOD_ID, value = Dist.CLIENT)
public final class ClientEvents {
    private ClientEvents() {
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        // Vanilla already lists these effects in creative-mode tooltips.
        if (!event.getFlags().isCreative()) {
            event.getToolTip().addAll(StewInfo.getEffectLines(event.getItemStack()));
        }
    }

    @SubscribeEvent
    public static void onSelectedItemName(RenderGuiOverlayEvent.Post event) {
        if (!event.getOverlay().id().equals(VanillaGuiOverlay.ITEM_NAME.id())) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.gameMode == null
                || minecraft.options.hideGui || minecraft.player.isSpectator()) {
            return;
        }

        ForgeGui gui = (ForgeGui) minecraft.gui;
        if (gui.toolHighlightTimer <= 0) {
            return;
        }
        List<Component> lines = StewInfo.getEffectLines(gui.lastToolHighlight);
        if (lines.isEmpty()) {
            return;
        }

        int alpha = Math.min(255, gui.toolHighlightTimer * 256 / 10);
        // Match vanilla's alpha cutoff; smaller values are treated as opaque by Font.
        if (alpha <= 3) {
            return;
        }
        int y = event.getWindow().getGuiScaledHeight()
                - Math.max(Math.max(gui.leftHeight, gui.rightHeight), 59);
        if (!minecraft.gameMode.canHurtPlayer()) {
            y += 14;
        }

        GuiGraphics graphics = event.getGuiGraphics();
        int lineHeight = minecraft.font.lineHeight + 5;
        for (Component line : lines) {
            y -= lineHeight;
            int width = minecraft.font.width(line);
            int x = (event.getWindow().getGuiScaledWidth() - width) / 2;
            graphics.drawString(minecraft.font, line, x, y, (alpha << 24) | 0xCCCCCC);
        }
    }
}
