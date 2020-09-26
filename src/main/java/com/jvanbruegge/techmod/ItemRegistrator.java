package com.jvanbruegge.techmod;

import lombok.Getter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(modid = TechMod.MODID, bus = Bus.MOD)
public enum ItemRegistrator {
    Cablecar("cablecar", ItemGroup.TRANSPORTATION);

    @Getter
    private final String name;
    @Getter
    private final Item item;
    @Getter
    private final ItemGroup group;

    ItemRegistrator(String name, ItemGroup group) {
        this.name = name;
        this.group = group;
        this.item = new Item(new Item.Properties().group(group))
                .setRegistryName(TechMod.MODID, name);
    }
    
    public ItemStack getItemStack(int amount) {
        return new ItemStack(this.getItem(), amount);
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        for(Item i : Utils.getEnumValues(ItemRegistrator.class, ItemRegistrator::getItem)) {
            event.getRegistry().register(i);
        }
    }
}
