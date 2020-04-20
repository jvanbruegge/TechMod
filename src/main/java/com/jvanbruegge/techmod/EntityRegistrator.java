package com.jvanbruegge.techmod;

import com.jvanbruegge.techmod.cablecar.CablecarEntity;
import com.jvanbruegge.techmod.cablecar.CablecarRenderer;
import lombok.Getter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = TechMod.MODID, bus = Bus.MOD)
public enum EntityRegistrator {
    Cablecar(CablecarEntity::new, EntityClassification.MISC, CablecarRenderer::new, "cablecar");

    @Getter
    private final String registryName;
    @Getter
    private final EntityType<?> entityType;
    private final IRenderFactory<?> renderer;

    EntityRegistrator(EntityType.IFactory<?> createEntity, EntityClassification classification, IRenderFactory renderer, String registryName) {
        this(
                EntityType.Builder
                    .create(createEntity, classification)
                    .build(registryName)
                    .setRegistryName(TechMod.MODID, registryName),
                renderer,
                registryName
        );
    }
    EntityRegistrator(EntityType<?> entityType, IRenderFactory<?> renderer, String registryName) {
        this.entityType = entityType;
        this.registryName = registryName;
        this.renderer = renderer;
    }

    @SubscribeEvent
    public static void registerEntities(RegistryEvent.Register<EntityType<?>> event) {
        for(EntityType<?> type : Utils.getEnumValues(EntityRegistrator.class, EntityRegistrator::getEntityType)) {
            event.getRegistry().register(type);
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends Entity> IRenderFactory<T> getRenderer() {
        return (IRenderFactory<T>) renderer;
    }

    @SubscribeEvent
    public static void registerRenderers(FMLClientSetupEvent event) {
        for(EntityRegistrator r : values()) {
            RenderingRegistry.registerEntityRenderingHandler(r.getEntityType(), r.getRenderer());
        }
    }
}
