package org.neoflock.neocomputers.entity;

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import org.neoflock.neocomputers.network.Networking

open class NodeEntity(blockEntityType: BlockEntityType<*>, blockPos: BlockPos, blockState: BlockState) :
    BlockEntity(blockEntityType, blockPos, blockState) {

    // stuff
    open fun getNode(): Networking.Node? = null

    open fun getSubnodes(): List<Networking.Node> = listOf()

    fun initNetworking() {
        val node = getNode()
        if(node != null) Networking.addNode(node)
        getSubnodes().forEach { Networking.addNode(it) }
        syncReachable()
    }

    open fun getDirectConnections(): List<NodeEntity> {
        if(level == null) return listOf();
        val offs = listOf(
            BlockPos(0, 1, 0),
            BlockPos(0, -1, 0),
            BlockPos(1, 0, 0),
            BlockPos(-1, 0, 0),
            BlockPos(0, 0, 1),
            BlockPos(0, 0, -1),
        )
        val entities = mutableListOf<NodeEntity>()
        offs.forEach {
            val ent = level?.getBlockEntity(blockPos.offset(it.x, it.y, it.z))
            if(ent is NodeEntity) {
                entities.add(ent)
            }
        }
        return entities
    }

    // may include itself
    fun getReachableNodes(): Set<Networking.Node> {
        val visited = mutableSetOf<NodeEntity>()
        val working = mutableListOf<NodeEntity>(this)
        val nodes = mutableSetOf<Networking.Node>()

        while(working.isNotEmpty()) {
            val cur = working.removeFirst()
            if(cur in visited) continue
            visited.add(cur)
            val n = cur.getNode()
            if(n != null) {
                // rely on the defined direct connections of the node
                nodes.add(n)
                if(n != this.getNode()) continue
            }
            working.addAll(cur.getDirectConnections());
        }

        return nodes
    }

    fun syncReachable() {
        val reachable = getReachableNodes().toList()
        val node = getNode()
        // nothing to sync
        if(node == null) return

        reachable.filter {
            it !in node.connections
        }.forEach {
            node.connectTo(it)
        }

        node.connections.filter { it !in reachable }.forEach {
            node.disconnectFrom(it)
        }
    }

    override fun setChanged() {
        super.setChanged()
        syncReachable()
    }

    override fun setRemoved() {
        super.setRemoved()
        syncReachable()
        val n = getNode()
        if(n != null) Networking.removeNode(n)
        getSubnodes().forEach { Networking.removeNode(it) }
    }
}