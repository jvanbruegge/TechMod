package com.jvanbruegge.techmod.network;

import com.jvanbruegge.techmod.TechMod;
import com.jvanbruegge.techmod.network.cablecar.CloseInventoryMessage;
import com.jvanbruegge.techmod.network.cablecar.EnableTextboxMessage;
import com.jvanbruegge.techmod.network.cablecar.UpdateMultiplierMessage;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = TechMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class PacketHandler {
    private static SimpleChannel channel;

    public static final String PROTOCOL_VERSION = "1.0";

    @SubscribeEvent
    public static void setup(FMLCommonSetupEvent event) {
        channel = NetworkRegistry.newSimpleChannel(
                new ResourceLocation(TechMod.MODID, "channel"),
                () -> PROTOCOL_VERSION,
                MessageHandlerOnClient::isProtocolVersionAcceptedByClient,
                MessageHandlerOnServer::isProtocolAcceptedByServer
        );

        int id = 1;
        channel.registerMessage(id++, CloseInventoryMessage.class,
                CloseInventoryMessage::encode, CloseInventoryMessage::decode,
                MessageHandlerOnServer.create(CloseInventoryMessage::onMessage)
        );
        channel.registerMessage(id++, EnableTextboxMessage.class,
                EnableTextboxMessage::encode, EnableTextboxMessage::decode,
                MessageHandlerOnClient.create(EnableTextboxMessage::onMessage)
        );
        channel.registerMessage(id++, UpdateMultiplierMessage.Client.class,
                UpdateMultiplierMessage.Client::encode, UpdateMultiplierMessage.Client::decode,
                MessageHandlerOnClient.create(UpdateMultiplierMessage.Client::onMessage)
        );
        channel.registerMessage(id++, UpdateMultiplierMessage.Server.class,
                UpdateMultiplierMessage.Server::encode, UpdateMultiplierMessage.Server::decode,
                MessageHandlerOnServer.create(UpdateMultiplierMessage.Server::onMessage)
        );
    }

    public static <MSG> void sendToServer(MSG msg) {
        channel.send(PacketDistributor.SERVER.noArg(), msg);
    }

    public static <MSG> void sendToPlayer(MSG msg, ServerPlayerEntity player) {
        channel.send(PacketDistributor.PLAYER.with(() -> player), msg);
    }

    public static <MSG> void sendToChunk(MSG msg, Chunk chunk, ServerPlayerEntity sender) {
        channel.send(trackingChunk(sender, chunk).noArg(), msg);
    }

    public static boolean isValidMessage(World world, BlockPos pos) {
        return world.isAreaLoaded(pos, 1);
    }

    private static PacketDistributor<Void> trackingChunk(ServerPlayerEntity sender, Chunk chunk) {
        BiFunction<PacketDistributor<Void>, Supplier<Void>, Consumer<IPacket<?>>> functor = new BiFunction<PacketDistributor<Void>, Supplier<Void>, Consumer<IPacket<?>>>() {
            @Override
            public Consumer<IPacket<?>> apply(PacketDistributor<Void> voidPacketDistributor, Supplier<Void> voidSupplier) {
                return p ->
                ((ServerChunkProvider) chunk.getWorld().getChunkProvider()).chunkManager.getTrackingPlayers(chunk.getPos(), false)
                        .forEach(e -> {
                            if(e != sender) {
                                e.connection.sendPacket(p);
                            }
                        });
            }
        };

        return new PacketDistributor<>(functor, NetworkDirection.PLAY_TO_CLIENT);
    }
}
