package lol.bai.megane.runtime.component.block;

import java.util.List;
import java.util.Map;

import lol.bai.megane.runtime.config.MeganeConfig;
import lol.bai.megane.runtime.Megane;
import lol.bai.megane.runtime.provider.EnergyInfoProvider;
import lol.bai.megane.runtime.registry.Registrar;
import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.ITooltip;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.registry.Registry;

import static lol.bai.megane.runtime.util.Keys.B_COLOR;
import static lol.bai.megane.runtime.util.Keys.B_LONG;
import static lol.bai.megane.runtime.util.Keys.B_MAX;
import static lol.bai.megane.runtime.util.Keys.B_PREFIX;
import static lol.bai.megane.runtime.util.Keys.B_STORED;
import static lol.bai.megane.runtime.util.Keys.B_UNIT;
import static lol.bai.megane.runtime.util.Keys.E_HAS;
import static lol.bai.megane.runtime.util.Keys.E_MAX;
import static lol.bai.megane.runtime.util.Keys.E_STORED;
import static lol.bai.megane.runtime.util.MeganeUtils.CONFIG;
import static lol.bai.megane.runtime.util.MeganeUtils.MODID;
import static lol.bai.megane.runtime.util.MeganeUtils.config;

public class EnergyComponent extends BlockComponent {

    public static final NbtCompound TAG = new NbtCompound();

    public EnergyComponent() {
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

            TAG.putString(B_PREFIX, provider != null ? provider.name().getString() : I18n.translate("megane.energy"));
            TAG.putInt(B_COLOR, color);
            TAG.putDouble(B_STORED, stored);
            TAG.putDouble(B_MAX, max);
            TAG.putBoolean(B_LONG, expand);
            TAG.putString(B_UNIT, unit);

            tooltip.addDrawable(Megane.BAR, TAG);
        }
    }

}
