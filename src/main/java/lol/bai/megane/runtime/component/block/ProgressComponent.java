package lol.bai.megane.runtime.component.block;

import lol.bai.megane.runtime.Megane;
import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.ITooltip;
import net.minecraft.nbt.NbtCompound;

import static lol.bai.megane.runtime.util.Keys.P_HAS;
import static lol.bai.megane.runtime.util.Keys.P_PERCENT;
import static lol.bai.megane.runtime.util.MeganeUtils.config;

public class ProgressComponent extends BlockComponent {

    public ProgressComponent() {
        super(() -> config().progress);
    }

    @Override
    protected void append(ITooltip tooltip, IBlockAccessor accessor) {
        NbtCompound data = accessor.getServerData();
        if (data.getBoolean(P_HAS) && (data.getInt(P_PERCENT) > 0 || config().progress.isShowWhenZero())) {
            tooltip.addDrawable(Megane.PROGRESS, data);
        }
    }

}
