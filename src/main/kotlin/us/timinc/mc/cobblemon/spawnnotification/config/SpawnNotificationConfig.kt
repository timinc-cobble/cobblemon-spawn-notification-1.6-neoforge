package us.timinc.mc.cobblemon.spawnnotification.config

import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import us.timinc.mc.cobblemon.spawnnotification.SpawnNotification.MOD_ID

class SpawnNotificationConfig {
    val broadcastShiny = true
    val broadcastCoords = true
    val broadcastBiome = false
    val playShinySound = true
    val playShinySoundPlayer = false
    val announceCrossDimensions = false
    val broadcastDespawns = false
    val broadcastVolatileDespawns = false
    val broadcastSpeciesName = true
    val broadcastPlayerSpawnedOn = false

    val broadcastRange: Int = -1
    val playerLimit: Int = -1

    val labelsForBroadcast: MutableSet<String> = mutableSetOf("legendary")
    val bucketsForBroadcast: MutableSet<String> = mutableSetOf()

    @Suppress("KotlinConstantConditions")
    val broadcastRangeEnabled: Boolean
        get() = broadcastRange > 0

    @Suppress("KotlinConstantConditions")
    val playerLimitEnabled: Boolean
        get() = playerLimit > 0

    val formatting = mutableMapOf(
        "label.legendary" to "LIGHT_PURPLE",
        "bucket.ultra-rare" to "AQUA",
        "shiny" to "GOLD"
    )

    fun getComponent(characteristic: String, vararg props: Any): Component {
        val result = Component.translatable("$MOD_ID.$characteristic", *props)
        val possibleFormatting = formatting[characteristic]?.let(ChatFormatting::getByName)
        return if (possibleFormatting != null) result.withStyle(possibleFormatting) else result
    }

    fun getRawComponent(characteristic: String, vararg props: Any): Component {
        val result = Component.translatable(characteristic, *props)
        val possibleFormatting = formatting[characteristic]?.let(ChatFormatting::getByName)
        return if (possibleFormatting != null) result.withStyle(possibleFormatting) else result
    }
}