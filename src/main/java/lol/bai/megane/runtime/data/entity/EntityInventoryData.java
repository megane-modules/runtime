package lol.bai.megane.runtime.data.entity;

import java.util.List;

import lol.bai.megane.api.provider.ItemProvider;
import lol.bai.megane.runtime.registry.Registrar;
import lol.bai.megane.runtime.util.Keys;
import lol.bai.megane.runtime.util.MeganeUtils;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

import static net.minecraft.util.registry.Registry.ITEM;

public class EntityInventoryData extends EntityData {

    public EntityInventoryData() {
        super(Registrar.INVENTORY, () -> MeganeUtils.config().entityInventory);
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    void append(NbtCompound data, ServerPlayerEntity player, World world, Entity entity) {
        List<ItemProvider> providers = Registrar.INVENTORY.get(entity);
        for (ItemProvider provider : providers) {
            provider.setContext(world, entity.getBlockPos(), player, entity);
            if (provider.hasItems()) {
                data.putBoolean(Keys.I_HAS, true);
                data.putBoolean(Keys.I_SHOW, MeganeUtils.config().entityInventory.isItemCount());
                int size = provider.getSlotCount();
                int i = 0;
                for (int j = 0; j < size; j++) {
                    ItemStack stack = provider.getStack(j);
                    if (stack.isEmpty()) {
                        continue;
                    }
                    data.putInt(Keys.I_ID + i, ITEM.getRawId(stack.getItem()));
                    data.putInt(Keys.I_COUNT + i, stack.getCount());
                    NbtCompound nbt = stack.getNbt();
                    data.put(Keys.I_NBT + i, nbt == null || !MeganeUtils.config().entityInventory.isNbt() ? MeganeUtils.EMPTY_TAG : nbt);
                    i++;
                }
                data.putInt(Keys.I_SIZE, i);
                return;
            }
            if (provider.blockOtherProvider()) {
                return;
            }
        }
    }

}
