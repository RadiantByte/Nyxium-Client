package com.radiantbyte.nyxium.client.game.entity

import com.radiantbyte.nyxium.client.game.GameSession
import com.radiantbyte.nyxium.client.game.inventory.AbstractInventory
import com.radiantbyte.nyxium.client.game.inventory.ContainerInventory
import com.radiantbyte.nyxium.client.game.inventory.PlayerInventory
import org.cloudburstmc.math.vector.Vector3f
import org.cloudburstmc.protocol.bedrock.data.AuthoritativeMovementMode
import org.cloudburstmc.protocol.bedrock.data.SoundEvent
import org.cloudburstmc.protocol.bedrock.data.inventory.transaction.InventoryTransactionType
import org.cloudburstmc.protocol.bedrock.packet.AnimatePacket
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket
import org.cloudburstmc.protocol.bedrock.packet.ContainerClosePacket
import org.cloudburstmc.protocol.bedrock.packet.ContainerOpenPacket
import org.cloudburstmc.protocol.bedrock.packet.InventoryTransactionPacket
import org.cloudburstmc.protocol.bedrock.packet.LevelSoundEventPacket
import org.cloudburstmc.protocol.bedrock.packet.PlayerAuthInputPacket
import org.cloudburstmc.protocol.bedrock.packet.StartGamePacket
import java.util.UUID

@Suppress("MemberVisibilityCanBePrivate")
class LocalPlayer(val session: GameSession) : Player(0L, 0L, UUID.randomUUID(), "") {

    override var runtimeEntityId: Long = 0L
        private set

    override var uniqueEntityId: Long = 0L
        private set

    override var uuid: UUID = UUID.randomUUID()
        private set

    var blockBreakServerAuthoritative = false
        private set

    var movementServerAuthoritative = true
        private set

    var inventoriesServerAuthoritative = false
        private set

    var soundServerAuthoritative = false
        private set

    override val inventory = PlayerInventory(this)

    var openContainer: AbstractInventory? = null
        private set

    override fun onPacketBound(packet: BedrockPacket) {
        super.onPacketBound(packet)
        if (packet is StartGamePacket) {
            runtimeEntityId = packet.runtimeEntityId
            uniqueEntityId = packet.uniqueEntityId

            movementServerAuthoritative =
                packet.authoritativeMovementMode != AuthoritativeMovementMode.CLIENT
            packet.authoritativeMovementMode = AuthoritativeMovementMode.SERVER
            inventoriesServerAuthoritative = packet.isInventoriesServerAuthoritative
            blockBreakServerAuthoritative = packet.isServerAuthoritativeBlockBreaking
            soundServerAuthoritative = packet.networkPermissions.isServerAuthSounds

            reset()
        }
        if (packet is PlayerAuthInputPacket) {
            move(packet.position)
            rotate(packet.rotation)
            tickExists = packet.tick
        }
        if (packet is ContainerOpenPacket) {
            openContainer = if (packet.id.toInt() == 0) {
                return
            } else {
                ContainerInventory(packet.id.toInt(), packet.type)
            }
        }
        if (packet is ContainerClosePacket && packet.id.toInt() == openContainer?.containerId) {
            openContainer = null
        }

        inventory.onPacketBound(packet)
        openContainer?.also {
            if (it is ContainerInventory) {
                it.onPacketBound(packet)
            }
        }
    }

    override fun onDisconnect() {
        super.onDisconnect()
        reset()
    }

}
