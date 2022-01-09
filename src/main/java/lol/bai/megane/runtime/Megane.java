package lol.bai.megane.runtime;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import lol.bai.megane.runtime.component.block.BeaconComponent;
import lol.bai.megane.runtime.component.block.BlockInventoryComponent;
import lol.bai.megane.runtime.component.block.CauldronComponent;
import lol.bai.megane.runtime.component.block.EnergyComponent;
import lol.bai.megane.runtime.component.block.FluidComponent;
import lol.bai.megane.runtime.component.block.ProgressComponent;
import lol.bai.megane.runtime.component.entity.EntityInventoryComponent;
import lol.bai.megane.runtime.component.entity.PlayerHeadComponent;
import lol.bai.megane.runtime.component.entity.SpawnEggComponent;
import lol.bai.megane.runtime.component.entity.StatusEffectComponent;
import lol.bai.megane.runtime.data.block.BeaconData;
import lol.bai.megane.runtime.data.block.BlockInventoryData;
import lol.bai.megane.runtime.data.block.EnergyData;
import lol.bai.megane.runtime.data.block.FluidData;
import lol.bai.megane.runtime.data.block.ProgressData;
import lol.bai.megane.runtime.data.entity.EntityInventoryData;
import lol.bai.megane.runtime.data.entity.StatusEffectData;
import lol.bai.megane.runtime.registry.Registrar;
import lol.bai.megane.runtime.renderer.BarRenderer;
import lol.bai.megane.runtime.renderer.InventoryRenderer;
import lol.bai.megane.runtime.renderer.ProgressRenderer;
import lol.bai.megane.runtime.renderer.StatusEffectRenderer;
import lol.bai.megane.api.MeganeModule;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.VersionParsingException;
import net.fabricmc.loader.api.metadata.CustomValue;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.fabricmc.loader.api.metadata.version.VersionPredicate;
import net.minecraft.block.AbstractCauldronBlock;
import net.minecraft.block.BeaconBlock;
import net.minecraft.block.Block;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import static lol.bai.megane.runtime.util.MeganeUtils.LOGGER;
import static lol.bai.megane.runtime.util.MeganeUtils.MODULE_CONFIG;
import static lol.bai.megane.runtime.util.MeganeUtils.id;
import static mcp.mobius.waila.api.TooltipPosition.HEAD;
import static mcp.mobius.waila.api.TooltipPosition.TAIL;

public class Megane implements IWailaPlugin {

    public static final Identifier INVENTORY = id("inventory");
    public static final Identifier BAR = id("bar");
    public static final Identifier PROGRESS = id("progress");
    public static final Identifier EFFECT = id("effect");

    private static final Class<Block> BLOCK = Block.class;
    private static final Class<LivingEntity> ENTITY = LivingEntity.class;

    @Override
    public void register(IRegistrar r) {
        // Renderer
        r.addRenderer(INVENTORY, new InventoryRenderer());
        r.addRenderer(BAR, new BarRenderer());
        r.addRenderer(PROGRESS, new ProgressRenderer());
        r.addRenderer(EFFECT, new StatusEffectRenderer());

        // --- BLOCK ---
        // Component
        r.addComponent(new EnergyComponent(), HEAD, BLOCK);
        r.addComponent(new FluidComponent(), HEAD, BLOCK);
        r.addComponent(new CauldronComponent(), HEAD, AbstractCauldronBlock.class);

        r.addComponent(new BlockInventoryComponent(), TAIL, BLOCK);
        r.addComponent(new ProgressComponent(), TAIL, BLOCK);
        r.addComponent(new BeaconComponent(), TAIL, BeaconBlock.class);


        // Server Data
        r.addBlockData(new BlockInventoryData(), BLOCK);
        r.addBlockData(new EnergyData(), BLOCK);
        r.addBlockData(new FluidData(), BLOCK);
        r.addBlockData(new ProgressData(), BLOCK);
        r.addBlockData(new BeaconData(), BeaconBlock.class);

        // --- ENTITY ---
        r.addDisplayItem(new SpawnEggComponent(), ENTITY);
        r.addDisplayItem(new PlayerHeadComponent(), PlayerEntity.class);

        // Component
        r.addComponent(new EntityInventoryComponent(), TAIL, ENTITY);
        r.addComponent(new StatusEffectComponent(), TAIL, ENTITY);

        // Server Data
        r.addEntityData(new EntityInventoryData(), ENTITY);
        r.addEntityData(new StatusEffectData(), ENTITY);

        // Modules
        FabricLoader loader = FabricLoader.getInstance();

        loader.getAllMods().forEach(mod -> {
            ModMetadata metadata = mod.getMetadata();
            String modId = metadata.getId();
            if (metadata.containsCustomValue("megane:modules")) {
                metadata.getCustomValue("megane:modules").getAsArray().forEach(value -> {
                    boolean satisfied = true;
                    String className;
                    if (value.getType() == CustomValue.CvType.OBJECT) {
                        CustomValue.CvObject object = value.getAsObject();
                        className = object.get("init").getAsString();
                        if (object.containsKey("depends")) {
                            for (Map.Entry<String, CustomValue> dep : object.get("depends").getAsObject()) {
                                Optional<ModContainer> optional = loader.getModContainer(dep.getKey());
                                satisfied = optional.isPresent();
                                if (satisfied) {
                                    try {
                                        satisfied = VersionPredicate.parse(dep.getValue().getAsString()).test(optional.get().getMetadata().getVersion());
                                    } catch (VersionParsingException e) {
                                        LOGGER.error("Failed to parse dependency version for module " + className, e);
                                        satisfied = false;
                                    }
                                }
                                if (!satisfied) {
                                    break;
                                }
                            }
                        }
                    } else {
                        className = value.getAsString();
                    }
                    satisfied = satisfied && MODULE_CONFIG.get().modules
                        .computeIfAbsent(modId, s -> new HashMap<>())
                        .computeIfAbsent(className, s -> true);
                    MODULE_CONFIG.save();
                    if (satisfied)
                        try {
                            LOGGER.info("[megane] Loading {} from {}", className, modId);
                            MeganeModule entry = (MeganeModule) Class.forName(className).getDeclaredConstructor().newInstance();
                            entry.registerCommon(Registrar.INSTANCE);
                            if (loader.getEnvironmentType() == EnvType.CLIENT)
                                entry.registerClient(Registrar.INSTANCE);
                        } catch (Throwable t) {
                            LOGGER.error("[megane] error when loading {} from {}", className, modId);
                            t.printStackTrace();
                        }
                });
            }
        });
    }

}
