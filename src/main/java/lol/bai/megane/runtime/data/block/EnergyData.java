package lol.bai.megane.runtime.data.block;

import java.util.List;

import lol.bai.megane.runtime.registry.Registrar;
import lol.bai.megane.api.provider.EnergyProvider;
import lol.bai.megane.runtime.util.MeganeUtils;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

import static lol.bai.megane.runtime.util.Keys.E_HAS;
import static lol.bai.megane.runtime.util.Keys.E_MAX;
import static lol.bai.megane.runtime.util.Keys.E_STORED;

public class EnergyData extends BlockData {

    public EnergyData() {
        super(Registrar.ENERGY, () -> MeganeUtils.config().energy);
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    void append(NbtCompound data, ServerPlayerEntity player, World world, BlockEntity blockEntity) {
        List<EnergyProvider> providers = Registrar.ENERGY.get(blockEntity);
        for (EnergyProvider provider : providers) {
            provider.setContext(world, blockEntity.getPos(), player, blockEntity);
            if (provider.hasEnergy()) {
                data.putBoolean(E_HAS, true);
                data.putDouble(E_STORED, provider.getStored());
                data.putDouble(E_MAX, provider.getMax());
                return;
            }
            if (provider.blockOtherProvider()) {
                return;
            }
        }
    }

}
