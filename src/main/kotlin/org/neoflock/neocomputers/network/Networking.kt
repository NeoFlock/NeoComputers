package org.neoflock.neocomputers.network

import net.minecraft.core.BlockPos
import org.neoflock.neocomputers.entity.MachineEvent
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


    abstract class Message(val sender: DeviceNode)

    class ClassicPacket(sender: DeviceNode, val src: String, val dst: String, val port: Int, val data: List<Any>, val hopCount: Int) : Message(sender) {
        fun hop() = ClassicPacket(sender, src, dst, port, data, hopCount + 1);
    }

    // for plugins and shi
    class ComputerCheckedSignal(sender: DeviceNode, val player: String?, val name: String, val data: Array<Any>): Message(sender)
    class ComputerUncheckedSignal(sender: DeviceNode, val name: String, val data: Array<Any>): Message(sender)
    class ComputerEvent(sender: DeviceNode, val machineEvent: MachineEvent): Message(sender)

    val wirelessNodes = ThreadLocal.withInitial { HashSet<WirelessEndpoint>() }
    val allNodes = ThreadLocal.withInitial { HashMap<UUID, DeviceNode>() }

    // node may differ from message.sender in the case of relays,
    // as they might have DIRECT reachability but
    fun emitMessage(deviceNode: DeviceNode, message: Message) {
        deviceNode.getReachable().forEach { it.received(message) }
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
        wirelessNodes.get().forEach {
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
        allNodes.get().forEach { it.value.tick() }
        tickCount++
    }

    fun getNode(address: UUID): DeviceNode? = allNodes.get()[address]

    // TODO: use setter, more convenient
    fun changeNodeAddress(deviceNode: DeviceNode, address: UUID) {
        if(deviceNode.address.equals(address)) return
        if(deviceNode.address !in allNodes.get()) return
        allNodes.get().remove(deviceNode.address)
        deviceNode.address = address
        allNodes.get()[address] = deviceNode
    }

    fun addNode(deviceNode: DeviceNode) {
        if(deviceNode.address in allNodes.get()) return
        allNodes.get()[deviceNode.address] = deviceNode
        if(deviceNode is WirelessEndpoint) {
            wirelessNodes.get().add(deviceNode);
        }
        // notify at the end so it is notified of its own creation
        allNodes.get().forEach { it.value.onNodeAdded(deviceNode) }
    }

    fun addNodes(vararg deviceNodes: DeviceNode) {
        deviceNodes.forEach { addNode(it) }
    }

    fun removeNode(deviceNode: DeviceNode) {
        if(deviceNode.address !in allNodes.get()) return
        allNodes.get().forEach { it.value.onNodeRemoved(deviceNode) }
        // toList() in order to copy it
        deviceNode.connections.toList().forEach {
            deviceNode.disconnectFrom(it)
        }
        // actually remove at the end so it can listen to its own removal
        allNodes.get().remove(deviceNode.address)
        if(deviceNode is WirelessEndpoint) {
            wirelessNodes.get().remove(deviceNode);
        }
    }

    fun removeNodes(vararg deviceNodes: DeviceNode) {
        deviceNodes.forEach { removeNode(it) }
    }

    val channels = ThreadLocal.withInitial { HashMap<String, MutableSet<DeviceNode>>() }

    fun addToChannel(channel: String, deviceNode: DeviceNode) {
        val localChannels = channels.get()
        if(!localChannels.containsKey(channel)) {
            localChannels[channel] = mutableSetOf();
        }
        localChannels[channel]!!.add(deviceNode);
    }

    fun removeFromChannel(channel: String, deviceNode: DeviceNode) {
        val localChannels = channels.get()
        if(!localChannels.containsKey(channel)) return;
        localChannels[channel]?.remove(deviceNode);
        if(localChannels[channel].isNullOrEmpty()) {
            localChannels.remove(channel);
        }
    }

    fun emitChannelMessage(starter: DeviceNode, channel: String, message: Message) {
        val localChannels = channels.get()
        val c = localChannels[channel] ?: return;
        c.forEach { if(it != starter) it.received(message); };
    }
}