package us.timinc.mc.cobblemon.spawnnotification

import com.cobblemon.mod.common.api.Priority
import com.cobblemon.mod.common.api.events.CobblemonEvents
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvent
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent
import net.neoforged.neoforge.event.server.ServerStartedEvent
import us.timinc.mc.cobblemon.spawnnotification.config.ConfigBuilder
import us.timinc.mc.cobblemon.spawnnotification.config.SpawnNotificationConfig
import us.timinc.mc.cobblemon.spawnnotification.events.*

@Mod(SpawnNotification.MOD_ID)
object SpawnNotification {
    const val MOD_ID = "spawn_notification"
    const val SPAWN_BROADCASTED = "spawn_notification:spawn_broadcasted"
    const val BUCKET = "spawn_notification:bucket"
    const val SHOULD_BROADCAST_FAINT = "spawn_notification:should_broadcast_faint"
    const val FAINT_ENTITY = "spawn_notification:faint_entity"
    var config: SpawnNotificationConfig = ConfigBuilder.load(SpawnNotificationConfig::class.java, MOD_ID)
    var eventsListening = false
    var journeyMapPresent: Boolean = false
    var xaerosPresent: Boolean = false

    @JvmStatic
    var SHINY_SOUND_ID: ResourceLocation = ResourceLocation.parse("$MOD_ID:pla_shiny")

    @JvmStatic
    var SHINY_SOUND_EVENT: SoundEvent = SoundEvent.createVariableRangeEvent(SHINY_SOUND_ID)

    @EventBusSubscriber(modid = MOD_ID)
    object Registration {
        @SubscribeEvent
        fun onInit(e: ServerStartedEvent) {
            if (eventsListening) return
            eventsListening = true
            CobblemonEvents.POKEMON_ENTITY_SPAWN.subscribe(Priority.LOWEST, AttachBucket::handle)
            CobblemonEvents.POKEMON_ENTITY_SPAWN.subscribe(Priority.LOWEST, BroadcastSpawn::handle)
            CobblemonEvents.POKEMON_ENTITY_SPAWN.subscribe(Priority.LOWEST, PlayShinySound::handle)
            CobblemonEvents.POKEMON_SENT_POST.subscribe(Priority.LOWEST, PlayShinyPlayerSound::handle)
            CobblemonEvents.POKEMON_CAPTURED.subscribe(Priority.LOWEST, BroadcastCapture::handle)
            CobblemonEvents.POKEMON_FAINTED.subscribe(Priority.LOWEST, BroadcastFaint::handle)
            CobblemonEvents.BATTLE_FAINTED.subscribe(Priority.LOWEST, BroadcastFaint::handle)
        }

        @SubscribeEvent
        fun onEntityLoad(e: EntityJoinLevelEvent) {
            val level = e.level as? ServerLevel ?: return
            BroadcastUnnaturalSpawn.handle(e.entity, level)
        }

        @SubscribeEvent
        fun onEntityUnload(e: EntityLeaveLevelEvent) {
            val level = e.level as? ServerLevel ?: return
            BroadcastFaint.handle(e.entity, level)
            BroadcastDespawn.handle(e.entity, level)
        }
    }

    fun onInitializeJourneyMap() {
        journeyMapPresent = true
    }

    fun onInitializeXaeros() {
        xaerosPresent = true
    }
}