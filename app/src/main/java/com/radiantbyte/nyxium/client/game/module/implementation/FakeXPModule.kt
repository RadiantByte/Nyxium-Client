package com.radiantbyte.nyxium.client.game.module.implementation

import com.radiantbyte.nyxium.client.game.InterceptablePacket
import com.radiantbyte.nyxium.client.game.Module
import com.radiantbyte.nyxium.client.game.ModuleCategory
import org.cloudburstmc.protocol.bedrock.data.entity.EntityEventType
import org.cloudburstmc.protocol.bedrock.packet.EntityEventPacket
import org.cloudburstmc.protocol.bedrock.packet.PlayerAuthInputPacket

class FakeXPModule : Module("xp_levels", ModuleCategory.Implementation) {

    private val intervalValue by intValue("interval", 1000, 100..5000)
    private val levelsValue by intValue("levels", 1, 1..100)

    private var lastXPTime = 0L

    override fun beforePacketBound(interceptablePacket: InterceptablePacket) {
        if (!isEnabled) {
            return
        }

        val packet = interceptablePacket.packet
        if (packet is PlayerAuthInputPacket) {
            val currentTime = System.currentTimeMillis()

            if (currentTime - lastXPTime >= intervalValue) {
                lastXPTime = currentTime
                addXPLevels()
            }
        }
    }

    private fun addXPLevels() {
        val entityEventPacket = EntityEventPacket().apply {
            runtimeEntityId = session.localPlayer.runtimeEntityId
            type = EntityEventType.PLAYER_ADD_XP_LEVELS
            data = levelsValue
        }
        session.clientBound(entityEventPacket)
    }
}