package lol.bai.megane.runtime.provider.block;

import java.util.List;
import java.util.Map;

import lol.bai.megane.runtime.config.MeganeConfig;
import lol.bai.megane.runtime.registry.EnergyInfoProvider;
import lol.bai.megane.runtime.registry.Registrar;
import lol.bai.megane.runtime.component.BarComponent;
import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.component.PairComponent;
import mcp.mobius.waila.api.component.WrappedComponent;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.registry.Registry;

import static lol.bai.megane.runtime.util.Keys.E_HAS;
import static lol.bai.megane.runtime.util.Keys.E_MAX;
import static lol.bai.megane.runtime.util.Keys.E_STORED;
import static lol.bai.megane.runtime.util.MeganeUtils.CONFIG;
import static lol.bai.megane.runtime.util.MeganeUtils.MODID;
import static lol.bai.megane.runtime.util.MeganeUtils.config;

public class EnergyComponentProvider extends BlockComponentProvider {

    private static final TranslatableText ENERGY_NAME = new TranslatableText("megane.energy");

    public EnergyComponentProvider() {
        super(() -> config().energy);
    }

    @Override
    protected void append(ITooltip tooltip, IBlockAccessor accessor) {
        MeganeConfig.Energy energy = config().energy;
        Map<String, Integer> colors = energy.getColors();
        Map<String, String> units = energy.getUnits();
        NbtCompound data = accessor.getServerData();

        if (data.getBoolean(E_HAS)) {
            double stored = data.getDouble(E_STORED);
            double max = data.getDouble(E_MAX);

            String namespace = Registry.BLOCK.getId(accessor.getBlock()).getNamespace();
            boolean expand = accessor.getPlayer().isSneaking() && energy.isExpandWhenSneak();
            List<EnergyInfoProvider> providers = Registrar.ENERGY_INFO.get(namespace);
            EnergyInfoProvider provider = providers.isEmpty() ? null : providers.get(0);

            int color;
            if (colors.containsKey(namespace)) {
                color = colors.get(namespace);
            } else {
                color = provider == null
                    ? colors.computeIfAbsent(MODID, c -> 0x710C00)
                    : provider.color();
                colors.put(namespace, color);
                CONFIG.save();
            }

            String unit;
            if (units.containsKey(namespace)) {
                unit = units.get(namespace);
            } else {
                unit = provider == null
                    ? units.computeIfAbsent(MODID, a -> "E")
                    : provider.unit();
                units.put(namespace, unit);
                CONFIG.save();
            }

            tooltip.addLine(new PairComponent(
                new WrappedComponent(provider != null ? provider.name() : ENERGY_NAME),
                new BarComponent(color, stored, max, unit, expand)));
        }
    }

}
