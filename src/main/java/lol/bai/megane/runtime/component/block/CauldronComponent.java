package lol.bai.megane.runtime.component.block;

import java.util.List;

import lol.bai.megane.api.provider.CauldronProvider;
import lol.bai.megane.runtime.registry.Registrar;
import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.IWailaConfig;
import mcp.mobius.waila.api.WailaConstants;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.text.LiteralText;

public class CauldronComponent extends FluidComponent {

    @Override
    protected void append(ITooltip tooltip, IBlockAccessor accessor) {
        BlockState state = accessor.getBlockState();
        List<CauldronProvider> providers = Registrar.CAULDRON.get(state.getBlock());
        for (CauldronProvider provider : providers) {
            provider.setContext(accessor.getWorld(), accessor.getPosition(), accessor.getPlayer(), null);
            if (provider.hasFluids()) {
                Fluid fluid = provider.getFluid(0);
                if (fluid != null && !fluid.matchesType(Fluids.EMPTY)) {
                    tooltip.set(WailaConstants.OBJECT_NAME_TAG, new LiteralText(
                        IWailaConfig.get().getFormatting().formatBlockName(I18n.translate(Blocks.CAULDRON.getTranslationKey()))));
                    addFluid(tooltip, accessor, DEFAULT_TAG, fluid, provider.getStored(0), provider.getMax(0));
                    return;
                }
            }
        }
    }

}
