package com.jvanbruegge.techmod.network.cablecar;

import com.jvanbruegge.techmod.cablecar.CableCarDeployerScreen;
import com.jvanbruegge.techmod.cablecar.CablecarDeployerContainer;
import com.jvanbruegge.techmod.cablecar.CablecarDeployerTileEntity;
import com.jvanbruegge.techmod.network.PacketHandler;
import lombok.Data;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public abstract @Data class UpdateDataMessage {
    private final BlockPos pos;
    private final int multiplier;
    private final boolean binary;

    public void encode(PacketBuffer buf) {
        buf.writeBlockPos(pos);
        buf.writeInt(multiplier);
        buf.writeBoolean(binary);
    }

    public static class Client extends UpdateDataMessage {
        public Client(UpdateDataMessage msg) {
            this(msg.getPos(), msg.getMultiplier(), msg.isBinary());
        }
        public Client(BlockPos pos, int multiplier, boolean binary) {
            super(pos, multiplier, binary);
        }

        public static Client decode(PacketBuffer buf) {
            return new Client(buf.readBlockPos(), buf.readInt(), buf.readBoolean());
        }

        public static void onMessage(Client msg, ClientPlayerEntity player) {
            if(player.openContainer instanceof CablecarDeployerContainer) {
                CablecarDeployerContainer container = (CablecarDeployerContainer) player.openContainer;
                container.setMultiplier(msg.getMultiplier(), false);
                container.setBinary(msg.isBinary(), false);
            }
        }
    }

    public static class Server extends UpdateDataMessage {
        public Server(BlockPos pos, int multiplier, boolean binary) {
            super(pos, multiplier, binary);
        }

        public static Server decode(PacketBuffer buf) {
            return new Server(buf.readBlockPos(), buf.readInt(), buf.readBoolean());
        }

        public static void onMessage(Server msg, ServerPlayerEntity sender) {
            if(!PacketHandler.isValidMessage(sender.world, msg.getPos())) return;

            if(sender.openContainer instanceof CablecarDeployerContainer) {
                CablecarDeployerContainer container = (CablecarDeployerContainer) sender.openContainer;
                if(container.isEnabled()) {
                    TileEntity entity = sender.world.getTileEntity(msg.getPos());
                    if(entity instanceof CablecarDeployerTileEntity) {
                        CablecarDeployerTileEntity tileEntity = (CablecarDeployerTileEntity) entity;

                        tileEntity.setMultiplier(msg.getMultiplier());
                        tileEntity.setBinary(msg.isBinary());

                        PacketHandler.sendToChunk(
                                new UpdateDataMessage.Client(msg),
                                sender.world.getChunkAt(entity.getPos()),
                                sender
                        );
                    }
                }
            }
        }
    }
}
