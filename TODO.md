# Networking
> Pretty important here

## Auto-connect

Rework the auto-connect system of device blocks to be more stable.
Prob re-scan the network continuously

## Synchronization

Move the state logic into the node, as it was meant to be.
Note, addresses on the client are allowed to be complete bullshit at least for a bit of time.
`ComponentItem`s should no longer allow a node on the client (at least for now), it serves no
purpose currently, and also is not designed well.

## Get rid of NodeBlockEntity

It is basically throw-away stitched together garbage.
Replace it with a new abstract class, which allows different nodes on different sides,
handles synchronizing those nodes according to their state changes.

Also implement an equivalent for LivingEntities, like drones or other addon stuff.

## Optimizations

Optimize the networking, both synchronization using smaller encodings,
and emitting messages or adding/removing nodes. The goal is to have as little of
the CPU time on the server thread taken by NC as we can.

# Computation
> Pretty important for a computer mod

## JNI

We need the JNI system so we can salvage our hard labor thrown into NeoNucleus.
Also because it is a capable engine and has a good API for architectures, and NCL is very capable.

## Worker threads

Computers need worker threads for running non-synchronized code, because otherwise we're cooked