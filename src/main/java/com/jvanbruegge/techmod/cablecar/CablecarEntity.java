package com.jvanbruegge.techmod.cablecar;

import com.jvanbruegge.techmod.BlockRegistrator;
import com.jvanbruegge.techmod.ItemRegistrator;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
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


    @Override
    public void tick() {
        super.tick();
        if(!this.isAlive()) return;

        Direction heading = this.dataManager.get(HEADING);
        int offsetX = heading.getXOffset();
        int offsetZ = heading.getZOffset();

        BlockPos pos = new BlockPos(this.getPosX() - 0.5 * offsetX, this.getPosY(), this.getPosZ() - 0.5 * offsetZ);

        Block current = world.getBlockState(pos).getBlock();
        System.out.println(current);
        if(current == BlockRegistrator.CablecarTrack.getBlock()) {
            this.dataManager.set(ON_RAIL, true);
        }
        else if(current == BlockRegistrator.CablecarDeployer.getBlock() && this.dataManager.get(ON_RAIL)) {
            CablecarDeployerTileEntity entity = (CablecarDeployerTileEntity) world.getTileEntity(pos);
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
