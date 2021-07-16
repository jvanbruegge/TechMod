package com.jvanbruegge.techmod.network.cablecar;

import com.jvanbruegge.techmod.Utils;
import com.jvanbruegge.techmod.cablecar.CablecarDeployerContainer;
import com.jvanbruegge.techmod.network.PacketHandler;
import lombok.Data;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public abstract @Data class ActivateSliderMessage {
    private final BlockPos pos;
    private final boolean active;

    public void encode(PacketBuffer buf) {
        buf.writeBlockPos(pos);
        buf.writeBoolean(active);
    }


    public static class Client extends ActivateSliderMessage {
        public Client(BlockPos pos, boolean active) {
            super(pos, active);
        }

        public static Client decode(PacketBuffer buf) {
            return new Client(buf.readBlockPos(), buf.readBoolean());
        }

        public static void onMessage(ActivateSliderMessage msg, ClientPlayerEntity player) {
            if(player.openContainer instanceof CablecarDeployerContainer) {
                ((CablecarDeployerContainer)player.openContainer).setEnabled(!msg.isActive());
            }
        }
    }

    public static class Server extends ActivateSliderMessage {
        public Server(BlockPos pos, boolean active) {
            super(pos, active);
        }

        public static Server decode(PacketBuffer buf) {
            return new Server(buf.readBlockPos(), buf.readBoolean());
        }

        public static void onMessage(ActivateSliderMessage msg, ServerPlayerEntity player) {
            if(!PacketHandler.isValidMessage(player.world, msg.getPos())) return;

            List<PlayerEntity> active = Utils.getPlayersWithOpenContainer(player.world, msg.getPos(), CablecarDeployerContainer.class, null);
            if(!active.stream().anyMatch(p -> ((CablecarDeployerContainer)p.openContainer).isActive())) {
                for(PlayerEntity p : active) {
                    if(p != player) {
                        ((CablecarDeployerContainer)p.openContainer).setEnabled(false);
                        PacketHandler.sendToPlayer(new ActivateSliderMessage.Client(msg.getPos(), msg.isActive()), (ServerPlayerEntity)p);
                    }
                }
            }
        }
    }
}
