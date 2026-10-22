package schauweg.unsuspiciousstew;

import net.minecraft.SharedConstants;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.SuspiciousStewEffects;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class StewInfoTest {
    @BeforeAll
    public static void bootstrapMinecraft() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    public void readsAllEffectsWithoutChangingTheItem() {
        ItemStack stack = stew(entry(MobEffects.NIGHT_VISION, 140), entry(MobEffects.POISON, 240));
        ItemStack original = stack.copy();

        List<MobEffectInstance> effects = StewInfo.getEffects(stack);

        assertEquals(2, effects.size());
        assertSame(MobEffects.NIGHT_VISION, effects.get(0).getEffect());
        assertEquals(140, effects.get(0).getDuration());
        assertSame(MobEffects.POISON, effects.get(1).getEffect());
        assertEquals(240, effects.get(1).getDuration());
        assertTrue(ItemStack.matches(original, stack));
        assertTrue(StewInfo.getEffectLines(stack, 20.0F).get(0).getString().endsWith("0:07"));
        assertTrue(StewInfo.getEffectLines(stack, 20.0F).get(1).getString().endsWith("0:12"));

        effects.get(0).update(new MobEffectInstance(MobEffects.NIGHT_VISION, 280));
        assertEquals(140, StewInfo.getEffects(stack).get(0).getDuration());
        assertTrue(ItemStack.matches(original, stack));
    }

    @Test
    public void ignoresOtherItemsEvenWhenTheyHaveStewEffects() {
        ItemStack other = new ItemStack(Items.MUSHROOM_STEW);
        other.set(DataComponents.SUSPICIOUS_STEW_EFFECTS,
                new SuspiciousStewEffects(List.of(entry(MobEffects.POISON, 100))));

        assertTrue(StewInfo.getEffects(other).isEmpty());
        assertTrue(StewInfo.getEffectLines(other, 20.0F).isEmpty());
        assertTrue(StewInfo.getEffects(ItemStack.EMPTY).isEmpty());
    }

    @Test
    public void handlesAbsentEmptyAndRemovedEffectComponents() {
        ItemStack absent = new ItemStack(Items.SUSPICIOUS_STEW);
        assertTrue(StewInfo.getEffects(absent).isEmpty());
        assertTrue(StewInfo.getEffects(stew()).isEmpty());

        ItemStack removed = stew(entry(MobEffects.SATURATION, 7));
        removed.remove(DataComponents.SUSPICIOUS_STEW_EFFECTS);
        assertTrue(StewInfo.getEffects(removed).isEmpty());
        assertTrue(StewInfo.getEffectLines(removed, 20.0F).isEmpty());
    }

    @Test
    public void formatsDurationsUsingTheWorldTickRate() {
        ItemStack stack = stew(entry(MobEffects.BLINDNESS, 140));

        assertTrue(StewInfo.getEffectLines(stack, 20.0F).getFirst().getString().endsWith("0:07"));
        assertTrue(StewInfo.getEffectLines(stack, 10.0F).getFirst().getString().endsWith("0:14"));
    }

    @Test
    public void appendsSurvivalTooltipButKeepsVanillaCreativeTooltip() {
        ItemStack stack = stew(entry(MobEffects.NIGHT_VISION, 140));
        List<Component> survival = new ArrayList<>(List.of(Component.literal("Stew")));
        ClientEvents.onItemTooltip(new ItemTooltipEvent(stack, null, survival,
                new TooltipFlag.Default(false, false), Item.TooltipContext.EMPTY));
        assertEquals(2, survival.size());
        assertTrue(survival.get(1).getString().endsWith("0:07"));

        List<Component> creative = new ArrayList<>(List.of(
                Component.literal("Stew"), Component.literal("Vanilla effect")));
        ClientEvents.onItemTooltip(new ItemTooltipEvent(stack, null, creative,
                new TooltipFlag.Default(false, true), Item.TooltipContext.EMPTY));
        assertEquals(2, creative.size());
        assertEquals("Vanilla effect", creative.get(1).getString());
    }

    private static SuspiciousStewEffects.Entry entry(Holder<MobEffect> effect, int duration) {
        return new SuspiciousStewEffects.Entry(effect, duration);
    }

    private static ItemStack stew(SuspiciousStewEffects.Entry... entries) {
        ItemStack stack = new ItemStack(Items.SUSPICIOUS_STEW);
        stack.set(DataComponents.SUSPICIOUS_STEW_EFFECTS, new SuspiciousStewEffects(List.of(entries)));
        return stack;
    }
}
