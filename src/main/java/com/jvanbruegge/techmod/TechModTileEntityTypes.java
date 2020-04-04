package com.jvanbruegge.techmod;

import com.jvanbruegge.techmod.cablecar.CablecarDeployerTileEntity;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = TechMod.MODID, bus = Bus.MOD)
public enum TechModTileEntityTypes {
    CablecarDeployer(CablecarDeployerTileEntity::new, TechModBlocks.CablecarDeployer.getBlock(), "cablecar_deployer");

    private TileEntityType<?> entityType;
    private String registryName;
    private TechModTileEntityTypes(Supplier factory, Block block, String registryName) {
        this(TileEntityType.Builder.create(factory, block), registryName);
    }
    private TechModTileEntityTypes(TileEntityType.Builder builder, String registryName) {
        this.entityType = builder.build(null);
        this.entityType.setRegistryName(TechMod.MODID, registryName);
    }

    public TileEntityType getType() {
        return this.entityType;
    }

    public static TileEntityType[] tileEntityTypes() {
        TileEntityType[] types = new TileEntityType[TechModTileEntityTypes.values().length];
        int i = 0;
        for(TechModTileEntityTypes t : TechModTileEntityTypes.values()) {
            types[i++] = t.getType();
        }
        return types;
    }

    @SubscribeEvent
    public static void register(RegistryEvent.Register<TileEntityType<?>> event) {
        for(TileEntityType t : TechModTileEntityTypes.tileEntityTypes()) {
            event.getRegistry().register(t);
        }
    }
}
