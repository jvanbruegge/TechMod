package com.jvanbruegge.techmod.cablecar;

import com.jvanbruegge.techmod.Registrator;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CablecarDeployerTileEntity extends TileEntity implements INamedContainerProvider {

    private int multiplier = 1;
    private LazyOptional<ItemStackHandler> inventory = LazyOptional.of(() -> new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            markDirty();
        }
    });

    public CablecarDeployerTileEntity() {
        super(Registrator.CablecarDeployer.getTileEntityType());
    }

    public int getMultiplier() {
        return multiplier;
    }
    public void setMultiplier(int multiplier) {
        this.multiplier = multiplier;
        this.markDirty();
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound);
        compound.putInt("multiplier", multiplier);
        inventory.ifPresent(inv -> compound.put("inventory", inv.serializeNBT()));
        return compound;
    }

    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);
        this.multiplier = compound.getInt("multiplier");
        inventory.ifPresent(inv -> inv.deserializeNBT(compound.getCompound("inventory")));
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(
                this.pos,
                Registrator.CablecarDeployer.getTileEntityType().hashCode(),
                this.write(new CompoundNBT())
        );
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        this.read(pkt.getNbtCompound());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return this.write(new CompoundNBT());
    }

    @Override
    public void handleUpdateTag(CompoundNBT tag) {
        this.read(tag);
    }

    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction side) {
        if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return inventory.cast();
        }
        return super.getCapability(capability, side);
    }

    @Override
    public void remove() {
        super.remove();
        inventory.invalidate();
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("block.techmod.cablecar_deployer");
    }

    @Nullable
    @Override
    public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity entity) {
        boolean enabled = CablecarDeployerContainer.shouldBeEnabled((ServerWorld)world, pos);
        return new CablecarDeployerContainer(windowId, entity.world, inventory, this.getPos(), enabled);
    }
}
