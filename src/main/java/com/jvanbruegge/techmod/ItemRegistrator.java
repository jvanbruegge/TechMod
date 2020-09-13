package com.jvanbruegge.techmod;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(modid = TechMod.MODID, bus = Bus.MOD)
public enum ItemRegistrator {
    Cablecar("cablecar", ItemGroup.TRANSPORTATION);

    private final String name;
    private final Item item;
    private final ItemGroup group;

    ItemRegistrator(String name, ItemGroup group) {
        this.name = name;
        this.group = group;
        this.item = new Item(new Item.Properties().group(group))
                .setRegistryName(TechMod.MODID, name);
    }
    
    public Item getItem() { return item; }
    public ItemGroup getGroup() {
        return group;
    }
    public String getName() {
        return name;
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        for(Item i : Utils.getEnumValues(ItemRegistrator.class, ItemRegistrator::getItem)) {
            event.getRegistry().register(i);
        }
    }
}
