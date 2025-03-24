package com.radiantbyte.nyxium.client.game.module.implementation

import com.radiantbyte.nyxium.client.game.InterceptablePacket
import com.radiantbyte.nyxium.client.game.Module
import com.radiantbyte.nyxium.client.game.ModuleCategory
import org.cloudburstmc.protocol.bedrock.data.entity.EntityEventType
import org.cloudburstmc.protocol.bedrock.packet.EntityEventPacket

class NoHurtCameraModule : Module("no_hurt_camera", ModuleCategory.Implementation) {

    override fun beforePacketBound(interceptablePacket: InterceptablePacket) {
        if (!isEnabled) {
            return
        }

        val packet = interceptablePacket.packet
        if (packet is EntityEventPacket) {
            if (packet.runtimeEntityId == session.localPlayer.runtimeEntityId
                && packet.type == EntityEventType.HURT
            ) {
                interceptablePacket.intercept()
            }
        }
    }

}