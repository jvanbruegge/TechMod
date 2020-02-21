package com.jvanbruegge.techmod;

import com.jvanbruegge.techmod.cablecar.CablecarTrackBlock;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(modid = TechMod.MODID, bus = Bus.MOD)
public class TechModBlocks {
    //public static final CableCarTrack track = new CableCarTrack();
    public static final CablecarTrackBlock deployer = new CablecarTrackBlock();

    @SubscribeEvent
    public static void register(RegistryEvent.Register<Block> event) {
        deployer.register(event);
    }

    @SubscribeEvent
    public static void registerItemBlock(RegistryEvent.Register<Item> event) {
        deployer.registerBlockItem(event);
    }
}
