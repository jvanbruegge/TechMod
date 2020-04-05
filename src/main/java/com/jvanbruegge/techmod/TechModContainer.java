package com.jvanbruegge.techmod;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;

import javax.annotation.Nullable;

public abstract class TechModContainer extends Container {
    protected TechModContainer(@Nullable ContainerType<?> type, int windowId) {
        super(type, windowId);
    }

    protected void addPlayerInventory(PlayerInventory inventory, int left, int top) {
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 9; j++) {
                this.addSlot(new Slot(inventory, j + i * 9 + 9, left + j * 18, top + i * 18));
            }
        }

        for(int i = 0; i < 9; i++) {
            this.addSlot(new Slot(inventory, i, left + i * 18, top + 58));
        }
    }
}
