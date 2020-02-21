package com.jvanbruegge.techmod;

import com.mojang.datafixers.util.Pair;
import net.java.games.input.Mouse;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHelper;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

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
}
