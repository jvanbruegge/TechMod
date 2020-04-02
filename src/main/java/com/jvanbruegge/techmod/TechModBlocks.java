package com.jvanbruegge.techmod;

import com.jvanbruegge.techmod.cablecar.CablecarDeployerBlock;
import com.jvanbruegge.techmod.cablecar.CablecarTrackBlock;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

import java.util.Arrays;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = TechMod.MODID, bus = Bus.MOD)
public enum TechModBlocks {
    CablecarTrack(new CablecarTrackBlock()),
    CablecarDeployer(new CablecarDeployerBlock());

    private TechModBlock block;
    private TechModBlocks(TechModBlock block) {
        this.block = block;
    }

    public TechModBlock getBlock() {
        return this.block;
    }

    public static TechModBlock[] blockValues() {
        TechModBlock[] blocks = new TechModBlock[TechModBlocks.values().length];
        int i = 0;
        for(TechModBlocks x : TechModBlocks.values()) {
            blocks[i++] = x.getBlock();
        }
        return blocks;
    }

    @SubscribeEvent
    public static void register(RegistryEvent.Register<Block> event) {
        for(TechModBlock block : TechModBlocks.blockValues()) {
            block.register(event);
        }
    }

    @SubscribeEvent
    public static void registerItemBlock(RegistryEvent.Register<Item> event) {
        for(TechModBlock block : TechModBlocks.blockValues()) {
            block.registerBlockItem(event);
        }
    }
}
