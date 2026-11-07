# yan-wings

> **非官方二次开发分支**：本仓库基于 [lv-wings](https://github.com/ahasasjeb/lv-wings)（MIT 许可）开发，并新增 Trinkets 饰品槽支持。不是 lv-wings 官方版本。

yan-wings 在 lv-wings（Wings 模组的社区分支）基础上，让翅膀药水可以装备到 [Trinkets](https://modrinth.com/mod/trinkets-updated) 的 `chest/cape` 槽位。

## ✨ 本分支新增

- 每种翅膀新增一个**饰品版物品**（与药水版图标相同），可在合成表中与药水版**双向互转**。
- 饰品佩戴在 Trinkets 的 `chest/cape` 槽位（与披风/鞘翅共用）。
- 佩戴后**持续**提供对应的 `wings` 效果与翅膀模型/动画；卸下立即消失。
- 饰品状态优先级最高，会覆盖命令/药水设置的翅膀。
- 死亡按普通饰品规则处理（`keepInventory` 时保留，复活后自动恢复）。
- 独立的创造模式物品栏。

## 📦 环境要求

| 项目 | 版本 |
|---|---|
| Minecraft | 26.2 |
| Fabric Loader | 0.19.3+ |
| Fabric API | 0.152.1+26.2 |
| Trinkets Updated | 4.1.1+26.2（前置，必须安装） |

## 🔧 安装

1. 安装 Fabric Loader 与 Fabric API。
2. 安装 [Trinkets Updated](https://modrinth.com/mod/trinkets-updated)。
3. 将 `yan-wings-*.jar` 放入 `mods/` 目录。

## 🛠 构建

```bash
./gradlew :fabric:build
```

产物位于 `fabric/build/libs/yan_wings-26.2-Fabric-<version>.jar`。

## 🧪 药水酿造（沿用上游）

基础材料：

- **蝙蝠血瓶**：用空玻璃瓶右键点击蝙蝠获得。

所有翅膀药水都在酿造台中，以**缓降药水**（或长效缓降药水）为基础：

| 翅膀 | 酿造材料 |
|---|---|
| 天使翅膀 | 羽毛 |
| 鹦鹉翅膀 | 红色染料 |
| 蝙蝠翅膀 | 蝙蝠血瓶 |
| 蓝蝴蝶翅膀 | 蓝色染料 |
| 龙翅膀 | 皮革 |
| 邪恶翅膀 | 骨头 |
| 精灵翅膀 | 滨菊 |
| 火焰翅膀 | 烈焰粉 |
| 帝王蝶翅膀 | 橙色染料 |
| 史莱姆翅膀 | 粘液球 |
| LVJIA 超级翅膀 | 仅创造模式 |

## 📜 来源与致谢

- 原始项目：[pau101/Wings](https://github.com/pau101/Wings)（MIT）
- 1.18.2 移植：[jt789/WingsPort](https://github.com/jt789/WingsPort)
- 上游分支：[ahasasjeb/lv-wings](https://github.com/ahasasjeb/lv-wings)（MIT）
- 前置库：[Trinkets](https://github.com/Patbox/trinkets)（MIT，© Emily Rose Ploszaj & Patbox）

本仓库的修改部分 © 2026 yan，同样以 MIT 许可发布。

## 📄 许可证

[MIT](LICENSE.md)。分发时请保留原作者的版权声明。

## 📝 开发日志

见 [devlog.md](devlog.md)。
