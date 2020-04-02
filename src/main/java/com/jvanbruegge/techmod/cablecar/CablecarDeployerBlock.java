package com.jvanbruegge.techmod.cablecar;

import com.jvanbruegge.techmod.TechModBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemGroup;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;

import java.util.Arrays;
import java.util.stream.Collectors;

public class CablecarDeployerBlock extends TechModBlock {
    private static final EnumProperty<Direction> facing = EnumProperty.create("facing", Direction.class);

    public CablecarDeployerBlock() {
        super(Properties.create(Material.PISTON), "cablecar_deployer", ItemGroup.TRANSPORTATION);
        this.setDefaultState(
                this.getStateContainer().getBaseState().with(facing, Direction.NORTH)
        );
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        Direction dir = Arrays.asList(context.getNearestLookingDirections())
                .stream()
                .filter(x -> x != Direction.UP && x != Direction.DOWN)
                .collect(Collectors.toList())
                .get(0);
        if(context.getPlayer() != null && context.getPlayer().isCrouching()) {
            dir = dir.getOpposite();
        }
        return this.getDefaultState().with(facing, dir);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(new IProperty[]{ facing });
    }
}
