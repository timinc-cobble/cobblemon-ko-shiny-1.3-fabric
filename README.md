# KO Shiny

Increase your chances of getting a shiny Pokemon by KOing wild versions of it!

## Help

[Discord](https://discord.com/invite/WKAR27SdSv)

## Features

### Defeat Streak

Whenever a player defeats a wild Pokémon, a counter associated with that Pokémon’s species is increased for that player. This counter is never reset, and persists throughout the life of the player on that world.

### Bonus Shiny Chance

As a player’s wild Pokémon KO counter increases, any Pokémon of that counter’s species that spawns nearby will gain a higher chance of becoming shiny. The chances are organized by thresholds; when a player’s KO counter becomes high enough, they unlock additional chances. When there are multiple players nearby who have unlocked a threshold for the spawning Pokémon’s species, the player with the highest unlocked threshold will add their chances.

### Config

In the config, you can change the range at which a Pokémon considers a player’s count when spawning. By default it’s 64 blocks. You can also configure the thresholds, which by default are at a count of 101+ you get 2 chances, 301+ you get 3, and 501+ you get 4. If there isn’t a player nearby who has achieved a threshold, there’s just 1 chance. The other side of the chance is determined by the `shinyRate` in the Cobblemon config, which is 8196 by default.

## Dependencies

Cobblemon [Modrinth](https://modrinth.com/mod/cobblemon) / [CurseForge](https://www.curseforge.com/minecraft/mc-mods/cobblemon)

Fabric Language Kotlin [Modrinth](https://modrinth.com/mod/fabric-language-kotlin) / [CurseForge](https://www.curseforge.com/minecraft/mc-mods/fabric-language-kotlin)
