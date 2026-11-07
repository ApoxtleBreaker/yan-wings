package cc.yan.wings.server.net.clientbound;

import cc.yan.wings.WingsMod;
import cc.yan.wings.server.flight.Flight;
import cc.yan.wings.server.flight.FlightDefault;
import cc.yan.wings.server.net.Message;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

/**
 * 服务端 -> 客户端：同步指定玩家的飞行数据。
 * <p>
 * 使用 UUID 而非实体 id 定位玩家：玩家重生时服务端会创建新实体（新的 entity id），
 * 而客户端会复用同一个 LocalPlayer（entity id 不变），按 id 查找会导致同步包丢失。
 */
@SuppressWarnings("null")
public record MessageSyncFlight(UUID playerUuid, Flight flight) implements Message {
    public static final CustomPacketPayload.Type<MessageSyncFlight> TYPE = new CustomPacketPayload.Type<>(
            WingsMod.locate("sync_flight"));
    public static final StreamCodec<FriendlyByteBuf, MessageSyncFlight> STREAM_CODEC = StreamCodec
            .of((buf, message) -> {
                buf.writeUUID(message.playerUuid());
                message.flight().serialize(buf);
            }, buf -> {
                UUID playerUuid = buf.readUUID();
                FlightDefault flight = new FlightDefault();
                flight.deserialize(buf);
                return new MessageSyncFlight(playerUuid, flight);
            });

    public MessageSyncFlight(Player player, Flight flight) {
        this(player.getUUID(), flight);
    }

    @Override
    public CustomPacketPayload.Type<MessageSyncFlight> type() {
        return TYPE;
    }
}
