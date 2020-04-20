package com.jvanbruegge.techmod.network.cablecar;

import com.jvanbruegge.techmod.Utils;
import com.jvanbruegge.techmod.cablecar.CablecarDeployerContainer;
import com.jvanbruegge.techmod.network.PacketHandler;
import lombok.Data;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public @Data
class CloseInventoryMessage {
    private final BlockPos pos;

    public void encode(PacketBuffer buf) {
        buf.writeBlockPos(pos);
    }

    public static CloseInventoryMessage decode(PacketBuffer buf) {
        return new CloseInventoryMessage(buf.readBlockPos());
    }

    public static void onMessage(CloseInventoryMessage msg, ServerPlayerEntity player) {
        if(!PacketHandler.isValidMessage(player.world, msg.getPos())) return;
        List<PlayerEntity> active = Utils.getPlayersWithOpenContainer(player.world, msg.getPos(), CablecarDeployerContainer.class, player);

        if(active.size() > 0) {
            boolean hasEnabled = active.stream().anyMatch(p ->
                    p.openContainer instanceof CablecarDeployerContainer
                            && ((CablecarDeployerContainer) p.openContainer).isEnabled()
            );

            if(!hasEnabled) {
                ServerPlayerEntity receiver = (ServerPlayerEntity) active.get(0);
                ((CablecarDeployerContainer)receiver.openContainer).setEnabled(true);
                PacketHandler.sendToPlayer(new EnableTextboxMessage(msg.getPos()), receiver);
            }
        }
    }
}
