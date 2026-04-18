// package org.neoflock.neocomputers.gui.render;

// import com.mojang.blaze3d.platform.NativeImage
// import com.mojang.blaze3d.systems.RenderSystem
// import com.mojang.blaze3d.vertex.BufferBuilder
// import com.mojang.blaze3d.vertex.DefaultVertexFormat
// import com.mojang.blaze3d.vertex.Tesselator
// import com.mojang.blaze3d.vertex.VertexFormat
// import net.minecraft.client.Minecraft
// import net.minecraft.client.gui.GuiGraphics
// import net.minecraft.client.renderer.texture.DynamicTexture
// import net.minecraft.resources.Identifier
// import org.neoflock.neocomputers.NeoComputers

// class ScreenRenderer { 
//     val BORDERS: Identifier = Identifier.fromNamespaceAndPath(NeoComputers.MODID, "textures/gui/borders.png")
//     var bound: Identifier = Identifier.fromNamespaceAndPath(NeoComputers.MODID, "screen/unbound")

//     val bordersize = 10

//     fun render(graphics: GuiGraphics, x: Int, y: Int, width: Int, height: Int) {
//         // RenderSystem.setShader(GameR)
//         // top left corner, uv (0, 0) to (6,6)
//         // graphics.blit(BORDERS, x-bordersize, y-bordersize, 0, 0, bordersize.toFloat(), bordersize.toFloat(), 6F, 6F)
//         graphics.blit(BORDERS, 6, 6, 0, 0, (x-bordersize).toFloat(), (y-bordersize).toFloat(), bordersize.toFloat(), bordersize.toFloat())
//         // top border, uv (7,0) to (8,6)
//         graphics.blit(BORDERS, 6, 1, 7, 0, x.toFloat(), (y-bordersize).toFloat(), width.toFloat(), bordersize.toFloat())
//         // graphics.blit(BORDERS, x, y-bordersize, 7, 0, width.toFloat(), bordersize.toFloat(), 1F, 6F)
//         // top right corner, uv (9,0) to (15, 6)
//         // graphics.blit(BORDERS, x+width, y-bordersize, 9, 0, bordersize.toFloat(), bordersize.toFloat(), 6F, 6F)
//         graphics.blit(BORDERS, 6, 6, 9, 0, (x+width).toFloat(), (y-bordersize).toFloat(), bordersize.toFloat(), bordersize.toFloat())

//         // left border, uv (0,7) to (6, 8)
//         graphics.blit(BORDERS, 6, 1, 0, 7, (x-bordersize).toFloat(), (y).toFloat(), bordersize.toFloat(), height.toFloat())
//         // graphics.blit(BORDERS, x-bordersize, y, 0, 7, bordersize.toFloat(), height.toFloat(), 6F, 1F)
//         // right border uv (9, 7) to (15, 8)
//         graphics.blit(BORDERS, 6, 1, 9, 7, (x+width).toFloat(), (y).toFloat(), bordersize.toFloat(), height.toFloat())
//         // graphics.blit(BORDERS, x+width, y, 9, 7, bordersize.toFloat(), height.toFloat(), 6F, 1F)

//         // bottom left corner, uv (0, 9) to (6, 15)
//         graphics.blit(BORDERS, 6, 6, 0, 9, (x-bordersize).toFloat(), (y+height).toFloat(), bordersize.toFloat(), bordersize.toFloat())
//         // graphics.blit(BORDERS, x-bordersize, y+height, 0, 9, bordersize.toFloat(), bordersize.toFloat(), 6F, 6F)
//         // bottom border, uv (7, 9) to (8, 15)
//         graphics.blit(BORDERS, 1, 6, 7, 9, (x).toFloat(), (y+height).toFloat(), width.toFloat(), bordersize.toFloat())
//         // graphics.blit(BORDERS, x, y+height, 7, 9, width.toFloat(), bordersize.toFloat(), 1F, 6F)
//         // bottom right corner, uv (9, 9) to (15, 15)
//         graphics.blit(BORDERS, 6, 6, 9, 9, (x+width).toFloat(), (y+height).toFloat(), bordersize.toFloat(), bordersize.toFloat())
//         // graphics.blit(BORDERS, x+width, y+height, 9, 9, bordersize.toFloat(), bordersize.toFloat(), 6F, 6F)

//         // texture
//         // TODO: min mag filter thing
//         graphics.blit(bound, 15, 15, 0, 0, x.toFloat(), y.toFloat(), width.toFloat(), height.toFloat())
//         // graphics.blit(bound, x, y, 0, 0, width.toFloat(), height.toFloat(), 15F, 15F) 

//     }

//     // fun drawQuad(graphics: GuiGraphics, x: Int, y: Int, width: Int, height: Int, u1: Int, u2: Int, v1: Int, v2: Int) {
//     //     var t: Tesselator = Tesselator.getInstance()
//     //     var builder: BufferBuilder = t.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX)
//     //     builder.setUv1(u1, v1)
//     //     builder.setUv2(u2, v2)

//     //     builder.addVertex(x.toFloat(), (y+height).toFloat(), 1f)
//     //     builder.addVertex((x+width).toFloat(), (y+height).toFloat(), 1f)
//     //     builder.addVertex((x+width).toFloat(), y.toFloat(), 1f)
//     //     builder.addVertex(x.toFloat(), y.toFloat(), 1f)

//     //     builder.build()
//     // }

//     fun bind(id: Identifier) {
//         bound = id
//     }

//     fun unbind() {
//         bound = Identifier.fromNamespaceAndPath(NeoComputers.MODID, "screen/unbound")
//     }

//     companion object Static {
//         val img: NativeImage = NativeImage(1, 1, false)
//         val tex: DynamicTexture = DynamicTexture({ "screen/unbound" }, img)

//         fun genUnboundTex() {
//             img.fillRect(0, 0, 1, 1, 0xFF000000.toInt())
//             tex.upload()

//             Minecraft.getInstance().textureManager.register(Identifier.fromNamespaceAndPath(NeoComputers.MODID, "screen/unbound"), tex)
//         }

//         fun cleanUnboundTex() {
//             img.close()
//             tex.close()
//             Minecraft.getInstance().textureManager.release(Identifier.fromNamespaceAndPath(NeoComputers.MODID, "screen/unbound"))
//         }
//     }
// }