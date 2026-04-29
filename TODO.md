# Networking
> All that is left is optimization

## ERADICATE DIRECT

We only need NONE, SOME and NETWORK.

## Optimize compute and memory
> OC does this too

Optimize both *time* and *memory* using graph theory.

### Requirements

It obviously must be fast and memory-efficient, and respect the current semantics.

The current idea is to make NETWORK style nodes the only ones with a cache,
and to instead appoint a connection that is also a NETWORK node, if any, as
the one source of truth, or steal the source. This means only one node
in a local network gets to actually compute the graph layout.
Complications can happen when merges happen, the idea is to pick one source of truth
then as well.

Also, `onNodeAdded` and `onNodeRemoved` should also be called when a reachable node is added/removed.
This is for perf. We only care about nodes being added/removed then.
This can be done by broadcasting to all of their *connections*, to circumvent the NONE/SOME/NETWORK asymmetric reachability sets.

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