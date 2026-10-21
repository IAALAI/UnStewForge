package schauweg.unsuspiciousstew;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

public final class StewInfo {
    private StewInfo() {
    }

    public static List<MobEffectInstance> getEffects(ItemStack stack) {
        if (!stack.is(Items.SUSPICIOUS_STEW) || stack.getTag() == null) {
            return List.of();
        }

        ListTag entries = stack.getTag().getList("Effects", Tag.TAG_COMPOUND);
        List<MobEffectInstance> effects = new ArrayList<>(entries.size());
        for (int i = 0; i < entries.size(); i++) {
            CompoundTag entry = entries.getCompound(i);
            // In 1.20.1 this is an int, not the byte used by the old Fabric port.
            if (!entry.contains("EffectId", Tag.TAG_ANY_NUMERIC)) {
                continue;
            }
            MobEffect effect = MobEffect.byId(entry.getInt("EffectId"));
            if (effect == null) {
                continue;
            }
            // Match SuspiciousStewItem's vanilla fallback of eight seconds.
            int duration = entry.contains("EffectDuration", Tag.TAG_ANY_NUMERIC)
                    ? entry.getInt("EffectDuration") : 160;
            effects.add(new MobEffectInstance(effect, duration));
        }
        return effects;
    }

    public static List<Component> getEffectLines(ItemStack stack) {
        List<Component> lines = new ArrayList<>();
        for (MobEffectInstance effect : getEffects(stack)) {
            lines.add(Component.translatable(effect.getDescriptionId())
                    .append(" ")
                    .append(MobEffectUtil.formatDuration(effect, 1.0F))
                    .withStyle(ChatFormatting.GRAY));
        }
        return lines;
    }
}
