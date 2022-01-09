package lol.bai.megane.runtime.component.block;

import java.util.List;
import java.util.Map;

import lol.bai.megane.runtime.Megane;
import lol.bai.megane.runtime.registry.Registrar;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lol.bai.megane.api.provider.FluidInfoProvider;
import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.ITooltip;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import static lol.bai.megane.runtime.util.Keys.B_COLOR;
import static lol.bai.megane.runtime.util.Keys.B_LONG;
import static lol.bai.megane.runtime.util.Keys.B_MAX;
import static lol.bai.megane.runtime.util.Keys.B_PREFIX;
import static lol.bai.megane.runtime.util.Keys.B_STORED;
import static lol.bai.megane.runtime.util.Keys.B_TL;
import static lol.bai.megane.runtime.util.Keys.B_UNIT;
import static lol.bai.megane.runtime.util.Keys.F_HAS;
import static lol.bai.megane.runtime.util.Keys.F_ID;
import static lol.bai.megane.runtime.util.Keys.F_MAX;
import static lol.bai.megane.runtime.util.Keys.F_SIZE;
import static lol.bai.megane.runtime.util.Keys.F_STORED;
import static lol.bai.megane.runtime.util.MeganeUtils.CONFIG;
import static lol.bai.megane.runtime.util.MeganeUtils.config;
import static lol.bai.megane.runtime.util.MeganeUtils.id;
import static lol.bai.megane.runtime.util.MeganeUtils.fluidName;

public class FluidComponent extends BlockComponent {

    private static final Identifier DEFAULT = id("default");

    static final NbtCompound DEFAULT_TAG = new NbtCompound();
    static final Int2ObjectOpenHashMap<NbtCompound> TAGS = new Int2ObjectOpenHashMap<>();

    static {
        DEFAULT_TAG.putBoolean(B_TL, false);
        DEFAULT_TAG.putString(B_UNIT, "mB");
    }

    public FluidComponent() {
        super(() -> config().fluid);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void addFluid(ITooltip tooltip, IBlockAccessor accessor, NbtCompound nbt, Fluid fluid, double stored, double max) {
        BlockPos pos = accessor.getPosition();
        World world = accessor.getWorld();

        Map<Identifier, Integer> colors = config().fluid.getColors();
        boolean expand = accessor.getPlayer().isSneaking() && config().fluid.isExpandWhenSneak();

        Identifier id = Registry.FLUID.getId(fluid);
        List<FluidInfoProvider> providers = Registrar.FLUID_INFO.get(fluid);
        FluidInfoProvider provider = providers.isEmpty() ? null : providers.get(0);

        if (provider != null) {
            provider.setContext(world, pos, accessor.getPlayer(), fluid);
        }

        int color;
        if (colors.containsKey(id)) {
            color = colors.get(id);
        } else {
            color = provider == null
                ? colors.computeIfAbsent(DEFAULT, s -> 0x0D0D59)
                : provider.getColor() & 0xFFFFFF;
            colors.put(id, color);
            CONFIG.save();
        }

        String name = provider == null ? fluidName(fluid) : provider.getName().getString();

        nbt.putInt(B_COLOR, color);
        nbt.putDouble(B_STORED, stored);
        nbt.putDouble(B_MAX, max);
        nbt.putBoolean(B_LONG, expand);
        nbt.putString(B_PREFIX, name);

        tooltip.addDrawable(Megane.BAR, nbt);
    }

    @Override
    protected void append(ITooltip tooltip, IBlockAccessor accessor) {
        NbtCompound data = accessor.getServerData();
        if (data.getBoolean(F_HAS)) {
            for (int i = 0; i < data.getInt(F_SIZE); i++) {
                double stored = data.getDouble(F_STORED + i);
                if (stored == 0)
                    continue;
                double max = data.getDouble(F_MAX + i);
                Fluid fluid = Registry.FLUID.get(data.getInt(F_ID + i));
                addFluid(tooltip, accessor, TAGS.computeIfAbsent(i, k -> DEFAULT_TAG.copy()), fluid, stored, max);
            }
        }
    }

}
