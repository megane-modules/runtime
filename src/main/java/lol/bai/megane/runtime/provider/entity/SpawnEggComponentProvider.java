package lol.bai.megane.runtime.provider.entity;

import mcp.mobius.waila.api.IEntityAccessor;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.component.ItemComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;

import static lol.bai.megane.runtime.util.MeganeUtils.config;

import org.jetbrains.annotations.Nullable;

public class SpawnEggComponentProvider implements IEntityComponentProvider {

    @Override
    public @Nullable ITooltipComponent getIcon(IEntityAccessor accessor, IPluginConfig config) {
        return config().getSpawnEgg() ? new ItemComponent(SpawnEggItem.forEntity(accessor.getEntity().getType())) : null;
    }

}
