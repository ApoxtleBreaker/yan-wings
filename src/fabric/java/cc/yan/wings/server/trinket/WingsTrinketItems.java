package cc.yan.wings.server.trinket;

import cc.yan.wings.WingsCore;
import cc.yan.wings.WingsMod;
import cc.yan.wings.server.apparatus.FlightApparatus;
import cc.yan.wings.server.sound.WingsSounds;
import eu.pb4.trinkets.api.TrinketDropRule;
import eu.pb4.trinkets.api.component.TrinketDataComponents;
import eu.pb4.trinkets.api.component.TrinketEquippable;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 翅膀饰品物品：与药水版一一对应，可佩戴在 Trinkets 的 chest/cape 槽位。
 */
@SuppressWarnings("null")
public final class WingsTrinketItems {
    public static final String SLOT = "chest/cape";

    private static final Map<Item, FlightApparatus> WINGS = new HashMap<>();
    private static final Map<String, Supplier<Item>> ALL = new LinkedHashMap<>();

    public static final Supplier<Item> ANGEL = register("angel_wings", () -> WingsMod.ANGEL_WINGS);
    public static final Supplier<Item> PARROT = register("parrot_wings", () -> WingsMod.PARROT_WINGS);
    public static final Supplier<Item> SLIME = register("slime_wings", () -> WingsMod.SLIME_WINGS);
    public static final Supplier<Item> BLUE_BUTTERFLY = register("blue_butterfly_wings", () -> WingsMod.BLUE_BUTTERFLY_WINGS);
    public static final Supplier<Item> MONARCH_BUTTERFLY = register("monarch_butterfly_wings", () -> WingsMod.MONARCH_BUTTERFLY_WINGS);
    public static final Supplier<Item> FIRE = register("fire_wings", () -> WingsMod.FIRE_WINGS);
    public static final Supplier<Item> BAT = register("bat_wings", () -> WingsMod.BAT_WINGS);
    public static final Supplier<Item> FAIRY = register("fairy_wings", () -> WingsMod.FAIRY_WINGS);
    public static final Supplier<Item> EVIL = register("evil_wings", () -> WingsMod.EVIL_WINGS);
    public static final Supplier<Item> DRAGON = register("dragon_wings", () -> WingsMod.DRAGON_WINGS);
    public static final Supplier<Item> LVJIA_SUPER = register("lvjia_super_wings", () -> WingsMod.LVJIA_SUPER_WINGS);

    private WingsTrinketItems() {
    }

    /**
     * 触发本类静态初始化，确保饰品物品在注册阶段完成。
     */
    public static void register() {
    }

    private static Supplier<Item> register(String name, Supplier<FlightApparatus> wingFactory) {
        Item item = Registry.register(BuiltInRegistries.ITEM, WingsCore.locate(name), new Item(properties(name)));
        WINGS.put(item, wingFactory.get());
        ALL.put(name, () -> item);
        return () -> item;
    }

    private static Item.Properties properties(String name) {
        return new Item.Properties()
                .setId(ResourceKey.create(Registries.ITEM, WingsCore.locate(name)))
                .stacksTo(1)
                .component(TrinketDataComponents.EQUIPMENT,
                        TrinketEquippable.DEFAULT
                                .withSlots(SLOT)
                                .withEquipSound(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(WingsSounds.ITEM_ARMOR_EQUIP_WINGS.get()))
                                .withDropRule(TrinketDropRule.DEFAULT)
                                .withEquipOnInteract(true));
    }

    public static FlightApparatus getWing(ItemStack stack) {
        return stack.isEmpty() ? null : WINGS.get(stack.getItem());
    }

    public static void forEach(Consumer<Supplier<Item>> consumer) {
        ALL.values().forEach(consumer);
    }
}
