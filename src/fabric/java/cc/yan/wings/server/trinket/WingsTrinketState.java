package cc.yan.wings.server.trinket;

import cc.yan.wings.WingsCore;
import cc.yan.wings.server.apparatus.FlightApparatus;
import cc.yan.wings.server.effect.WingsEffects;
import cc.yan.wings.server.flight.Flight;
import eu.pb4.trinkets.api.TrinketAttachment;
import eu.pb4.trinkets.api.TrinketInventory;
import eu.pb4.trinkets.api.TrinketsApi;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * 由 Trinkets 的 chest/cape 槽位提供的翅膀状态。
 * 该状态优先于命令/药水设置的翅膀；每 tick 强制同步并刷新药水效果。
 */
@SuppressWarnings("null")
public final class WingsTrinketState {
    private static final AttachmentType<Boolean> APPLIED = AttachmentRegistry.create(WingsCore.locate("trinket_wing_applied"));

    private WingsTrinketState() {
    }

    public static FlightApparatus getWing(Player player) {
        TrinketAttachment attachment = TrinketsApi.getAttachment(player);
        if (attachment == null) {
            return null;
        }
        TrinketInventory inventory = attachment.getInventory(WingsTrinketItems.SLOT);
        if (inventory == null || inventory.getContainerSize() == 0) {
            return null;
        }
        ItemStack stack = inventory.getItem(0);
        return WingsTrinketItems.getWing(stack);
    }

    public static void tick(Player player, Flight flight) {
        FlightApparatus wing = getWing(player);
        boolean applied = player.getAttachedOrElse(APPLIED, false);
        if (wing != null) {
            if (flight.getWing() != wing) {
                flight.setWing(wing, Flight.PlayerSet.ofAll());
            }
            if (WingsEffects.WINGS.isBound() && !player.hasEffect(WingsEffects.WINGS)) {
                player.addEffect(new MobEffectInstance(
                        WingsEffects.WINGS,
                        MobEffectInstance.INFINITE_DURATION,
                        0,
                        true,
                        false));
            }
            if (!applied) {
                player.setAttached(APPLIED, true);
            }
        } else if (applied) {
            flight.setWing(FlightApparatus.NONE, Flight.PlayerSet.ofAll());
            if (WingsEffects.WINGS.isBound()) {
                player.removeEffect(WingsEffects.WINGS);
            }
            player.setAttached(APPLIED, false);
        }
    }
}
