# Networking
> All that is left is optimization

## Copy the networking optimizations of OC
> https://github.com/MightyPirates/OpenComputers/blob/master-MC1.7.10/src/main/scala/li/cil/oc/server/network/Network.scala

We should implement this, as it not only makes behavior match better, but also optimizes
it a lot. We do need to replace the NEIGHBORS rule with implementing the SOME visibility.
A direct port is not needed, just taking heavy inspiration from OC.

## Optimize power balancing

Use a smarter algorithm to prevent N storage nodes from iterating N^2 nodes each tick
to balance power.

# Computation
> Pretty important for a computer mod

## JNI

We need the JNI system so we can salvage our hard labor thrown into NeoNucleus.
Also because it is a capable engine and has a good API for architectures, and NCL is very capable.

## Worker threads

Computers need worker threads for running non-synchronized code, because otherwise we're cooked

## Entities as machines

Aside from blocks like cases and robots, we should also support entities like drones.
Not only for OC parity, but also as addons would def love that.