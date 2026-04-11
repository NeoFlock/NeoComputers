package org.neoflock.neocomputers.util

fun interface TieredSupplier<T> {
    fun get(tier: Int): T
}