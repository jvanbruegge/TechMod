package com.jvanbruegge.techmod.network;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class MessageHandlerOnServer {
    public static boolean isProtocolAcceptedByServer(String version) {
        return version.equals(PacketHandler.PROTOCOL_VERSION);
    }

    public static <MSG> BiConsumer<MSG, Supplier<NetworkEvent.Context>> create(BiConsumer<MSG, ServerPlayerEntity> handler) {
        return (msg, ctxSupplier) -> {
            NetworkEvent.Context ctx = ctxSupplier.get();
            LogicalSide sideReceived = ctx.getDirection().getReceptionSide();
            ServerPlayerEntity player = ctx.getSender();

            if(sideReceived == LogicalSide.SERVER) {
                ctx.enqueueWork(() -> handler.accept(msg, player));
                ctx.setPacketHandled(true);
            } else {
                System.err.println("Message received on wrong side: " + msg);
            }
        };
    }
}
