package cc.yan.wings.server.flight;

final class FlightMovementChecks {
    private FlightMovementChecks() {
    }

    static boolean exceedsDownwardLimit(double verticalMovement, double limit) {
        return verticalMovement < -limit;
    }
}
