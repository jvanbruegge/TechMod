package com.jvanbruegge.techmod.cablecar;

import com.jvanbruegge.techmod.Registrator;
import com.jvanbruegge.techmod.TechModContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CablecarDeployerContainer extends TechModContainer {

    private final World world;
    private final PlayerEntity player;
    private final TileEntity entity;

    // Client-side only
    public CablecarDeployerContainer(int windowId, PlayerInventory inventory, PacketBuffer extraData) {
        this(windowId, Minecraft.getInstance().world, inventory, extraData.readBlockPos());
    }
    // Server-side
    public CablecarDeployerContainer(int windowId, World world, PlayerInventory inventory, BlockPos pos) {
        super(Registrator.CablecarDeployer.getContainerType(), windowId);
        this.world = world;
        this.player = inventory.player;
        this.entity = world.getTileEntity(pos);

        this.addPlayerInventory(inventory, 8, 84);
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return isWithinUsableDistance(IWorldPosCallable.of(world, entity.getPos()), player, Registrator.CablecarDeployer.getBlock());
    }
}
