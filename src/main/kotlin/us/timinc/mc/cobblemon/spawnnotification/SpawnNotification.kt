package us.timinc.mc.cobblemon.spawnnotification

import com.cobblemon.mod.common.api.Priority
import com.cobblemon.mod.common.api.events.CobblemonEvents
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvent
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent
import net.neoforged.neoforge.event.server.ServerStartedEvent
import us.timinc.mc.cobblemon.spawnnotification.config.ConfigBuilder
import us.timinc.mc.cobblemon.spawnnotification.config.SpawnNotificationConfig
import us.timinc.mc.cobblemon.spawnnotification.events.BroadcastDespawn
import us.timinc.mc.cobblemon.spawnnotification.events.BroadcastSpawn
import us.timinc.mc.cobblemon.spawnnotification.events.PlayShinyPlayerSound
import us.timinc.mc.cobblemon.spawnnotification.events.PlayShinySound

@Mod(SpawnNotification.MOD_ID)
object SpawnNotification {
    const val MOD_ID = "spawn_notification"
    var config: SpawnNotificationConfig = ConfigBuilder.load(SpawnNotificationConfig::class.java, MOD_ID)
    var eventsListening = false

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
            CobblemonEvents.POKEMON_ENTITY_SPAWN.subscribe(Priority.LOWEST, BroadcastSpawn::handle)
            CobblemonEvents.POKEMON_ENTITY_SPAWN.subscribe(Priority.LOWEST, PlayShinySound::handle)
            CobblemonEvents.POKEMON_SENT_POST.subscribe(Priority.LOWEST, PlayShinyPlayerSound::handle)
            CobblemonEvents.POKEMON_CAPTURED.subscribe(Priority.LOWEST, BroadcastDespawn::handle)
            CobblemonEvents.POKEMON_FAINTED.subscribe(Priority.LOWEST, BroadcastDespawn::handle)
        }

        @SubscribeEvent
        fun onEntityUnload(e: EntityLeaveLevelEvent) = BroadcastDespawn.handle(e)
    }
}