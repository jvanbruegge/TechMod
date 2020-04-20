package com.jvanbruegge.techmod.network.cablecar;

import com.jvanbruegge.techmod.cablecar.CablecarDeployerContainer;
import com.jvanbruegge.techmod.cablecar.CablecarDeployerTileEntity;
import com.jvanbruegge.techmod.network.PacketHandler;
import lombok.Data;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public abstract @Data class UpdateMultiplierMessage {
    private final BlockPos pos;
    private final int multiplier;
    private final boolean fromSelf;

    public UpdateMultiplierMessage(BlockPos pos, int multiplier) {
        this(pos, multiplier, false);
    }
    public UpdateMultiplierMessage(BlockPos pos, int multiplier, boolean fromSelf) {
        this.pos = pos;
        this.multiplier = multiplier;
        this.fromSelf = fromSelf;
    }

    public void encode(PacketBuffer buf) {
        buf.writeBlockPos(pos);
        buf.writeInt(multiplier);
    }

    public static class Client extends UpdateMultiplierMessage {
        public Client(UpdateMultiplierMessage msg) {
            this(msg.getPos(), msg.getMultiplier());
        }
        public Client(BlockPos pos, int multiplier) {
            super(pos, multiplier, false);
        }
        public Client(BlockPos pos, int multiplier, boolean fromSelf) {
            super(pos, multiplier, fromSelf);
        }

        public static Client decode(PacketBuffer buf) {
            return new Client(buf.readBlockPos(), buf.readInt());
        }

        public static void onMessage(Client msg, ClientPlayerEntity player) {
            if(!msg.isFromSelf() && player.openContainer instanceof CablecarDeployerContainer) {
                ((CablecarDeployerContainer)player.openContainer).setMultiplier(msg.getMultiplier(), false);
            }
            TileEntity entity = player.world.getTileEntity(msg.getPos());
            if(entity instanceof CablecarDeployerTileEntity) {
                ((CablecarDeployerTileEntity)entity).setMultiplier(msg.getMultiplier());
                entity.markDirty();
            }
        }
    }

    public static class Server extends UpdateMultiplierMessage {
        public Server(BlockPos pos, int multiplier) {
            super(pos, multiplier);
        }

        public static Server decode(PacketBuffer buf) {
            return new Server(buf.readBlockPos(), buf.readInt());
        }

        public static void onMessage(Server msg, ServerPlayerEntity sender) {
            if(!PacketHandler.isValidMessage(sender.world, msg.getPos())) return;

            if(sender.openContainer instanceof CablecarDeployerContainer) {
                CablecarDeployerContainer container = (CablecarDeployerContainer) sender.openContainer;
                if(container.isEnabled()) {
                    TileEntity entity = sender.world.getTileEntity(msg.getPos());
                    if(entity instanceof CablecarDeployerTileEntity) {
                        ((CablecarDeployerTileEntity)entity).setMultiplier(msg.getMultiplier());
                        PacketHandler.sendToChunk(
                                new UpdateMultiplierMessage.Client(msg),
                                sender.world.getChunkAt(entity.getPos()),
                                sender
                        );
                        PacketHandler.sendToPlayer(
                                new UpdateMultiplierMessage.Client(msg.getPos(), msg.getMultiplier(), true),
                                sender
                        );
                    }
                }
            }
        }
    }
}
