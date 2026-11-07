package cc.yan.wings.client.net;

import cc.yan.wings.server.flight.Flight;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;

public final class ClientFlightSyncApplier {
    private static final Logger LOGGER = LogManager.getLogger("WingsNetwork");

    private ClientFlightSyncApplier() {
    }

    public static void apply(UUID playerUuid, Flight snapshot, Level level, Predicate<Player> hasFlight,
                             Function<Player, Flight> getFlight) {
        if (level == null) {
            LOGGER.warn("Received sync_flight but level is null");
            return;
        }
        Player player = null;
        // 本地玩家优先：重生时新实例沿用旧 UUID，避免同步写进已被替换的旧实例。
        Player local = net.minecraft.client.Minecraft.getInstance().player;
        if (local != null && local.getUUID().equals(playerUuid)) {
            player = local;
        }
        if (player == null) {
            for (Player candidate : level.players()) {
                if (candidate.getUUID().equals(playerUuid)) {
                    player = candidate;
                    break;
                }
            }
        }
        if (player == null) {
            LOGGER.warn("Received sync_flight for unknown player uuid={}", playerUuid);
            return;
        }
        boolean hadFlight = hasFlight.test(player);
        Flight flight = getFlight.apply(player);
        if (!hadFlight) {
            LOGGER.debug("Creating new flight attachment for player {}", player.getName().getString());
        }
        flight.clone(snapshot);
        LOGGER.debug("Synced flight data for player {} (flying={}, wing={})",
                player.getName().getString(), flight.isFlying(), flight.getWing());
    }
}
