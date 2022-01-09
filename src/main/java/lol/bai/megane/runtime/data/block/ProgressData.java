package lol.bai.megane.runtime.data.block;

import java.util.List;

import lol.bai.megane.runtime.registry.Registrar;
import lol.bai.megane.api.provider.ProgressProvider;
import lol.bai.megane.runtime.util.MeganeUtils;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import static lol.bai.megane.runtime.util.Keys.P_HAS;
import static lol.bai.megane.runtime.util.Keys.P_I_COUNT;
import static lol.bai.megane.runtime.util.Keys.P_I_ID;
import static lol.bai.megane.runtime.util.Keys.P_I_NBT;
import static lol.bai.megane.runtime.util.Keys.P_I_SIZE;
import static lol.bai.megane.runtime.util.Keys.P_O_COUNT;
import static lol.bai.megane.runtime.util.Keys.P_O_ID;
import static lol.bai.megane.runtime.util.Keys.P_O_NBT;
import static lol.bai.megane.runtime.util.Keys.P_O_SIZE;
import static lol.bai.megane.runtime.util.Keys.P_PERCENT;
import static lol.bai.megane.runtime.util.MeganeUtils.EMPTY_TAG;

public class ProgressData extends BlockData {

    public ProgressData() {
        super(Registrar.PROGRESS, () -> MeganeUtils.config().progress);
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    void append(NbtCompound data, ServerPlayerEntity player, World world, BlockEntity blockEntity) {
        List<ProgressProvider> providers = Registrar.PROGRESS.get(blockEntity);
        for (ProgressProvider provider : providers) {
            provider.setContext(world, blockEntity.getPos(), player, blockEntity);
            if (provider.hasProgress()) {
                data.putBoolean(P_HAS, true);

                int inputSize = provider.getInputSlotCount();
                int outputSize = provider.getOutputSlotCount();

                int i = 0;
                for (int j = 0; j < inputSize; j++) {
                    ItemStack stack = provider.getInputStack(j);
                    i = addStack(data, i, stack, P_I_ID, P_I_COUNT, P_I_NBT);
                }
                data.putInt(P_I_SIZE, i);

                data.putInt(P_PERCENT, provider.getPercentage());

                i = 0;
                for (int j = 0; j < outputSize; j++) {
                    ItemStack stack = provider.getOutputStack(j);
                    i = addStack(data, i, stack, P_O_ID, P_O_COUNT, P_O_NBT);
                }
                data.putInt(P_O_SIZE, i);
                return;
            }
            if (provider.blockOtherProvider()) {
                return;
            }
        }
    }

    private int addStack(NbtCompound data, int i, ItemStack stack, String itemId, String countId, String nbtId) {
        if (stack.isEmpty()) {
            return i;
        }
        NbtCompound tag = stack.getNbt();
        data.putInt(itemId + i, Registry.ITEM.getRawId(stack.getItem()));
        data.putInt(countId + i, stack.getCount());
        data.put(nbtId + i, tag == null ? EMPTY_TAG : tag);
        i++;
        return i;
    }

}
