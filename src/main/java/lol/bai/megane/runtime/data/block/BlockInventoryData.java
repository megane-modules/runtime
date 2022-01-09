package lol.bai.megane.runtime.data.block;

import java.util.List;

import lol.bai.megane.api.provider.ItemProvider;
import lol.bai.megane.runtime.registry.Registrar;
import lol.bai.megane.runtime.util.MeganeUtils;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

import static lol.bai.megane.runtime.util.Keys.I_COUNT;
import static lol.bai.megane.runtime.util.Keys.I_HAS;
import static lol.bai.megane.runtime.util.Keys.I_ID;
import static lol.bai.megane.runtime.util.Keys.I_NBT;
import static lol.bai.megane.runtime.util.Keys.I_SHOW;
import static lol.bai.megane.runtime.util.Keys.I_SIZE;
import static lol.bai.megane.runtime.util.MeganeUtils.EMPTY_TAG;
import static net.minecraft.util.registry.Registry.ITEM;

public class BlockInventoryData extends BlockData {

    public BlockInventoryData() {
        super(Registrar.INVENTORY, () -> MeganeUtils.config().inventory);
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    void append(NbtCompound data, ServerPlayerEntity player, World world, BlockEntity blockEntity) {
        data.putBoolean(I_SHOW, MeganeUtils.config().inventory.isItemCount());
        List<ItemProvider> providers = Registrar.INVENTORY.get(blockEntity);
        for (ItemProvider provider : providers) {
            provider.setContext(world, blockEntity.getPos(), player, blockEntity);
            if (provider.hasItems()) {
                data.putBoolean(I_HAS, true);
                int size = provider.getSlotCount();
                int i = 0;
                for (int j = 0; j < size; j++) {
                    ItemStack stack = provider.getStack(j);
                    if (stack.isEmpty()) {
                        continue;
                    }
                    data.putInt(I_ID + i, ITEM.getRawId(stack.getItem()));
                    data.putInt(I_COUNT + i, stack.getCount());
                    NbtCompound nbt = stack.getNbt();
                    data.put(I_NBT + i, nbt == null || !MeganeUtils.config().inventory.isNbt() ? EMPTY_TAG : nbt);
                    i++;
                }
                data.putInt(I_SIZE, i);
                return;
            }
            if (provider.blockOtherProvider()) {
                return;
            }
        }
    }

}
