package us.timinc.mc.cobblemon.koshiny.config

import me.shedaniel.autoconfig.ConfigData
import me.shedaniel.autoconfig.annotation.Config
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment
import us.timinc.mc.cobblemon.koshiny.KoShiny

@Config(name = KoShiny.MOD_ID)
class KoShinyConfig : ConfigData {
    @Comment("The distance at which a spawning Pokemon takes into consideration this player's KO count")
    val effectiveRange = 64
}