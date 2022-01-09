package lol.bai.megane.runtime.renderer;

import java.awt.Dimension;

import com.mojang.blaze3d.systems.RenderSystem;
import lol.bai.megane.runtime.util.Keys;
import lol.bai.megane.runtime.util.MeganeUtils;
import mcp.mobius.waila.api.ICommonAccessor;
import mcp.mobius.waila.api.ITooltipRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.StatusEffectSpriteManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.nbt.NbtCompound;

public class StatusEffectRenderer implements ITooltipRenderer {

    @Override
    public Dimension getSize(NbtCompound data, ICommonAccessor accessor) {
        int size = data.getInt(Keys.S_SIZE);
        for (int i = 0; i < size; i++) {
            int id = data.getInt(Keys.S_ID + i);
            if (id == -1) {
                size--;
                i--;
            }
        }
        if (size <= 0)
            return new Dimension();
        return new Dimension(size * 20, 20);
    }

    @Override
    public void draw(MatrixStack matrices, NbtCompound data, ICommonAccessor accessor, int x, int y) {
        StatusEffectSpriteManager manager = MinecraftClient.getInstance().getStatusEffectSpriteManager();

        int size = data.getInt(Keys.S_SIZE);
        for (int i = 0; i < size; i++) {
            String lv = data.getString(Keys.S_LV_STR + i);
            StatusEffect statusEffect = StatusEffect.byRawId(data.getInt(Keys.S_ID + i));
            if (statusEffect != null) {
                Sprite sprite = manager.getSprite(statusEffect);
                RenderSystem.setShaderTexture(0, sprite.getAtlas().getId());
                DrawableHelper.drawSprite(matrices, x + 20 * i, y, 0, 18, 18, sprite);
                MeganeUtils.textRenderer().drawWithShadow(matrices, lv, x + 20 + (20 * i) - MeganeUtils.textRenderer().getWidth(lv), y + 20 - MeganeUtils.textRenderer().fontHeight, 0xFFFFFF);
            } else {
                size--;
                i--;
            }
        }
    }

}
