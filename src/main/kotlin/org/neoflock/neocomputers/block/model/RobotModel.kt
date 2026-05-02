package org.neoflock.neocomputers.block.model

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.ItemOverrides
import net.minecraft.client.renderer.block.model.ItemTransforms
import net.minecraft.client.renderer.texture.TextureAtlas
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.resources.model.BakedModel
import net.minecraft.client.resources.model.Material
import net.minecraft.client.resources.model.ModelBaker
import net.minecraft.client.resources.model.ModelState
import net.minecraft.client.resources.model.UnbakedModel
import net.minecraft.core.Direction
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.RandomSource
import net.minecraft.util.ResourceLocationPattern
import net.minecraft.world.level.block.state.BlockState
import org.neoflock.neocomputers.NeoComputers
import java.util.function.Function

class RobotModel() : AbstractModel() {
    val size = 0.4f
    val l = 0.5f-size;
    val h = 0.5f+size;

    // TODO: fix dimensions (i eyeballed it)
    override fun bake(atlas: (Material) -> TextureAtlasSprite) {
//    override fun bake(atlas: Function<Material?, TextureAtlasSprite?>, state: ModelState): BakedModel {
        val sprite = atlas(Material(TextureAtlas.LOCATION_BLOCKS, ResourceLocation.fromNamespaceAndPath(NeoComputers.MODID, "block/robot")))
//        val sprite = atlas.apply(Material(TextureAtlas.LOCATION_BLOCKS, ResourceLocation.fromNamespaceAndPath(NeoComputers.MODID, "robot")))!!
        var verts: SchizoConsumer = SchizoConsumer()

        // top pyramid, enjoy reading this schizo mess
        bakeTri(verts, Direction.WEST, sprite, l,l, l,  h, 9f/16f,1f, 0f, 0f, 0f, 16f/32f)
        bakeTri(verts, Direction.EAST, sprite, h, h, h,  l, 9f/16f, 1f, 16/32f, 16/32f, 16/32f, 0f)
        bakeTri(verts, Direction.SOUTH, sprite, l,  h, h, h, 9f/16f, 1f, 0f, 16f/32f, 16f/32f, 16f/32f)
        bakeTri(verts, Direction.NORTH, sprite, h,  l, l,l,  9f/16f, 1f, 16/32f, 0f, 0f, 0f)

        verts.startQuad(Direction.DOWN, sprite, 0)
        verts.vertex(l, 9f/16F, h, 0xFFFFFFFF.toInt(), 16f/32f, 16/32F)
        verts.vertex(l, 9f/16f, l, 0xFFFFFFFF.toInt(), 0F, 16/32F)
        verts.vertex(h, 9f/16f, l, 0xFFFFFFFF.toInt(), 0F, 1F)
        verts.vertex(h, 9f/16f, h, 0xFFFFFFFF.toInt(), 16/32F, 1F)

        // bottom
        bakeTri(verts, Direction.WEST, sprite, l, h, l, l, 8f/16f, 1/16f, 0f, 0f, 0f, 16f/32f)
        bakeTri(verts, Direction.EAST, sprite, h, l, h,  h, 8f/16f, 1/16f, 0f, 0f, 0f, 16f/32f)
        bakeTri(verts, Direction.SOUTH, sprite, h,  h, l, h, 8f/16f, 1/16f, 0f, 0f, 0f, 16f/32f)
        bakeTri(verts, Direction.NORTH, sprite, l,  l, h,l,  8f/16f, 1/16f, 0f, 0f, 0f, 16f/32f)

        verts.startQuad(Direction.UP, sprite, 0)
        verts.vertex(h, 8f/16F, l, 0xFFFFFFFF.toInt(), 16/32F, 16/32F)
        verts.vertex(l, 8f/16f, l, 0xFFFFFFFF.toInt(), 0F, 16/32F)
        verts.vertex(l, 8f/16f, h, 0xFFFFFFFF.toInt(), 0F, 1F)
        verts.vertex(h, 8f/16f, h, 0xFFFFFFFF.toInt(), 16/32F, 1F)


        this.mesh = verts.mesh
    }

    fun bakeTri(verts: SchizoConsumer, normal: Direction, sprite: TextureAtlasSprite, lx: Float,  lz: Float, rx: Float,  rz: Float, dy: Float, uy: Float, lu: Float, lv: Float, ru: Float, rv: Float) {
        verts.startQuad(normal, sprite, 0)
        verts.vertex(0.5F, uy, 0.5F, 0xFFFFFFFF.toInt(), 8F/32F, 8F/32F)
        verts.vertex(0.5F, uy, 0.5F, 0xFFFFFFFF.toInt(), 8F/32F, 8F/32F)
        verts.vertex(lx, dy, lz, 0xFFFFFFFF.toInt(), lu, lv)
        verts.vertex(rx, dy, rz, 0xFFFFFFFF.toInt(), ru, rv)
    }

    override fun particle(): ResourceLocation = ResourceLocation.fromNamespaceAndPath(NeoComputers.MODID, "block/teto")

    override fun useAmbientOcclusion(): Boolean = true

    override fun isGui3d(): Boolean = true

    override fun usesBlockLight(): Boolean = true

    override fun isCustomRenderer(): Boolean = false

    override fun getTransforms(): ItemTransforms? = ItemTransforms.NO_TRANSFORMS

    override fun getOverrides(): ItemOverrides? = ItemOverrides.EMPTY // TODO: item
}