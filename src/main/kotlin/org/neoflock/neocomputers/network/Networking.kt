package org.neoflock.neocomputers.network

import net.minecraft.core.BlockPos
import org.neoflock.neocomputers.NeoComputers
import java.lang.ref.WeakReference
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

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
        val reachability = Visibility.NETWORK
        var reachableCache: Set<Node>? = null

        open fun tick() {}
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

        fun computeReachable(): Set<Node> {
            if(reachability == Visibility.NONE) {
                return setOf();
            }
            if(reachability == Visibility.DIRECT) {
                return connections;
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

        fun disconnectTo(other: Node) {
            this.directDisconnectFrom(other);
            other.directDisconnectFrom(this);
        }

        fun directConnectTo(other: Node) {
            if(other in connections) return;
            connections.add(other);
            onConnect(other);
        }

        fun directDisconnectFrom(other: Node) {
            if(other !in connections) return;
            connections.remove(other);
            onDisconnect(other);
        }
    }

    class LoggerNode(val label: String): Node() {
        override fun received(message: Message) {
            NeoComputers.LOGGER.info("$label: ${message.javaClass.name} message");
            super.received(message)
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

    fun removeNode(node: Node) {
        if(node !in allNodes) return;
        allNodes.remove(node);
        if(node is WirelessEndpoint) {
            wirelessNodes.remove(node);
        }
        allNodes.forEach { it.onNodeRemoved(node) }
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