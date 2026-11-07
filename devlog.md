# yan-wings 开发日志

> 记录构建、测试与问题修复。新条目按时间倒序或追加在对应章节。

## 2026-11-07 · 物品栏独立与纹理分离- **独立模组物品栏**：移除物品在原版 `FOOD_AND_DRINKS` / `TOOLS_AND_UTILITIES` 标签中的注册，新增独立的 `CreativeModeTab`（key: `yan_wings:wings_tab`）。
- **物品栏图标**：新增专用图标物品 `wings_tab_icon`，使用 `textures/icon/wings.png`（32×32 自定义图标）作为栏位图标。（原 `mob_effect/wings.png` 为 18×18 非标准尺寸导致破图，已替换）
- **Trinket 翅膀纹理分离**：Trinket 翅膀饰品（`angel_wings` 等 11 种）原先复用翅膀瓶的纹理（`xxx_wings_bottle.png`），现改为独立引用无瓶子的纹理（`xxx_wings.png`）。
  - 新建 11 个 `models/item/{name}.json` 模型文件，纹理路径去掉 `_bottle` 后缀。
  - 修改 11 个 `items/{name}.json` 物品定义文件，模型引用从 `_bottle` 版改为无瓶子版。
- **修改文件**：
  - `WingsItems.java`：注册独立物品栏、注册图标物品
  - `items/wings_tab_icon.json`：新增图标物品定义
  - `models/item/wings_tab_icon.json`：新增图标模型
  - `models/item/` 下 11 个 trinket 翅膀模型：新增
  - `items/` 下 11 个 trinket 翅膀物品定义：修改模型引用

## 2026-11-07 · 第一次构建与初步测试

- 构建命令：`.\gradlew.bat :fabric:build -x test`
- 产物：`yan_wings-26.2-Fabric-1.0.0.jar`
- 初步结论：基本功能正常（饰品可装备、翅膀可渲染、可飞行）。

### 问题 1 · 上游遗留：死亡后重新喝药水无法渲染翅膀

- **现象**：死亡后药水效果被清除、翅膀消失；再次喝药水获得新的翅膀效果时，无法渲染翅膀模型与动画。
- **来源**：上游（lv-wings）已存在。
- **状态**：已修复（待游戏内验证）

### 问题 2 · 开启死亡不掉落时动画冻结

- **现象**：`keepInventory=true` 时，死亡后翅膀渲染停留在死亡前状态，无法播放动画；退出存档重进后恢复。
- **状态**：已修复（待游戏内验证）

### 问题 3 · 关闭死亡不掉落时 R 键姿势残留

- **现象**：`keepInventory=false` 时，死亡后饰品丢失，但按 R 仍会触发起飞姿势/动画（无实际飞行效果）。
- **期望**：饰品已丢失时不应触发 R 键飞行姿势。
- **状态**：已修复（待游戏内验证）

### 问题 4 · 药水与饰品互转合成表未生效

- **现象**：药水物品与饰品物品的互相合成配方未被游戏加载，无法触发合成。
- **状态**：已修复（待游戏内验证）

## 2026-11-07 · 修复记录

### 问题 4：配方格式不兼容 26.2

- **原因**：26.2 的 `crafting_shapeless` 配方 `ingredients` 使用字符串形式（如 `"yan_wings:angel_wings_bottle"`），原实现使用了旧版 `{"item": ...}` 对象形式，导致配方解析失败、未被加载。
- **修改**：重写 `data/yan_wings/recipe/*.json` 共 22 个配方，改用字符串 ingredients；已核对原版 26.2 配方格式（`minecraft-client.jar` 内 `data/minecraft/recipe/*.json`）。
- **状态**：已修复，待游戏内验证。

### 问题 1 / 2 / 3：重生后客户端飞行状态不同步

- **原因**：玩家重生时服务端创建新实体（entity id 变化），客户端复用同一 `LocalPlayer` 并调用 `setId` 改写 id。`MessageSyncFlight` 以 entity id 定位玩家，客户端 `level.getEntity(newId)` 因实体表仍以旧 id 索引而返回 null，同步包被丢弃，导致：
  - 重新喝药水后客户端 wing 不同步 → 不渲染（问题 1）；
  - `keepInventory` 时客户端沿用死亡前状态 → 动画冻结（问题 2）；
  - 饰品丢失后客户端仍保留 wing / `isFlying` → R 键仍有飞行姿势（问题 3）。
- **修改**：
  1. `MessageSyncFlight` 改用玩家 `UUID` 定位，替代 entity id；
  2. `ClientFlightSyncApplier` 按 UUID 在 `level.players()` 查找，找不到时回退到本地玩家；
  3. `ClientEventHandlerSupport` 检测 `LocalPlayer.getId()` 变化时 `FlightViews.invalidate`，重置动画视图缓存；
  4. `ClientFlightInputActions.toggleLocalFlight` 增加 `canFly` 与 wing 校验，无翅膀时不再触发飞行姿势。
- **状态**：已修复，待游戏内验证。

### 构建验证

- `.\gradlew.bat :fabric:build -x test` → `BUILD SUCCESSFUL`
- 74 个资源 JSON 全部通过解析校验。
- 产物：`fabric/build/libs/yan_wings-26.2-Fabric-1.0.0.jar`

## 2026-11-07 · 问题 1（死亡后动画冻结）再修复

- **现象**：死亡/复活后，翅膀的模型、纹理和动画停留在死亡前的状态不再更新；退出存档重进后恢复。
- **根因（已通过反汇编确认）**：26.2 客户端重生时并不复用旧 `LocalPlayer`，而是通过 `MultiPlayerGameMode.createPlayer(...)` **新建一个 `LocalPlayer` 实例**，再调用 `setId(旧实例的 entity id)` 沿用旧 id，最后替换 `Minecraft.player`。
  - 旧实例可能仍被 `level` 实体表或 `FlightViews` 缓存持有，且已不再 tick；
  - 渲染/同步若按 entity id 解析，会命中旧实例；`FlightViewDefault.tick()` 因旧实例的 `player.tickCount` 不再变化而提前返回，`animator.update()` 不再执行 → 动画冻结。
- **修改**：
  1. `ClientRenderHookSupport.resolvePlayer`、`WingsLayerRenderer.resolvePlayer`：当 `state.id == Minecraft.player.getId()` 时直接返回当前 `Minecraft.player`，避免解析到旧实例；
  2. `ClientFlightSyncApplier`：本地玩家优先同步到 `Minecraft.player`，避免写进旧实例；
  3. `ClientEventHandlerSupport.tickClientPlayerIdentity`：改为按 `LocalPlayer` **实例**变化检测重生，变化时 `FlightViews.invalidate` 并清理 `FlightPoseSupport` 缓存。
- **状态**：已修复，待游戏内验证。

- **补充**：之前一版仅把同步包改成 UUID、并按 entity id 变化清理缓存；由于重生后新旧实例的 entity id 相同，id 变化检测不会触发，且渲染仍可能命中旧实例，因此没有解决冻结问题。本版改为“实例优先”。

## 2026-11-08 · 创造栏图标破图（纹理未进入图集）

- **现象**：独立创造栏的图标显示为破图 / 纹理加载失败。
- **根因**：26.2 的物品图集 `assets/minecraft/atlases/items.json` 只通过 `directory` 源收录 `textures/item/`（另加 trims 的 `paletted_permutations`），`textures/icon/` 不在 `items` / `blocks` / `gui` / `particles` 任何一个图集的 source 中。物品模型 `models/item/wings_tab_icon.json` 引用 `yan_wings:icon/wings`，该 sprite 未被 stitch 进图集 → missing texture。
- **修改**：
  1. 图标纹理移动到 `assets/yan_wings/textures/item/wings_tab_icon.png`（32×32 尺寸本身没问题）；
  2. `models/item/wings_tab_icon.json` 的 `layer0` 改为 `yan_wings:item/wings_tab_icon`；
  3. 删除已空的 `textures/icon/`。
- **状态**：已修复，待游戏内验证。
- **说明**：物品纹理应放在 `textures/item/`；若确实想用自定义目录，需要额外提供 `assets/<ns>/atlases/items.json`，为对应目录添加 `minecraft:directory` source。
