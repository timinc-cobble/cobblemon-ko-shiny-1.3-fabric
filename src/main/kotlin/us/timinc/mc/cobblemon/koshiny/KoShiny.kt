package us.timinc.mc.cobblemon.koshiny

import com.cobblemon.mod.common.Cobblemon.config
import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.api.spawning.context.SpawningContext
import draylar.omegaconfig.OmegaConfig
import net.fabricmc.api.ModInitializer
import net.minecraft.world.entity.ai.targeting.TargetingConditions
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import us.timinc.mc.cobblemon.counter.Counter
import us.timinc.mc.cobblemon.koshiny.config.KoShinyConfig
import kotlin.random.Random.Default.nextInt

object KoShiny : ModInitializer {
    const val MOD_ID = "ko_shiny"
    private var koShinyConfig: KoShinyConfig = OmegaConfig.register(KoShinyConfig::class.java)

    override fun onInitialize() {}

    fun modifyShinyRate(ctx: SpawningContext, props: PokemonProperties) {
        if (props.shiny != null || props.species == null) {
            return
        }

        val world: Level = ctx.world
        val possibleMaxPlayer = world.getNearbyPlayers(
            TargetingConditions.forNonCombat()
                .ignoreLineOfSight()
                .ignoreInvisibilityTesting(),
            null,
            AABB.ofSize(
                Vec3.atCenterOf(ctx.position),
                koShinyConfig.effectiveRange.toDouble(),
                koShinyConfig.effectiveRange.toDouble(),
                koShinyConfig.effectiveRange.toDouble()
            )
        ).stream().max(Comparator.comparingInt { player: Player? ->
            Counter.getPlayerKoStreak(
                player!!, props.species!!
            )
        })
        if (possibleMaxPlayer.isEmpty) {
            return
        }

        val maxPlayer = possibleMaxPlayer.get()
        val maxKoStreak = Counter.getPlayerKoCount(maxPlayer, props.species!!)
        val shinyChances = koShinyConfig.getThreshold(maxKoStreak) + 1

        val shinyRate: Int = config.shinyRate.toInt()
        val shinyRoll = nextInt(shinyRate)
        props.shiny = shinyRoll < shinyChances
    }
}