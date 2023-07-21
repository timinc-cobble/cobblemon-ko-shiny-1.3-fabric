package us.timinc.mc.cobblemon.koshiny.store

import com.cobblemon.mod.common.api.storage.player.PlayerDataExtension
import com.google.gson.JsonObject

class WildKos : PlayerDataExtension {
    companion object {
        const val name = "wildKos"
    }

    val wildKos = mutableMapOf<String, Int>()

    fun resetDefeats() {
        wildKos.clear()
    }

    fun addDefeat(defeatedPokemonResourceIdentifier: String) {
        wildKos[defeatedPokemonResourceIdentifier] =
            getDefeats(defeatedPokemonResourceIdentifier) + 1
    }

    fun getDefeats(defeatedPokemonResourceIdentifier: String): Int {
        return wildKos.getOrDefault(defeatedPokemonResourceIdentifier, 0)
    }

    override fun deserialize(json: JsonObject): WildKos {
        val defeatsData = json.getAsJsonObject("defeats")
        for (pokemonResourceIdentifier in defeatsData.keySet()) {
            wildKos[pokemonResourceIdentifier] = defeatsData.get(pokemonResourceIdentifier).asInt
        }

        return this
    }

    override fun name(): String {
        return name
    }

    override fun serialize(): JsonObject {
        val json = JsonObject()
        json.addProperty("name", name)

        val defeatsData = JsonObject()
        for (pokemonResourceIdentifier in wildKos.keys) {
            defeatsData.addProperty(pokemonResourceIdentifier, wildKos[pokemonResourceIdentifier])
        }
        json.add("defeats", defeatsData)

        return json
    }
}