package com.jvanbruegge.techmod;

import com.jvanbruegge.techmod.cablecar.*;
import net.minecraft.block.Block;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.network.IContainerFactory;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = TechMod.MODID, bus = Bus.MOD)
public enum Registrator {
    CablecarTrack("cablecar_track", new CablecarTrackBlock(), ItemGroup.TRANSPORTATION),
    CablecarDeployer("cablecar_deployer", new CablecarDeployerBlock(), ItemGroup.TRANSPORTATION, CablecarDeployerTileEntity::new, CablecarDeployerContainer::new);

    private final String name;
    private final Block block;
    private final ItemGroup group;
    private final TileEntityType<?> entityType;
    private final ContainerType<?> containerType;
    Registrator(String name, Block block, ItemGroup group) {
        this(name, block, group, null, (ContainerType<?>)null);
    }
    Registrator(String name, Block block, ItemGroup group, Supplier<? extends TileEntity> entity, IContainerFactory<?> container) {
       this(name, block, group, TileEntityType.Builder.create(entity, block).build(null), IForgeContainerType.create(container));
    }
    Registrator(String name, Block block, ItemGroup group, TileEntityType<?> entityType, ContainerType<?> containerType) {
        this.name = name;
        this.block = block;
        this.group = group;
        this.entityType = entityType;
        this.containerType = containerType;

        this.block.setRegistryName(name);
        if(this.entityType != null) {
            this.entityType.setRegistryName(name);
        }
        if(this.containerType != null) {
            this.containerType.setRegistryName(name);
        }
    }

    public Block getBlock() {
        return block;
    }

    public TileEntityType<?> getTileEntityType() {
        return entityType;
    }

    public ContainerType<?> getContainerType() {
        return containerType;
    }

    public ItemGroup getGroup() {
        return group;
    }

    public String getName() {
        return name;
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        for(Block b : Utils.getEnumValues(Registrator.class, Registrator::getBlock)) {
            event.getRegistry().register(b);
        }
    }

    @SubscribeEvent
    public static void registerBlockItems(RegistryEvent.Register<Item> event) {
        for(Registrator val : values()) {
            event.getRegistry().register(
                    new BlockItem(val.getBlock(), new Item.Properties().group(val.getGroup()))
                            .setRegistryName(TechMod.MODID, val.getName())
            );
        }
    }

    @SubscribeEvent
    public static void registerTileEntityTypes(RegistryEvent.Register<TileEntityType<?>> event) {
        for(TileEntityType<?> type : Utils.getEnumValues(Registrator.class, Registrator::getTileEntityType)) {
            if(type != null) event.getRegistry().register(type);
        }
    }

    @SubscribeEvent
    public static void registerContainerTypes(RegistryEvent.Register<ContainerType<?>> event) {
        for(ContainerType<?> type : Utils.getEnumValues(Registrator.class, Registrator::getContainerType)) {
            if(type != null) {
                event.getRegistry().register(type);
            }
        }
    }
}
