# Changelog

All notable changes to Heart of Life will be documented here.

---

## [1.0.4]

### Fixed
- Compatibility issues with Minecraft versions above 1.21.2
- Recipe unlock bug where obtaining the Heart of Life or Heart Fragment did not properly unlock their recipes in the crafting table

---

## [1.0.3]

### Added
- **Custom Advancements** 6 new advancements tracking Heart and Heart Fragment milestones throughout progression
- **Tooltips** Heart of Life and Heart Fragment now show custom tooltips explaining their usage and effects
- **Creative Tab** Heart of Life and Heart Fragment now have a dedicated creative tab
- **Colored Chat Messages** Heart gain and max heart messages now display in colored text
    - Heart gain messages display in red
    - Max heart warning messages display in yellow

### Fixed
- Recipe unlocks now correctly trigger regardless of the order required items are obtained

---

## [1.0.2]

### Added
- **Heart Fragment** A new rare crafting ingredient required to craft the Heart of Life
    - Stackable up to 16
    - Displayed in yellow rarity

### Changed
- **Heart of Life Recipe** Now requires 4 Heart Fragments to craft instead of the previous materials
- **Stack Size** Heart of Life stack size reduced from 16 to 1 since it is a powerful permanent upgrade
- **Heart of Life Rarity** Now displays in red rarity reflecting its value in progression

---

## [1.0.1]

### Changed
- **Heart of Life** Now fireproof and immune to fire and lava damage
    - Reflects its crafting ingredients which include netherite scraps

---

## [1.0.0]

### Added
- **Heart of Life** Core item that permanently increases the player's max hearts by 1 when used
    - Stackable up to 16
    - Crafted using netherite scraps and other materials
- **Heart Progression System** A permanent heart system inspired by Lifesteal SMP built for single player
    - Players start with 10 hearts
    - Lose 1 permanent heart on every death
    - Regain hearts by crafting and using a Heart of Life
- **Heart Floor** Players cannot go below 1 heart keeping death from being permanent
- **Max Heart Limit** Players cannot exceed 20 hearts keeping progression balanced

---

## Notes
- Requires **Fabric Loader** and **Fabric API**
- Compatible with **Minecraft 1.21.2+**