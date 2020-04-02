package com.jvanbruegge.techmod.cablecar;

import com.jvanbruegge.techmod.TechModBlock;
import com.jvanbruegge.techmod.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemGroup;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CablecarTrackBlock extends TechModBlock implements CablecarConnectable {
    private static final EnumProperty<Direction> firstEnd = EnumProperty.create("first_end", Direction.class);
    private static final EnumProperty<Direction> secondEnd = EnumProperty.create("second_end", Direction.class);

    public CablecarTrackBlock() {
        super(Properties.create(Material.PISTON), "cablecar_track", ItemGroup.TRANSPORTATION);
        this.setDefaultState(
                this.getStateContainer().getBaseState()
                        .with(firstEnd, Direction.NORTH)
                        .with(secondEnd, Direction.SOUTH)
        );
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(new IProperty[]{firstEnd, secondEnd});
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        VoxelShape shape = VoxelShapes.create(6.25/16, 13.75/16, 6.25/16, 9.75/16, 1, 9.75/16);
        for(Direction end : new Direction[]{ state.get(firstEnd), state.get(secondEnd) }) {
            switch (end) {
                case NORTH: shape = VoxelShapes.or(shape, VoxelShapes.create(6.25/16, 13.75/16, 0, 9.75/16, 1, 6.5/16)); break;
                case SOUTH: shape = VoxelShapes.or(shape, VoxelShapes.create(6.25/16, 13.75/16, 9.75/16, 9.75/16, 1, 1)); break;
                case EAST: shape = VoxelShapes.or(shape, VoxelShapes.create(9.75/16, 13.75/16, 6.25/16, 1, 1, 9.75/16)); break;
                case WEST: shape = VoxelShapes.or(shape, VoxelShapes.create(0, 13.75/16, 6.25/16, 6.25/16, 1, 9.75/16)); break;
            }
        }
        return shape;
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        List<BlockPos> neighbors = TechModBlock.getNeighborBlocks(context.getWorld(), context.getPos(), CablecarConnectable.class);
        List<Direction> ourConnections = new ArrayList<>();

        for(BlockPos neighbor : neighbors) {
            if(ourConnections.size() == 2) {
                break;
            }

            Direction current = Utils.getDirection(context.getPos(), neighbor);
            BlockState state = context.getWorld().getBlockState(neighbor);
            CablecarConnectable other = (CablecarConnectable)state.getBlock();

            if(other.connectTo(context.getPos(), context.getWorld(), neighbor, state)) {
                ourConnections.add(current);
            }
        }

        BlockState state = this.createState(Direction.NORTH, Direction.SOUTH);

        if(ourConnections.size() == 1) {
            ourConnections.add(ourConnections.get(0).getOpposite());
        }
        if(ourConnections.size() == 2) {
            state = this.createState(ourConnections.get(0), ourConnections.get(1));
        }

        return state;
    }

    @Override
    public boolean hasConnection(Direction dir, BlockPos ownPos, BlockState ownState) {
        return dir.equals(ownState.get(firstEnd)) || dir.equals(ownState.get(secondEnd));
    }

    @Override
    public boolean connectTo(BlockPos pos, World world, BlockPos ownPos, BlockState ownState) {
        List<Direction> connections = Stream.of(ownState.get(firstEnd), ownState.get(secondEnd))
                .filter(dir -> {
                    BlockPos other = ownPos.offset(dir);
                    BlockState state = world.getBlockState(other);

                    return state.getBlock() instanceof CablecarConnectable
                            && ((CablecarConnectable)state.getBlock()).hasConnection(dir.getOpposite(), other, state);
                })
                .collect(Collectors.toList());

        if(connections.size() == 2) {
            return false;
        } else {
            Direction first = Utils.getDirection(ownPos, pos);
            Direction second = connections.size() == 1 ? connections.get(0) : first.getOpposite();

            world.setBlockState(ownPos, this.createState(first, second));
            return true;
        }
    }

    private BlockState createState(Direction first, Direction second) {
        if(Utils.directionComparator.compare(first, second) < 0) {
            return this.getDefaultState()
                    .with(firstEnd, first)
                    .with(secondEnd, second);
        } else {
            return this.getDefaultState()
                    .with(firstEnd, second)
                    .with(secondEnd, first);
        }
    }
}
