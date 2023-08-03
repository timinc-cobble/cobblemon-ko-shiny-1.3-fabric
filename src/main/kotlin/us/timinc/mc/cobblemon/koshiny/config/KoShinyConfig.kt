package us.timinc.mc.cobblemon.koshiny.config

import me.shedaniel.autoconfig.ConfigData
import me.shedaniel.autoconfig.annotation.Config
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment
import us.timinc.mc.cobblemon.koshiny.KoShiny

@Config(name = KoShiny.MOD_ID)
class KoShinyConfig : ConfigData {
    @Comment("The distance at which a spawning Pokemon takes into consideration this player's KO count")
    val effectiveRange = 64

    @Comment("Thresholds for the KO counts : shiny chance bonus")
    val thresholds: Map<Int, Int> = mutableMapOf(Pair(100, 1), Pair(300, 2), Pair(500, 3))

    fun getThreshold(koCount: Int): Int {
        return thresholds.maxOfOrNull { entry -> if (entry.key < koCount) entry.value else 0 } ?: 0
    }
}