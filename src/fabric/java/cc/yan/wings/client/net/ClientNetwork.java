package cc.yan.wings.client.net;

import cc.yan.wings.WingsAttachments;
import cc.yan.wings.server.net.clientbound.MessageSyncFlight;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;

/**
 * Fabric 客户端网络接收注册。
 */
@SuppressWarnings("null")
public final class ClientNetwork {
    private ClientNetwork() {
    }

    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(MessageSyncFlight.TYPE, ClientNetwork::handleSyncFlight);
    }

    private static void handleSyncFlight(MessageSyncFlight message, ClientPlayNetworking.Context context) {
        // 网络线程回调：通过 client executor 切到主线程安全更新世界/实体数据。
        context.client().execute(() -> ClientFlightSyncApplier.apply(message.playerUuid(), message.flight(),
                Minecraft.getInstance().level, WingsAttachments::hasFlight, WingsAttachments::getFlight));
    }
}
