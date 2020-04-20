package com.jvanbruegge.techmod.network.cablecar;

import com.jvanbruegge.techmod.cablecar.CablecarDeployerContainer;
import lombok.Data;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;

public @Data class EnableTextboxMessage {
    private final BlockPos pos;

    public void encode(PacketBuffer buf) {
        buf.writeBlockPos(pos);
    }

    public static EnableTextboxMessage decode(PacketBuffer buf) {
        return new EnableTextboxMessage(buf.readBlockPos());
    }

    public static void onMessage(EnableTextboxMessage msg, ClientPlayerEntity player) {
        if(player.openContainer != null && player.openContainer instanceof CablecarDeployerContainer) {
            ((CablecarDeployerContainer)player.openContainer).setEnabled(true);
        }
    }
}
