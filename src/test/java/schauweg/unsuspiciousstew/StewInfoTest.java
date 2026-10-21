package schauweg.unsuspiciousstew;

import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class StewInfoTest {
    @BeforeClass
    public static void bootstrapMinecraft() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    public void readsAllEffectsWithoutChangingTheItem() {
        ItemStack stack = stew(entry(MobEffects.NIGHT_VISION, 140), entry(MobEffects.POISON, 240));
        CompoundTag original = stack.getTag().copy();

        List<MobEffectInstance> effects = StewInfo.getEffects(stack);

        assertEquals(2, effects.size());
        assertSame(MobEffects.NIGHT_VISION, effects.get(0).getEffect());
        assertEquals(140, effects.get(0).getDuration());
        assertSame(MobEffects.POISON, effects.get(1).getEffect());
        assertEquals(240, effects.get(1).getDuration());
        assertEquals(original, stack.getTag());
        assertTrue(StewInfo.getEffectLines(stack).get(0).getString().endsWith("0:07"));
        assertTrue(StewInfo.getEffectLines(stack).get(1).getString().endsWith("0:12"));
    }

    @Test
    public void usesVanillaDurationWhenTheDurationIsMissing() {
        CompoundTag effect = entry(MobEffects.BLINDNESS, 20);
        effect.remove("EffectDuration");

        assertEquals(160, StewInfo.getEffects(stew(effect)).get(0).getDuration());
    }

    @Test
    public void ignoresUnknownAndMissingEffectIdsWithoutHidingValidEffects() {
        CompoundTag unknown = new CompoundTag();
        unknown.putInt("EffectId", 10000);
        unknown.putInt("EffectDuration", 100);

        List<MobEffectInstance> effects = StewInfo.getEffects(
                stew(unknown, new CompoundTag(), entry(MobEffects.SATURATION, 7)));

        assertEquals(1, effects.size());
        assertSame(MobEffects.SATURATION, effects.get(0).getEffect());
    }

    @Test
    public void ignoresOtherItemsAndAbsentOrMalformedEffectLists() {
        assertTrue(StewInfo.getEffects(new ItemStack(Items.SUSPICIOUS_STEW)).isEmpty());
        ItemStack other = new ItemStack(Items.MUSHROOM_STEW);
        other.setTag(stew(entry(MobEffects.POISON, 100)).getTag().copy());
        assertTrue(StewInfo.getEffects(other).isEmpty());

        ItemStack malformed = new ItemStack(Items.SUSPICIOUS_STEW);
        malformed.getOrCreateTag().putString("Effects", "invalid");
        assertTrue(StewInfo.getEffects(malformed).isEmpty());
    }

    @Test
    public void appendsSurvivalTooltipButKeepsVanillaCreativeTooltip() {
        ItemStack stack = stew(entry(MobEffects.NIGHT_VISION, 140));
        List<Component> survival = new ArrayList<>(List.of(Component.literal("Stew")));
        ClientEvents.onItemTooltip(new ItemTooltipEvent(stack, null, survival,
                new TooltipFlag.Default(false, false)));
        assertEquals(2, survival.size());
        assertTrue(survival.get(1).getString().endsWith("0:07"));

        List<Component> creative = new ArrayList<>(List.of(
                Component.literal("Stew"), Component.literal("Vanilla effect")));
        ClientEvents.onItemTooltip(new ItemTooltipEvent(stack, null, creative,
                new TooltipFlag.Default(false, true)));
        assertEquals(2, creative.size());
        assertEquals("Vanilla effect", creative.get(1).getString());
    }

    private static CompoundTag entry(MobEffect effect, int duration) {
        CompoundTag entry = new CompoundTag();
        entry.putInt("EffectId", MobEffect.getId(effect));
        entry.putInt("EffectDuration", duration);
        return entry;
    }

    private static ItemStack stew(CompoundTag... entries) {
        ItemStack stack = new ItemStack(Items.SUSPICIOUS_STEW);
        ListTag effects = new ListTag();
        for (CompoundTag entry : entries) {
            effects.add(entry);
        }
        stack.getOrCreateTag().put("Effects", effects);
        return stack;
    }
}
