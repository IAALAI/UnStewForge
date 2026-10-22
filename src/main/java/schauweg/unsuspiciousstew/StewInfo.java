package schauweg.unsuspiciousstew;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.SuspiciousStewEffects;

import java.util.ArrayList;
import java.util.List;

public final class StewInfo {
    private StewInfo() {
    }

    public static List<MobEffectInstance> getEffects(ItemStack stack) {
        if (!stack.is(Items.SUSPICIOUS_STEW)) {
            return List.of();
        }

        SuspiciousStewEffects component = stack.getOrDefault(
                DataComponents.SUSPICIOUS_STEW_EFFECTS, SuspiciousStewEffects.EMPTY);
        List<MobEffectInstance> effects = new ArrayList<>(component.effects().size());
        for (SuspiciousStewEffects.Entry entry : component.effects()) {
            effects.add(entry.createEffectInstance());
        }
        return effects;
    }

    public static List<Component> getEffectLines(ItemStack stack, float tickRate) {
        List<Component> lines = new ArrayList<>();
        for (MobEffectInstance effect : getEffects(stack)) {
            lines.add(Component.translatable(effect.getDescriptionId())
                    .append(" ")
                    .append(MobEffectUtil.formatDuration(effect, 1.0F, tickRate))
                    .withStyle(ChatFormatting.GRAY));
        }
        return lines;
    }
}
