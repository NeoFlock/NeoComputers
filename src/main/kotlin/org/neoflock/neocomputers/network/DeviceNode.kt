package org.neoflock.neocomputers.network

import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import org.neoflock.neocomputers.NeoComputers
import org.neoflock.neocomputers.network.Networking.Message
import org.neoflock.neocomputers.network.Networking.Visibility
import org.neoflock.neocomputers.network.Networking.maxHopCount
import java.util.UUID
import kotlin.math.min

// tmp class until JNI bindings work
data class NNComponent(val type: String)

open class DeviceNode(_address: UUID? = null) {
    val connections = HashSet<DeviceNode>()
    private var reachableCache: Set<DeviceNode>? = null
    var address = _address ?: UUID.randomUUID()

    open var reachability = Visibility.NETWORK
    open var powerRole = PowerRole.CONSUMER
    open var energy: Long = 0
    open var energyCapacity: Long = 0
    // give energy, returns how much was actually given
    // cannot exceed amount specified
    open fun giveEnergy(amount: Long): Long {
        val maximum = min(amount, energyCapacity - energy)
        energy += maximum
        markChanged()
        return maximum
    }
    // take energy out, returns how much was actually taken
    // cannot exceed amount specified
    open fun withdrawEnergy(amount: Long): Long {
        val maximum = min(amount, energy)
        energy -= maximum
        markChanged()
        return maximum
    }

    fun getChargerNodes(): Set<DeviceNode> = getReachable().filter { it.powerRole != PowerRole.CONSUMER }.toSet()
    fun totalEnergyInConnections(): Long = getChargerNodes().fold(0) { acc, node -> acc + node.energy }
    fun maxEnergyInConnections(): Long = getChargerNodes().fold(0) { acc, node -> acc + node.energyCapacity }

    // attempts to consume
    fun consumeEnergy(energy: Long): Boolean {
        // consumes energy, returns false if not enough
        val total = totalEnergyInConnections() + this.energy
        if(energy > total) return false

        var remaining = energy
        remaining -= withdrawEnergy(remaining)
        if(remaining <= 0) return true

        for (charger in getChargerNodes()) {
            if(remaining <= 0) break
            remaining -= charger.withdrawEnergy(remaining)
        }

        return true
    }

    // PLEASE only call if consumer, in the name of all that is holy
    fun tryToChargeFully() {
        var remaining = energyCapacity - energy
        if(remaining <= 0) return
        for (charger in getChargerNodes()) {
            if(remaining <= 0) break
            val amount = charger.withdrawEnergy(remaining)
            val given = giveEnergy(amount)
            remaining -= given
            if(given < amount) {
                val delta = amount - given // amount lost while given back
                if(charger.giveEnergy(delta) < delta) {
                    NeoComputers.LOGGER.warn("LOSING ENERGY! Tried giving $delta back to $charger and we're losing our marbles!")
                }
            }
        }
    }

    // only call if storage
    fun balanceStorage() {
        for(battery in getReachable()) {
            if(battery.powerRole != PowerRole.STORAGE) continue
            // its so if for example we have a battery with 2x the capacity
            // we don't try to even the energy between them since that's just bad
            // and might pointless delete energy over time
            val capacityRatio = energyCapacity.toDouble() / battery.energyCapacity

            val meaningfulSurplus = (battery.energy * capacityRatio - energy).toLong()

            if(meaningfulSurplus <= 0) {
                // WE'RE greedy (or negligible surplus)? Do nothing
                continue
            }

            // steal from this greedy mf
            val toSteal = meaningfulSurplus / 2
            if(toSteal == 0L) continue // broke storahh

            val stolen = battery.withdrawEnergy(toSteal)
            if(giveEnergy(stolen) < stolen) {
                NeoComputers.LOGGER.warn("LOSING ENERGY IN NODE $address!!!! THIS IS REALLY BAD!!!")
            }
        }
    }

    // rob the generators
    fun stealGeneratorPower() {
        var remaining = energyCapacity - energy

        for(generator in getReachable()) {
            if(generator.powerRole != PowerRole.GENERATOR) continue
            // rob this mf
            val robbed = generator.withdrawEnergy(remaining)
            val taken = giveEnergy(robbed)
            if(taken < robbed) {
                NeoComputers.LOGGER.warn("energy caught being DELETED in the big 26")
            }
            remaining -= taken
        }
    }

    open fun tick() {
        if(powerRole == PowerRole.CONSUMER) tryToChargeFully()
        if(powerRole == PowerRole.STORAGE) {
            stealGeneratorPower()
            balanceStorage()
        }
    }
    // processes a received message
    open fun received(message: Message) {}

    // called when a new direct connection is made
    open fun onConnect(deviceNode: DeviceNode) {}
    // called when a direct connection is lost
    open fun onDisconnect(deviceNode: DeviceNode) {}

    // called when a new node is added globally
    open fun onNodeAdded(deviceNode: DeviceNode) {
        invalidateReachableCache()
    }

    // called when a node is removed globally
    open fun onNodeRemoved(deviceNode: DeviceNode) {
        invalidateReachableCache()
    }

    fun getReachable(): Set<DeviceNode> {
        if(reachableCache == null) {
            reachableCache = computeReachable()
        }
        return reachableCache!!
    }

    open fun invalidateReachableCache() {
        reachableCache = null
    }

    // Returns a subset of connections, for a subset of direct
    // meant for things like drives which dont want to accidentally fuse networks
    open fun getPreferredFew() = setOf<DeviceNode>()

    fun computeReachable(): Set<DeviceNode> {
        if(reachability == Visibility.NONE) {
            return setOf()
        }
        if(reachability == Visibility.SOME) {
            return getPreferredFew()
        }
        if(reachability == Visibility.DIRECT) {
            return connections.minus(this)
        }
        if(reachability == Visibility.NETWORK) {
            // absolute cinema
            val working = HashSet<DeviceNode>()
            val pending = mutableListOf(this)
            var iterCount = 0
            while(iterCount < maxHopCount && pending.isNotEmpty()) {
                iterCount++
                val subnode = pending.removeFirst()
                if(subnode in working) continue
                working.add(subnode)
                if(subnode.reachability == Visibility.NETWORK) {
                    pending.addAll(subnode.connections)
                } else if(subnode.reachability == Visibility.DIRECT) {
                    working.addAll(subnode.connections)
                } else if(subnode.reachability == Visibility.SOME) {
                    pending.addAll(subnode.getPreferredFew())
                }
            }
            // cannot send to itself!
            working.remove(this)
            return working
        }
        throw NotImplementedError("visibility not implemented")
    }

    fun connectTo(other: DeviceNode) {
        this.directConnectTo(other)
        other.directConnectTo(this)
    }

    fun disconnectFrom(other: DeviceNode) {
        this.directDisconnectFrom(other)
        other.directDisconnectFrom(this)
    }

    fun directConnectTo(other: DeviceNode) {
        if(other == this) return
        if(other in connections) return
        connections.add(other)
        this.onConnect(other)
        invalidateReachableCache()
    }

    fun directDisconnectFrom(other: DeviceNode) {
        if(other !in connections) return
        connections.remove(other)
        this.onDisconnect(other)
        invalidateReachableCache()
    }

    // Network synchronization with the NodeSynchronizer
    // TODO: process shi

    var outOfSync = false
    fun markChanged() {
        outOfSync = true
    }

    open fun encodeScreenData(player: ServerPlayer, buf: FriendlyByteBuf) {}
    open fun processScreenInteraction(player: ServerPlayer, buf: FriendlyByteBuf) {}

    // Meant to write the entire state as a single commit
    // for clients which say they have no fucking idea what the server is storing
    open fun writeFullStateCommit(buf: FriendlyByteBuf) {}

    // client-side, meant to bring state forward
    open fun processCommit(buf: FriendlyByteBuf) {}

    open fun getComponent(): NNComponent? = null
}

// Used by the relay
// If the ComponentItem in the card slot
interface ConventionalNetworkDevice {
    fun sendClassicPacket(packet: Networking.ClassicPacket)
}

abstract class WirelessEndpoint(address: UUID?) : DeviceNode(address) {

    abstract fun getEndpointRange(): Double
    abstract fun getEndpointDimension(): Int
    abstract fun getEndpointLevel(): Level
    abstract fun getEndpointPosition(): Vec3
    // separate from process for simplicity
    abstract fun receiveWireless(message: Message, emitter: WirelessEndpoint)
}