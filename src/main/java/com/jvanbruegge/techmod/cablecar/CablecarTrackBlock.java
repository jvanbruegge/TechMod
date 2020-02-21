package com.jvanbruegge.techmod.cablecar;

import com.jvanbruegge.techmod.TechModBlock;
import com.jvanbruegge.techmod.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
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

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CablecarTrackBlock extends TechModBlock {
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
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        List<BlockPos> neighbors = TechModBlock.getNeighborBlocks(worldIn, pos, CablecarTrackBlock.class);
        List<Direction> ourConnections = new ArrayList<>();

        for(BlockPos neighbor : neighbors) {
            if(ourConnections.size() == 2) {
                break;
            }

            List<BlockPos> connections = CablecarTrackBlock.getConnections(worldIn, neighbor);

            if(connections.size() == 2) { // Block is already fully connected, no changes to this neighbor
                continue;
            } else if(connections.size() == 1 && !connections.get(0).equals(pos)) { // Only one end is connected, connect to the second one
                Direction existingConnection = Utils.getDirection(neighbor, connections.get(0));
                Direction newConnection = Utils.getDirection(neighbor, pos);
                worldIn.setBlockState(neighbor, this.createState(existingConnection, newConnection));
                ourConnections.add(newConnection.getOpposite());
            } else { // No connections, connect and make neighbor a straight track
                Direction conDir = Utils.getDirection(pos, neighbor);
                BlockState neighborState = conDir == Direction.NORTH || conDir == Direction.SOUTH
                        ? this.createState(Direction.NORTH, Direction.SOUTH)
                        : this.createState(Direction.EAST, Direction.WEST);
                worldIn.setBlockState(neighbor, neighborState);

                ourConnections.add(conDir);
            }
        }

        BlockState newState = this.createState(Direction.NORTH, Direction.SOUTH);

        if(ourConnections.size() == 1) {
            ourConnections.add(ourConnections.get(0).getOpposite());
        }
        if(ourConnections.size() == 2) {
            newState = this.createState(ourConnections.get(0), ourConnections.get(1));
        }

        worldIn.setBlockState(pos, newState);
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

    /**
     * Returns those neighbors where `pos.(firstEnd || secondEnd) == invert(neighbor.(firstEnd || secondEnd))`
     */
    public static List<BlockPos> getConnections(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return Stream.of(state.get(firstEnd), state.get(secondEnd))
                .filter(dir -> world.getBlockState(pos.offset(dir)).getBlock() instanceof CablecarTrackBlock)
                .filter(dir -> {
                    BlockState s = world.getBlockState(pos.offset(dir));
                    return s.get(firstEnd) == dir.getOpposite() || s.get(secondEnd) == dir.getOpposite();
                })
                .map(dir -> pos.offset(dir))
                .collect(Collectors.toList());
    }
}
