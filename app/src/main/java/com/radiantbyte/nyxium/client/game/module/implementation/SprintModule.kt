package com.radiantbyte.nyxium.client.game.module.implementation

import com.radiantbyte.nyxium.client.game.InterceptablePacket
import com.radiantbyte.nyxium.client.game.Module
import com.radiantbyte.nyxium.client.game.ModuleCategory
import org.cloudburstmc.protocol.bedrock.data.PlayerAuthInputData
import org.cloudburstmc.protocol.bedrock.packet.PlayerAuthInputPacket

class SprintModule : Module("sprint", ModuleCategory.Implementation) {

    override fun beforePacketBound(interceptablePacket: InterceptablePacket) {
        val packet = interceptablePacket.packet
        if (packet is PlayerAuthInputPacket && isEnabled) {
            packet.inputData.add(PlayerAuthInputData.SPRINTING)
            packet.inputData.add(PlayerAuthInputData.START_SPRINTING)
        } else if (packet is PlayerAuthInputPacket && !isEnabled) {
            packet.inputData.add(PlayerAuthInputData.STOP_SPRINTING)
        }
    }
}