package org.neoflock.neocomputers.network

import net.minecraft.core.BlockPos
import org.neoflock.neocomputers.NeoComputers
import java.lang.ref.WeakReference
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

enum class PowerRole {
    // consumes energy, wants to be fully charged
    // does not give energy to network nodes
    CONSUMER,
    // produces/stores energy, will not care to charge itself
    // will happily give energy to network nodes
    PRODUCER,
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

    open class Node {
        val connections = mutableSetOf<Node>()
        private var reachableCache: Set<Node>? = null

        open fun getReachability() = Visibility.NETWORK
        open fun getPowerRole() = PowerRole.CONSUMER
        open fun getEnergy(): Long = 0
        // give energy, returns how much was actually given
        // cannot exceed amount specified
        open fun giveEnergy(amount: Long): Long = 0
        // take energy out, returns how much was actually taken
        // cannot exceed amount specified
        open fun withdrawEnergy(amount: Long): Long = 0

        open fun getEnergyCapacity(): Long = 0
        fun getChargerNodes(): Set<Node> = getReachable().filter { it.getPowerRole() == PowerRole.PRODUCER }.toSet()
        fun totalEnergyInConnections(): Long = getChargerNodes().fold(0) { acc, node -> acc + node.getEnergy() }
        fun maxEnergyInConnections(): Long = getChargerNodes().fold(0) { acc, node -> acc + node.getEnergyCapacity() }

        // attempts to consume
        fun consumeEnergy(energy: Long): Boolean {
            // consumes energy, returns false if not enough
            val total = totalEnergyInConnections() + getEnergy()
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

        fun tryToChargeFully() {
            var remaining = getEnergyCapacity() - getEnergy()
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

        open fun tick() {
            if(getPowerRole() == PowerRole.CONSUMER) tryToChargeFully()
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
            val reachability = getReachability()
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

    abstract class WirelessEndpoint : Node {

        constructor(position: BlockPos);

        abstract fun getRange(): Double
        abstract fun getDimension(): Int
        abstract fun getPosition(): BlockPos
        // separate from process for simplicity
        abstract fun receiveWireless(message: Message, emitter: WirelessEndpoint)
    }

    val wirelessNodes = mutableSetOf<WirelessEndpoint>()
    val allNodes = mutableSetOf<Node>()

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
        allNodes.forEach { it.tick() }
        tickCount++
    }

    fun addNode(node: Node) {
        if(node in allNodes) return;
        allNodes.forEach { it.onNodeAdded(node) }
        allNodes.add(node);
        if(node is WirelessEndpoint) {
            wirelessNodes.add(node);
        }
    }

    fun addNodes(vararg nodes: Node) {
        nodes.forEach { addNode(it) }
    }

    fun removeNode(node: Node) {
        if(node !in allNodes) return;
        allNodes.remove(node);
        if(node is WirelessEndpoint) {
            wirelessNodes.remove(node);
        }
        // toList() in order to copy it
        node.connections.toList().forEach {
            node.disconnectFrom(it)
        }
        allNodes.forEach { it.onNodeRemoved(node) }
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