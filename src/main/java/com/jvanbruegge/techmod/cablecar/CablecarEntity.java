package com.jvanbruegge.techmod.cablecar;

import com.jvanbruegge.techmod.BlockRegistrator;
import com.jvanbruegge.techmod.ItemRegistrator;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class CablecarEntity extends Entity {
    private static final DataParameter<Direction> HEADING = EntityDataManager.createKey(CablecarEntity.class, DataSerializers.DIRECTION);
    private static final DataParameter<Boolean> ON_RAIL = EntityDataManager.createKey(CablecarEntity.class, DataSerializers.BOOLEAN);
    public static final float movementSpeed = 0.6f / 20f;
    public static final float length = 0.75f;

    public CablecarEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
        this.setNoGravity(true);
        this.noClip = true;
    }

    @Override
    protected void registerData() {
        dataManager.register(HEADING, Direction.NORTH);
        dataManager.register(ON_RAIL, false);
    }

    public void setHeading(Direction heading) {
        dataManager.set(HEADING, heading);
    }
    public boolean isOnRail() { return this.dataManager.get(ON_RAIL); }

    @Override
    public void tick() {
        super.tick();
        if(!this.isAlive()) return;

        Block track = BlockRegistrator.CablecarTrack.getBlock();
        Block deployer = BlockRegistrator.CablecarDeployer.getBlock();

        Direction heading = this.dataManager.get(HEADING);
        int offsetX = heading.getXOffset();
        int offsetZ = heading.getZOffset();

        BlockPos front = new BlockPos(this.getPosX() + length/2 * offsetX, this.getPosY(), this.getPosZ() + length/2 * offsetZ);
        Block blockFront = world.getBlockState(front).getBlock();
        BlockPos back = new BlockPos(this.getPosX() - length/2 * offsetX, this.getPosY(), this.getPosZ() - length/2 * offsetZ);
        Block blockBack = world.getBlockState(back).getBlock();

        if(blockBack == track) {
            this.dataManager.set(ON_RAIL, true);
        }
        if (this.isOnRail() && !(blockFront instanceof CablecarConnectable)) {
            return;
        }

        if(blockBack == deployer && this.isOnRail()) {
            CablecarDeployerTileEntity entity = (CablecarDeployerTileEntity) world.getTileEntity(back);
            if(!entity.putCart()) {
                this.entityDropItem(ItemRegistrator.Cablecar.getItemStack(1));
            }
            this.remove();
        }

        Vec3d motion = new Vec3d(offsetX, 0, offsetZ)
                .normalize()
                .scale(movementSpeed);

        this.move(MoverType.SELF, motion);
    }

    @Override
    protected void readAdditional(CompoundNBT compound) {
        this.dataManager.set(HEADING, Direction.byIndex(compound.getInt("heading")));
        this.dataManager.set(ON_RAIL, compound.getBoolean("on_rail"));
    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {
        compound.putInt("heading", this.dataManager.get(HEADING).getIndex());
        compound.putBoolean("on_rail", this.dataManager.get(ON_RAIL));
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
