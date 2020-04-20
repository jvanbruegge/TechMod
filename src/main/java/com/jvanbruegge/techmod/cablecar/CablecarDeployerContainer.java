package com.jvanbruegge.techmod.cablecar;

import com.jvanbruegge.techmod.Registrator;
import com.jvanbruegge.techmod.TechModContainer;
import com.jvanbruegge.techmod.Utils;
import com.jvanbruegge.techmod.network.PacketHandler;
import com.jvanbruegge.techmod.network.cablecar.ActivateSliderMessage;
import com.jvanbruegge.techmod.network.cablecar.CloseInventoryMessage;
import com.jvanbruegge.techmod.network.cablecar.UpdateDataMessage;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import java.util.List;

public class CablecarDeployerContainer extends TechModContainer {

    private final World world;
    private final PlayerEntity player;
    private final CablecarDeployerTileEntity entity;

    @Setter
    private CableCarDeployerScreen screen = null;

    @Getter
    private boolean enabled = true;
    @Getter
    @Setter
    private boolean active = false;

    // Client-side only
    public CablecarDeployerContainer(int windowId, PlayerInventory inventory, PacketBuffer extraData) {
        this(windowId, Minecraft.getInstance().world, inventory, extraData.readBlockPos(), extraData.readBoolean());
    }
    // Server-side
    public CablecarDeployerContainer(int windowId, World world, PlayerInventory inventory, BlockPos pos, boolean enabled) {
        super(Registrator.CablecarDeployer.getContainerType(), windowId);
        this.world = world;
        this.player = inventory.player;
        this.entity = (CablecarDeployerTileEntity) world.getTileEntity(pos);

        this.enabled = enabled;

        this.addPlayerInventory(inventory, 8, 94);
        this.entity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                .ifPresent(inv -> this.addSlot(new SlotItemHandler(inv, 0, 16, 31)));
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return isWithinUsableDistance(IWorldPosCallable.of(world, entity.getPos()), player, Registrator.CablecarDeployer.getBlock());
    }

    @Override
    public void onContainerClosed(PlayerEntity player) {
        super.onContainerClosed(player);
        PacketHandler.sendToServer(new CloseInventoryMessage(entity.getPos()));
    }

    public void setActive(boolean active) {
        this.active = active;
        if(world.isRemote) {
            PacketHandler.sendToServer(new ActivateSliderMessage.Server(entity.getPos(), active));
        }
    }

    public void setEnabled(boolean enabled) {
        if(enabled != this.enabled) {
            this.enabled = enabled;
            if(this.screen != null) {
                this.screen.setSliderEnabled(enabled);
            }
        }
    }
    public int getMultiplier() {
        return this.entity.getMultiplier();
    }
    public void setMultiplier(int multiplier, boolean fromScreen) {
        if(this.getMultiplier() != multiplier) {
            this.entity.setMultiplier(multiplier);
            if(world.isRemote && fromScreen) {
                PacketHandler.sendToServer(new UpdateDataMessage.Server(entity.getPos(), multiplier, isBinary(), isKeepCarts()));
            }
            if(!fromScreen && this.screen != null) {
                screen.setMuliplier(multiplier);
            }
        }
    }
    public boolean isBinary() {
        return this.entity.isBinary();
    }
    public void setBinary(boolean binary, boolean fromScreen) {
        if(this.isBinary() != binary) {
            this.entity.setBinary(binary);
            if(world.isRemote && fromScreen) {
                PacketHandler.sendToServer(new UpdateDataMessage.Server(entity.getPos(), getMultiplier(), binary, isKeepCarts()));
            }
            if(!fromScreen && this.screen != null) {
                screen.updateMode();
            }
        }
    }
    public boolean isKeepCarts() {
        return this.entity.isKeepCarts();
    }
    public void setKeepCarts(boolean keepCarts, boolean fromScreen) {
        if(this.isKeepCarts() != keepCarts) {
            this.entity.setKeepCarts(keepCarts);
            if(world.isRemote && fromScreen) {
                PacketHandler.sendToServer(new UpdateDataMessage.Server(entity.getPos(), getMultiplier(), isBinary(), keepCarts));
            }
            if(!fromScreen && this.screen != null) {
                screen.updateKeepCarts();
            }
        }
    }

    public static boolean shouldBeEnabled(ServerWorld world, BlockPos pos) {
        List<PlayerEntity> active = Utils.getPlayersWithOpenContainer(world, pos, CablecarDeployerContainer.class, null);
        return !active.stream().anyMatch(player -> ((CablecarDeployerContainer)player.openContainer).isActive());
    }
}
