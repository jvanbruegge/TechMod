package com.jvanbruegge.techmod;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod(TechMod.MODID)
@Mod.EventBusSubscriber(modid = TechMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class TechMod {
    public static final String MODID = "techmod";

    @SubscribeEvent
    public static void registerRenderers(final FMLClientSetupEvent event) {
    }
}
