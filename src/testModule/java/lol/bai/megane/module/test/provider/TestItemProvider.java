package lol.bai.megane.module.test.provider;

import lol.bai.megane.api.provider.ItemProvider;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.NotNull;

public class TestItemProvider extends ItemProvider<ChestBlockEntity> {

    @Override
    public int getSlotCount() {
        return 13;
    }

    @Override
    public @NotNull ItemStack getStack(int slot) {
        return new ItemStack(Registry.ITEM.getRandom(getWorld().random));
    }

}
