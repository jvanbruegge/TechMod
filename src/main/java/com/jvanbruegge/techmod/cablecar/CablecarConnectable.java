package com.jvanbruegge.techmod.cablecar;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface CablecarConnectable {
    boolean hasConnection(Direction dir, BlockPos ownPos, BlockState ownState);
    boolean connectTo(BlockPos pos, World world, BlockPos ownPos, BlockState ownState);
}
