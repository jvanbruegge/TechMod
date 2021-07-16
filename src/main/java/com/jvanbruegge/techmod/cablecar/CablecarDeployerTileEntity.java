package com.jvanbruegge.techmod.cablecar;

import com.jvanbruegge.techmod.BlockRegistrator;
import com.jvanbruegge.techmod.ItemRegistrator;
import lombok.Getter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
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

    @Getter
    private int multiplier = 64;
    @Getter
    private boolean binary = true;
    @Getter
    private boolean keepCarts = true;

    private LazyOptional<ItemStackHandler> inventory = LazyOptional.of(() -> new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            markDirty();
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return stack.getItem() == ItemRegistrator.Cablecar.getItem();
        }
    });

    public CablecarDeployerTileEntity() {
        super(BlockRegistrator.CablecarDeployer.getTileEntityType());
    }

    public void setMultiplier(int multiplier) {
        this.multiplier = multiplier;
        this.markDirty();
    }
    public void setBinary(boolean binary) {
        this.binary = binary;
        this.markDirty();
    }
    public void setKeepCarts(boolean keepCarts) {
        this.keepCarts = keepCarts;
        this.markDirty();
    }

    public ItemStack extractItem(int amount, boolean simulate) {
        return this.inventory
                .map(handler -> handler.extractItem(0, amount, simulate))
                .orElse(ItemStack.EMPTY);
    }

    public boolean putCart() {
        return !this.inventory
                .map(handler -> !handler.insertItem(
                        0,
                        ItemRegistrator.Cablecar.getItemStack(1),
                        false
                ).isEmpty())
                .orElse(false);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound);
        compound.putInt("multiplier", multiplier);
        compound.putBoolean("binary", binary);
        compound.putBoolean("keepCarts", keepCarts);
        inventory.ifPresent(inv -> compound.put("inventory", inv.serializeNBT()));
        return compound;
    }

    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);
        this.multiplier = compound.getInt("multiplier");
        this.binary = compound.getBoolean("binary");
        this.keepCarts = compound.getBoolean("keepCarts");
        inventory.ifPresent(inv -> inv.deserializeNBT(compound.getCompound("inventory")));
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(
                this.pos,
                BlockRegistrator.CablecarDeployer.getTileEntityType().hashCode(),
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
