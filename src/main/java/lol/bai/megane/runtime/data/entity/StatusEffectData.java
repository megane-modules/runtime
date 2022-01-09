package lol.bai.megane.runtime.data.entity;

import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

import lol.bai.megane.runtime.util.Keys;
import lol.bai.megane.runtime.util.MeganeUtils;

public class StatusEffectData extends EntityData {

    public StatusEffectData() {
        super(() -> MeganeUtils.config().effect);
    }

    @Override
    void append(NbtCompound data, ServerPlayerEntity player, World world, Entity entity) {
        if (entity instanceof LivingEntity livingEntity) {
            List<StatusEffectInstance> effects = livingEntity
                .getStatusEffects()
                .stream()
                .filter(t -> t.shouldShowParticles() || MeganeUtils.config().effect.getHidden())
                .collect(Collectors.toList());

            data.putInt(Keys.S_SIZE, effects.size());

            for (int i = 0; i < effects.size(); i++) {
                StatusEffectInstance effect = effects.get(i);
                data.putInt(Keys.S_ID + i, StatusEffect.getRawId(effect.getEffectType()));
                data.putInt(Keys.S_LV + i, MeganeUtils.config().effect.getLevel() ? effect.getAmplifier() : 0);
            }
        }
    }

}
