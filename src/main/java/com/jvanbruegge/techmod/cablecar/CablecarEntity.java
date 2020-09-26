package com.jvanbruegge.techmod.cablecar;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class CablecarEntity extends Entity {
    private static final DataParameter<Direction> HEADING = EntityDataManager.createKey(CablecarEntity.class, DataSerializers.DIRECTION);
    public static final float movementSpeed = 0.6f / 20f;

    public CablecarEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
        this.setNoGravity(true);
        this.noClip = true;
    }

    @Override
    protected void registerData() {
        dataManager.register(HEADING, Direction.NORTH);
    }

    public void setHeading(Direction heading) {
        dataManager.set(HEADING, heading);
    }

    @Override
    public void tick() {
        super.tick();

        float offsetX = 0f;
        float offsetZ = 0f;

        switch (this.dataManager.get(HEADING)) {
            case NORTH: offsetZ = -1f; break;
            case SOUTH: offsetZ = 1f; break;
            case WEST: offsetX = -1f; break;
            case EAST: offsetX = 1f; break;
        }

        this.setMotion(offsetX * movementSpeed, 0, offsetZ * movementSpeed);
        this.move(MoverType.SELF, this.getMotion());
    }

    @Override
    protected void readAdditional(CompoundNBT compound) {
        this.dataManager.set(HEADING, Direction.byIndex(compound.getInt("heading")));
    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {
        compound.putInt("heading", this.dataManager.get(HEADING).getIndex());
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
