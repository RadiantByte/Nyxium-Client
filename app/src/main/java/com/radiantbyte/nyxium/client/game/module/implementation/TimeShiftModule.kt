package com.radiantbyte.nyxium.client.game.module.implementation

import com.radiantbyte.nyxium.client.game.InterceptablePacket
import com.radiantbyte.nyxium.client.game.Module
import com.radiantbyte.nyxium.client.game.ModuleCategory
import org.cloudburstmc.protocol.bedrock.packet.PlayerAuthInputPacket
import org.cloudburstmc.protocol.bedrock.packet.SetTimePacket

class TimeShiftModule : Module("time_shift", ModuleCategory.Implementation) {

    private val time by intValue("time", 6000, 0..24000)
    private var lastTimeUpdate = 0L

    override fun beforePacketBound(interceptablePacket: InterceptablePacket) {
        if (!isEnabled) {
            return
        }

        val packet = interceptablePacket.packet
        if (packet is PlayerAuthInputPacket) {
            val currentTime = System.currentTimeMillis()

            // Update time every 100ms
            if (currentTime - lastTimeUpdate >= 100) {
                lastTimeUpdate = currentTime

                val timePacket = SetTimePacket()
                timePacket.time = time
                session.clientBound(timePacket)
            }
        }
    }

    override fun onDisabled() {
        super.onDisabled()
        if (isSessionCreated) {
            val timePacket = SetTimePacket()
            timePacket.time = 0
            session.clientBound(timePacket)
        }
    }
}
