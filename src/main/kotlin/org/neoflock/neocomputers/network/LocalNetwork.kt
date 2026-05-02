package org.neoflock.neocomputers.network

class LocalNetwork(val sourceOfTruth: DeviceNode) {
    val devices = HashSet<DeviceNode>()

    fun getReachableByTruth() = sourceOfTruth.computeReachable()

    fun performMerge(other: LocalNetwork) {
        for(dev in other.devices) {
            if(other in dev.boundNetworks) {
                dev.boundNetworks.remove(other)
                dev.boundNetworks.add(this)
            }
            devices.add(dev)
        }
        other.devices.clear()
    }

    fun addToNetwork(dev: DeviceNode) {
        if(dev in devices) return
        if(dev.reachability == Networking.Visibility.NETWORK) {
            val primNet = dev.getPrimaryNetwork()
            if(primNet != null) {
                // Merge the networks!
                // merge smallest into largest for perf
                if(primNet.devices.size > devices.size) {
                    primNet.performMerge(this)
                } else {
                    performMerge(primNet)
                }
                return
            }
        }

        devices.add(dev)
        dev.boundNetworks.add(this)

        when(dev.reachability) {
            Networking.Visibility.NONE -> return
            Networking.Visibility.SOME -> {
                dev.getPreferredFew().forEach { addToNetwork(it) }
            }
            Networking.Visibility.NETWORK -> {
                dev.connections.forEach { addToNetwork(it) }
            }
        }
    }

    fun checkIfStillConnected(dev: DeviceNode) {
        if(dev !in devices) return
        if(dev in getReachableByTruth()) return
        removeFromNetwork(dev)
    }

    fun removeFromNetwork(dev: DeviceNode) {
        // TODO: this is wrong, rewrite it. We need it to make a sub-network.
        if(dev == sourceOfTruth) {
            throw UnsupportedOperationException("removing the source of truth is not supported")
        }
        if(dev !in devices) return
        devices.remove(dev)
        dev.boundNetworks.remove(this)

        when(dev.reachability) {
            Networking.Visibility.NONE -> {}
            Networking.Visibility.SOME -> dev.getPreferredFew().forEach { removeFromNetwork(it) }
            Networking.Visibility.NETWORK -> dev.connections.forEach { removeFromNetwork(it) }
        }
    }
}