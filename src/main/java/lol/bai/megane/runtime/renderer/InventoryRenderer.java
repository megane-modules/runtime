package lol.bai.megane.runtime.renderer;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import lol.bai.megane.runtime.util.Keys;
import lol.bai.megane.runtime.util.MeganeUtils;
import mcp.mobius.waila.api.ICommonAccessor;
import mcp.mobius.waila.api.ITooltipRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

import static net.minecraft.util.registry.Registry.ITEM;

public class InventoryRenderer implements ITooltipRenderer {

    private final List<ItemStack> stacks = new ArrayList<>();

    private int w;
    private int h;

    @Override
    public Dimension getSize(NbtCompound data, ICommonAccessor accessor) {
        w = data.getInt(Keys.I_MAX_W);
        h = data.getInt(Keys.I_MAX_H);
        int size = data.getInt(Keys.I_SIZE);
        boolean showCount = data.getBoolean(Keys.I_SHOW);

        stacks.clear();
        for (int i = 0; i < size; i++) {
            Item item = ITEM.get(data.getInt(Keys.I_ID + i));
            int count = showCount ? data.getInt(Keys.I_COUNT + i) : 1;
            NbtCompound nbt = (NbtCompound) data.get(Keys.I_NBT + i);
            if (count <= 0)
                continue;
            ItemStack stack = new ItemStack(item, count);
            stack.setNbt(nbt);
            Optional<ItemStack> optional = stacks
                .stream()
                .filter(j -> j.getItem() == stack.getItem() && j.getOrCreateNbt().equals(nbt))
                .findFirst();
            if (optional.isPresent()) {
                if (showCount) {
                    optional.get().increment(stack.getCount());
                }
            } else {
                stacks.add(stack);
            }
        }

        stacks.sort(Comparator.comparingInt(ItemStack::getCount).reversed());

        if (stacks.size() == 0)
            return new Dimension();
        return new Dimension(18 * Math.min(stacks.size(), w), 18 * Math.min((stacks.size() + w - 1) / w, h) + 2);
    }

    @Override
    public void draw(MatrixStack matrices, NbtCompound data, ICommonAccessor accessor, int x, int y) {
        for (int i = 0; i < Math.min(stacks.size(), w * h); i++) {
            MeganeUtils.drawStack(stacks.get(i), x + (18 * (i % w)) + 1, y + (18 * (i / w)));
        }
    }

}
