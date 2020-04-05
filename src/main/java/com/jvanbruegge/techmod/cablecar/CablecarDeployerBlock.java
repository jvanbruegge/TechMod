package com.jvanbruegge.techmod.cablecar;

import com.jvanbruegge.techmod.Registrator;
import com.jvanbruegge.techmod.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.Arrays;
import java.util.stream.Collectors;

public class CablecarDeployerBlock extends Block implements CablecarConnectable {
    private static final EnumProperty<Direction> facing = EnumProperty.create("facing", Direction.class);

    public CablecarDeployerBlock() {
        super(Properties.create(Material.PISTON));
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
                .get(0)
                .getOpposite();
        if(context.getPlayer() != null && context.getPlayer().isCrouching()) {
            dir = dir.getOpposite();
        }
        return this.getDefaultState().with(facing, dir);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return Registrator.CablecarDeployer.getTileEntityType().create();
    }

    @Override
    public boolean hasComparatorInputOverride(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(BlockState state, World world, BlockPos pos) {
        TileEntity entity = world.getTileEntity(pos);
        return entity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                .map(ItemHandlerHelper::calcRedstoneFromInventory)
                .orElse(0);
    }

    @Override
    public void onReplaced(BlockState state, World world, BlockPos pos, BlockState other, boolean p_196243_5_) {
        if(state.getBlock() != other.getBlock()) {
            TileEntity entity = world.getTileEntity(pos);
            entity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(inventory -> {
                Utils.dropInventoryItems(world, pos, inventory);
                world.updateComparatorOutputLevel(pos, this);
            });
        }
        super.onReplaced(state, world, pos, other, p_196243_5_);
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity entity, Hand hand, BlockRayTraceResult trace) {
        if(!world.isRemote) {
            TileEntity tileEntity = world.getTileEntity(pos);
            NetworkHooks.openGui((ServerPlayerEntity) entity, (INamedContainerProvider) tileEntity, pos);
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(new IProperty[]{ facing });
    }

    @Override
    public boolean hasConnection(Direction dir, BlockPos ownPos, BlockState ownState) {
        return dir.equals(ownState.get(facing));
    }

    @Override
    public boolean connectTo(BlockPos pos, World world, BlockPos ownPosition, BlockState state) {
        return ownPosition.offset(state.get(facing)).equals(pos);
    }
}
