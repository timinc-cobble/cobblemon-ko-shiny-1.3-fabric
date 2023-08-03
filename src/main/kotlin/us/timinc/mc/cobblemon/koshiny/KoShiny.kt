package us.timinc.mc.cobblemon.koshiny

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.Cobblemon.config
import com.cobblemon.mod.common.api.events.CobblemonEvents
import com.cobblemon.mod.common.api.events.battles.BattleVictoryEvent
import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.cobblemon.mod.common.api.spawning.context.SpawningContext
import com.cobblemon.mod.common.api.storage.player.PlayerDataExtensionRegistry
import com.cobblemon.mod.common.util.getPlayer
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.builder.RequiredArgumentBuilder.argument
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.commands.CommandSourceStack
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.ai.targeting.TargetingConditions
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import us.timinc.mc.cobblemon.koshiny.store.WildKos
import java.util.*
import kotlin.random.Random.Default.nextInt

object KoShiny : ModInitializer {

    override fun onInitialize() {
        PlayerDataExtensionRegistry.register(WildKos.name, WildKos::class.java)

        CobblemonEvents.BATTLE_VICTORY.subscribe { battleVictoryEvent ->
            if (!battleVictoryEvent.battle.isPvW) return@subscribe

            handleWildDefeat(battleVictoryEvent)
        }

        CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
            literal<CommandSourceStack>("checkkos")
                .then(
                    argument<CommandSourceStack?, String?>(
                        "name",
                        StringArgumentType.greedyString()
                    ).executes { checkScore(it) }).register(dispatcher)
            literal<CommandSourceStack>("resetkos")
                .executes { resetScore(it) }
                .register(dispatcher)
        }
    }

    private fun getPlayerKoStreak(player: Player, species: String): Int {
        val data = Cobblemon.playerData.get(player)
        return (data.extraData.getOrPut(WildKos.name) { WildKos() } as WildKos).getDefeats(species)
    }

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
            AABB.ofSize(Vec3.atCenterOf(ctx.position), 64.0, 64.0, 64.0)
        ).stream().max(Comparator.comparingInt { player: Player? ->
            getPlayerKoStreak(
                player!!, props.species!!
            )
        })
        if (possibleMaxPlayer.isEmpty) {
            return
        }

        val maxPlayer = possibleMaxPlayer.get()
        val maxKoStreak = getPlayerKoStreak(maxPlayer, props.species!!)
        val shinyChances = when {
            maxKoStreak > 500 -> 4
            maxKoStreak > 300 -> 3
            maxKoStreak > 100 -> 2
            else -> 1
        }

        val shinyRate: Int = config.shinyRate.toInt()
        val shinyRoll = nextInt(shinyRate)
        props.shiny = shinyRoll < shinyChances
    }

    private fun handleWildDefeat(battleVictoryEvent: BattleVictoryEvent) {
        val wildPokemons = battleVictoryEvent.battle.actors.flatMap { it.pokemonList }.map { it.originalPokemon }
            .filter { !it.isPlayerOwned() }

        battleVictoryEvent.winners
            .flatMap { it.getPlayerUUIDs().mapNotNull(UUID::getPlayer) }
            .forEach { player ->
                val data = Cobblemon.playerData.get(player)
                val wildKos: WildKos =
                    data.extraData.getOrPut(WildKos.name) { WildKos() } as WildKos
                wildPokemons.forEach { wildPokemon ->
                    wildKos.addDefeat(wildPokemon.species.name.lowercase(Locale.getDefault()))
                }
                Cobblemon.playerData.saveSingle(data)
            }
    }

    @Suppress("SameReturnValue")
    private fun checkScore(context: CommandContext<CommandSourceStack>): Int {
        val queriedPokemonResourceIdentifier = StringArgumentType.getString(context, "name")
        val player = context.source.playerOrException
        val data = Cobblemon.playerData.get(player)

        val wildDefeats = data.extraData.getOrPut(WildKos.name) { WildKos() } as WildKos
        val currentCount = wildDefeats.getDefeats(queriedPokemonResourceIdentifier.toString())

        if (currentCount == 0) {
            context.source.sendSuccess(Component.translatable("koshiny.nostreak"), true)
        } else {
            val currentPokemonSpecies =
                PokemonSpecies.getByIdentifier(
                    ResourceLocation(
                        "cobblemon",
                        queriedPokemonResourceIdentifier.toString()
                    )
                )

            if (currentPokemonSpecies == null) {
                context.source.sendSuccess(
                    Component.translatable("koshiny.error.invalidPokemonIdentifier"),
                    true
                )
            } else {
                context.source.sendSuccess(
                    Component.translatable(
                        "koshiny.streak",
                        Component.literal(currentCount.toString()),
                        Component.literal(currentPokemonSpecies.name)
                    ), true
                )
            }
        }

        return Command.SINGLE_SUCCESS
    }

    @Suppress("SameReturnValue")
    private fun resetScore(context: CommandContext<CommandSourceStack>): Int {
        val player = context.source.playerOrException
        val data = Cobblemon.playerData.get(player)

        val wildDefeats = data.extraData.getOrPut(WildKos.name) { WildKos() } as WildKos
        wildDefeats.resetDefeats()
        Cobblemon.playerData.saveSingle(data)

        context.source.sendSuccess(Component.translatable("koshiny.successfulReset"), true)

        return Command.SINGLE_SUCCESS
    }
}

// We can write extension functions to reduce nesting in our command logic if we wanted to
fun LiteralArgumentBuilder<CommandSourceStack>.register(dispatcher: CommandDispatcher<CommandSourceStack>) {
    dispatcher.register(this)
}