package org.neoflock.neocomputers.network

import net.minecraft.core.BlockPos
import org.neoflock.neocomputers.NeoComputers
import java.util.UUID
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

enum class PowerRole {
    // consumes energy, wants to be fully charged
    // does not give energy to network nodes
    CONSUMER,
    // stores energy, will not care to charge itself
    // will happily give energy to network nodes
    STORAGE,
    // only produces energy, thus obviously charges itself
    // also happily gives energy
    GENERATOR,
}

object Networking {
    // maximum amount of hops between nodes
    var maxHopCount = 32
    var tickCount = 0

    enum class Visibility {
        // only it can see itself
        NONE,
        // can only see its direct connections
        DIRECT,
        // Can see everything network-wide
        NETWORK,
    }


    abstract class Message(val sender: Node)

    class ClassicPacket(sender: Node, val src: String, val dst: String, val port: Int, val data: List<Any>, val hopCount: Int) : Message(sender) {
        fun hop() = ClassicPacket(sender, src, dst, port, data, hopCount + 1);
    }

    // for plugins and shi
    class ComputerCheckedSignal(sender: Node, val player: String?, val name: String, val data: Array<Any>): Message(sender)
    class ComputerUncheckedSignal(sender: Node, val name: String, val data: Array<Any>): Message(sender)

    open class Node(_address: UUID? = null) {
        val connections = mutableSetOf<Node>()
        private var reachableCache: Set<Node>? = null
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
            return maximum
        }
        // take energy out, returns how much was actually taken
        // cannot exceed amount specified
        open fun withdrawEnergy(amount: Long): Long {
            val maximum = min(amount, energy)
            energy -= maximum
            return maximum
        }

        fun getChargerNodes(): Set<Node> = getReachable().filter { it.powerRole != PowerRole.CONSUMER }.toSet()
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
                    NeoComputers.LOGGER.warn("LOSING ENERGY IN NODE $this!!!! THIS IS REALLY BAD!!!")
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
        open fun onConnect(node: Node) {}
        // called when a direct connection is lost
        open fun onDisconnect(node: Node) {}

        // called when a new node is added globally
        open fun onNodeAdded(node: Node) {
            reachableCache = null;
        }

        // called when a node is removed globally
        open fun onNodeRemoved(node: Node) {
            reachableCache = null;
        }

        fun getReachable(): Set<Node> {
            if(reachableCache == null) {
                reachableCache = computeReachable();
            }
            return reachableCache!!;
        }

        fun invalidateReachableCache() {
            reachableCache = null
        }

        fun computeReachable(): Set<Node> {
            if(reachability == Visibility.NONE) {
                return setOf();
            }
            if(reachability == Visibility.DIRECT) {
                return connections.minus(this);
            }
            if(reachability == Visibility.NETWORK) {
                // absolute cinema
                val working = mutableSetOf<Node>();
                val pending = mutableListOf(this);
                var iterCount = 0;
                while(iterCount < maxHopCount && pending.isNotEmpty()) {
                    iterCount++;
                    val subnode = pending.removeFirst();
                    if(subnode in working) continue;
                    working.add(subnode);
                    pending.addAll(subnode.connections);
                }
                // cannot send to itself!
                working.remove(this);
                return working;
            }
            throw NotImplementedError("visibility not implemented");
        }

        fun connectTo(other: Node) {
            this.directConnectTo(other);
            other.directConnectTo(this);
        }

        fun disconnectFrom(other: Node) {
            this.directDisconnectFrom(other);
            other.directDisconnectFrom(this);
        }

        fun directConnectTo(other: Node) {
            if(other == this) return;
            if(other in connections) return;
            connections.add(other);
            this.onConnect(other);
        }

        fun directDisconnectFrom(other: Node) {
            if(other !in connections) return;
            connections.remove(other);
            this.onDisconnect(other);
        }
    }

    abstract class WirelessEndpoint(address: UUID?) : Node(address) {

        abstract fun getRange(): Double
        abstract fun getDimension(): Int
        abstract fun getPosition(): BlockPos
        // separate from process for simplicity
        abstract fun receiveWireless(message: Message, emitter: WirelessEndpoint)
    }

    val wirelessNodes = mutableSetOf<WirelessEndpoint>()
    val allNodes = mutableMapOf<UUID, Node>()

    // node may differ from message.sender in the case of relays,
    // as they might have DIRECT reachability but
    fun emitMessage(node: Node, message: Message) {
        node.getReachable().forEach { it.received(message) }
    }

    fun computeRangeAllowedByHardness(src: BlockPos, dst: BlockPos): Double {
        return Double.POSITIVE_INFINITY // TODO: math
    }

    fun distanceBetween(a: BlockPos, b: BlockPos): Double {
        return sqrt((a.x - b.x + a.y - b.y + a.z - b.z).toDouble().pow(2.0));
    }

    fun emitWirelessMessage(starter: WirelessEndpoint, range: Double, message: Message) {
        val startPos = starter.getPosition();
        val startDim = starter.getDimension();
        val range = starter.getRange();
        wirelessNodes.forEach {
            if(it.getDimension() != startDim) return;
            val pos = it.getPosition();
            val d = distanceBetween(startPos, pos);
            var trueRange = min(it.getRange(), range);
            trueRange = min(trueRange, computeRangeAllowedByHardness(startPos, pos));
            if(d > trueRange) return;
            it.receiveWireless(message, starter);
        }
    }

    fun tickAllNodes() {
        allNodes.forEach { it.value.tick() }
        tickCount++
    }

    fun getNode(address: UUID): Node? = allNodes[address]

    // TODO: use setter, more convenient
    fun changeNodeAddress(node: Node, address: UUID) {
        allNodes.remove(node.address)
        node.address = address
        allNodes[address] = node
    }

    fun addNode(node: Node) {
        if(node.address in allNodes) return;
        allNodes[node.address] = node
        if(node is WirelessEndpoint) {
            wirelessNodes.add(node);
        }
        // notify at the end so it is notified of its own creation
        allNodes.forEach { it.value.onNodeAdded(node) }
    }

    fun addNodes(vararg nodes: Node) {
        nodes.forEach { addNode(it) }
    }

    fun removeNode(node: Node) {
        if(node.address !in allNodes) return
        allNodes.forEach { it.value.onNodeRemoved(node) }
        // toList() in order to copy it
        node.connections.toList().forEach {
            node.disconnectFrom(it)
        }
        // actually remove at the end so it can listen to its own removal
        allNodes.remove(node.address)
        if(node is WirelessEndpoint) {
            wirelessNodes.remove(node);
        }
    }

    fun removeNodes(vararg nodes: Node) {
        nodes.forEach { removeNode(it) }
    }

    val channels = mutableMapOf<String, MutableSet<Node>>();

    fun addToChannel(channel: String, node: Node) {
        if(!channels.containsKey(channel)) {
            channels[channel] = mutableSetOf();
        }
        channels[channel]!!.add(node);
    }

    fun removeFromChannel(channel: String, node: Node) {
        if(!channels.containsKey(channel)) return;
        channels[channel]?.remove(node);
        if(channels[channel].isNullOrEmpty()) {
            channels.remove(channel);
        }
    }

    fun emitChannelMessage(starter: Node, channel: String, message: Message) {
        val c = channels[channel] ?: return;
        c.forEach { if(it != starter) it.received(message); };
    }
}