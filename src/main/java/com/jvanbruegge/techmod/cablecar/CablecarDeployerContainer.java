package com.jvanbruegge.techmod.cablecar;

import com.jvanbruegge.techmod.Registrator;
import com.jvanbruegge.techmod.TechModContainer;
import com.jvanbruegge.techmod.Utils;
import com.jvanbruegge.techmod.network.PacketHandler;
import com.jvanbruegge.techmod.network.cablecar.CloseInventoryMessage;
import com.jvanbruegge.techmod.network.cablecar.UpdateMultiplierMessage;
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

public class CablecarDeployerContainer extends TechModContainer {

    private final World world;
    private final PlayerEntity player;
    private final CablecarDeployerTileEntity entity;

    @Setter
    private CableCarDeployerScreen screen = null;

    @Getter
    private boolean enabled = false;
    @Getter
    private int multiplier = 1;

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
        this.multiplier = entity.getMultiplier();

        this.addPlayerInventory(inventory, 8, 84);
        this.entity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                .ifPresent(inv -> this.addSlot(new SlotItemHandler(inv, 0, 18, 31)));
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

    public void setEnabled(boolean enabled) {
        if(enabled != this.enabled) {
            this.enabled = enabled;
            if(this.screen != null) {
                this.screen.setTextEnabled(enabled);
            }
        }
    }
    public void setMultiplier(int multiplier, boolean fromScreen) {
        if(this.multiplier != multiplier) {
            this.multiplier = multiplier;
            if(world.isRemote && fromScreen) {
                PacketHandler.sendToServer(new UpdateMultiplierMessage.Server(entity.getPos(), multiplier));
            }
            if(!fromScreen && this.screen != null) {
                screen.setMuliplier(multiplier);
            }
        }
    }

    public static boolean shouldBeEnabled(ServerWorld world, BlockPos pos) {
        return Utils.getPlayersWithOpenContainer(world, pos, CablecarDeployerContainer.class, null).size() == 0;
    }
}
