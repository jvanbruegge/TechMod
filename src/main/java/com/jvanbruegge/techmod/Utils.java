package com.jvanbruegge.techmod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.util.InputMappings;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Utils {
    public static Direction getDirection(BlockPos origin, BlockPos other) {
        if(origin.getX() < other.getX()) {
            return Direction.EAST;
        } else if (origin.getX() > other.getX()) {
            return Direction.WEST;
        } else if (origin.getZ() < other.getZ()) {
            return Direction.SOUTH;
        } else if (origin.getZ() > other.getZ()) {
            return Direction.NORTH;
        } else if (origin.getY() < other.getY()) {
            return Direction.UP;
        } else return Direction.DOWN;
    }

    public static Comparator<Direction> directionComparator = Comparator.comparingInt(Direction::getIndex);

    public static void restoreMouse() {
        InputMappings.setCursorPosAndMode(Minecraft.getInstance().getMainWindow().getHandle(), 212993, 0, 0);
    }

    public static void dropInventoryItems(World world, BlockPos pos, IItemHandler inventory) {
        for(int i = 0; i < inventory.getSlots(); i++) {
            InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), inventory.getStackInSlot(i));
        }
    }

    public static List<BlockPos> getNeighborBlocks(World world, BlockPos pos, Class<?> clazz) {
        return StreamSupport.stream(((Iterable<Direction>) () -> Direction.Plane.HORIZONTAL.iterator()).spliterator(), false)
                .map(dir -> pos.offset(dir))
                .filter(p -> clazz.isAssignableFrom(world.getBlockState(p).getBlock().getClass()))
                .collect(Collectors.toList());
    }

    public static <T extends Enum, U>  List<U> getEnumValues(Class<T> clazz, Function<T, U> getter) {
        T[] values = clazz.getEnumConstants();
        List<U> result = new ArrayList<>(values.length);
        for(int i = 0; i < values.length; i++) {
            result.add(getter.apply(values[i]));
        }
        return result;
    }
}
