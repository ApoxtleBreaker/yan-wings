package cc.yan.wings.client;

import cc.yan.wings.server.apparatus.FlightApparatus;
import cc.yan.wings.server.flight.Flight;
import cc.yan.wings.server.flight.Flights;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

public final class ClientFlightInputActions {
    private ClientFlightInputActions() {
    }

    public static void toggleLocalFlight() {
        Player player = Minecraft.getInstance().player;
        if (player == null || player.isSpectator()) {
            return;
        }
        Flight flight = Flights.get(player);
        // 没有翅膀或无法飞行时不触发飞行姿势；飞行中始终允许停下。
        if (!flight.isFlying()
                && (flight.getWing() == FlightApparatus.NONE || !flight.canFly(player))) {
            return;
        }
        flight.toggleIsFlying(Flight.PlayerSet.ofOthers());
    }
}
