package com.jvanbruegge.techmod.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class MessageHandlerOnClient {
    public static boolean isProtocolVersionAcceptedByClient(String version) {
        return version.equals(PacketHandler.PROTOCOL_VERSION);
    }

    public static <MSG> BiConsumer<MSG, Supplier<NetworkEvent.Context>> create(BiConsumer<MSG, ClientPlayerEntity> handler) {
        return (msg, ctxSupplier) -> {
            NetworkEvent.Context ctx = ctxSupplier.get();
            LogicalSide sideReceived = ctx.getDirection().getReceptionSide();
            if(sideReceived == LogicalSide.CLIENT) {
                ctx.enqueueWork(() -> handler.accept(msg, Minecraft.getInstance().player));
                ctx.setPacketHandled(true);
            } else {
                System.err.println("Client packet received on the server: " + msg);
            }
        };
    }
}
