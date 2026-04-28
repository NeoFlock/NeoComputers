package org.neoflock.neocomputers.entity

import net.minecraft.core.BlockPos
import net.minecraft.locale.Language
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import org.neoflock.neocomputers.NeoComputers
import org.neoflock.neocomputers.block.SingleDeviceBlockEntity
import org.neoflock.neocomputers.gui.buffer.BufferRenderer
import org.neoflock.neocomputers.network.DeviceNode
import org.neoflock.neocomputers.network.Networking
import org.neoflock.neocomputers.utils.GPUChar
import org.neoflock.neocomputers.utils.TextBuffer
import kotlin.text.ifEmpty

class ScreenEntity(blockPos: BlockPos, blockState: BlockState) :
    SingleDeviceBlockEntity(BlockEntities.SCREEN_ENTITY.get(), blockPos, blockState) {

    var lastError: String? = null
    var isOn: Boolean = false

    override val deviceNode = object : DeviceNode() {
        override fun received(message: Networking.Message) {
            super.received(message)
            if(message is Networking.ComputerEvent) {
                val mEnv = message.machineEvent
                NeoComputers.LOGGER.info("Got message $mEnv!")
                if(mEnv is MachinePowerEvent) {
                    if(mEnv.nowRunning) {
                        lastError = null
                        textBuf.fill(0, 0, textBuf.width, textBuf.height, GPUChar(' '))
                        textBuf.set(0, 0, address.toString())
                    }
                    isOn = mEnv.nowRunning
                    markChanged()
                }
                if(mEnv is MachineCrashEvent) {
                    lastError = mEnv.error
                    markChanged()
                }
            }
        }

        override fun encodeScreenData(player: ServerPlayer, buf: FriendlyByteBuf) {
            super.encodeScreenData(player, buf)
            textBuf.encodeContents(buf)
        }

        override fun writeFullStateCommit(buf: FriendlyByteBuf) {
            super.writeFullStateCommit(buf)
            buf.writeUUID(address)
            buf.writeBoolean(isOn)
            buf.writeUtf(lastError ?: "")
            textBuf.encodeContents(buf)
        }

        override fun processCommit(buf: FriendlyByteBuf) {
            super.processCommit(buf)
            if(Networking.changeNodeAddress(this, buf.readUUID())) createScreenTexture()
            isOn = buf.readBoolean()
            lastError = buf.readUtf().ifEmpty { null }
            textBuf.decodeContents(buf)
        }
    }
    var bound = "screen/unbound"

    val textBuf = TextBuffer(50, 16)

    private var cleanRenderer: () -> Unit = { } // TODO: THIS SUCKS, FIND A BETTER WAY

    override fun tickDevice(level: Level) {
        super.tickDevice(level)
        cleanRenderer()
        createScreenTexture()
    }

    override fun setRemoved() {
        super.setRemoved()
        bound = "screen/unbound" // ensure no missing texture is displayed
        cleanRenderer()
    }

    private fun createScreenTexture() {
        bound = "screen/"+deviceNode.address.toString().replace("-", "_")
        if (level!!.isClientSide) {
            if(lastError == null) {
                if(!isOn) {
                    textBuf.fill(0, 0, textBuf.width, textBuf.height)
                }
                val renderer = BufferRenderer(ResourceLocation.fromNamespaceAndPath(NeoComputers.MODID, bound), textBuf)
                renderer.drawBuffer()
                cleanRenderer = { renderer.clean() }
            } else {
                var trueError = lastError!!
                if(trueError.startsWith("@")) {
                    val trans = trueError.substring(1)
                    val lang = Language.getInstance()
                    trueError = lang.getOrDefault("neocomputers.computer.errorNoMsg", "Error: ") + lang.getOrDefault(trans)
                }
                val throwAwayBuf = TextBuffer(50, 16)
                val fg = 0xFFFFFF
                val bg = 0x2B68A6
                throwAwayBuf.fill(0, 0, throwAwayBuf.width, throwAwayBuf.height, GPUChar(' ', fg, bg))
                throwAwayBuf.set((throwAwayBuf.width - trueError.length) / 2, throwAwayBuf.height/2, trueError, fg, bg)
                val renderer = BufferRenderer(ResourceLocation.fromNamespaceAndPath(NeoComputers.MODID, bound), throwAwayBuf)
                renderer.drawBuffer()
                cleanRenderer = { renderer.clean() }
            }
        }
    }
}