package com.jvanbruegge.techmod;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class TechModBlock extends Block {
    public final ItemGroup itemGroup;

    public TechModBlock(Properties properties, String registryName, ItemGroup group) {
        super(properties);
        this.setRegistryName(TechMod.MODID, registryName);
        this.itemGroup = group;
    }

    public void register(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(this);
    }

    public void registerBlockItem(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(
                new BlockItem(this, new Item.Properties().group(itemGroup))
                        .setRegistryName(this.getRegistryName())
        );
    }

    public static List<BlockPos> getNeighborBlocks(World world, BlockPos pos, Class<? extends Block> clazz) {
        return StreamSupport.stream(((Iterable<Direction>) () -> Direction.Plane.HORIZONTAL.iterator()).spliterator(), false)
                .map(dir -> pos.offset(dir))
                .filter(p -> world.getBlockState(p).getBlock().getClass() == clazz)
                .collect(Collectors.toList());
    }
}
