package org.neoflock.neocomputers.utils

object Formatting {
    fun formatMemory(size: Long, spacing: String = " "): String {
        var unit = 0
        val units = listOf("B", "KiB", "MiB", "GiB", "TiB", "PiB")
        var num = size.toDouble()
        while(unit < units.lastIndex && num >= 1024) {
            num /= 1024
            unit++
        }
        num = (num * 100).toInt().toDouble() / 100
        return "$num$spacing${units[unit]}"
    }
}