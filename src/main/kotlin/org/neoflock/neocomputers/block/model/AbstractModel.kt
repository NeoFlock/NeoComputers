package org.neoflock.neocomputers.block.model

import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.Minecraft
import net.minecraft.client.model.geom.ModelPart
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
import net.minecraft.world.level.block.state.BlockState
import org.neoflock.neocomputers.NeoComputers
import java.util.function.Function

abstract class AbstractModel : BakedModel {
    val atlas = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS)
    var baker: ModelBaker? = null;
    var mesh: Map<Direction, List<BakedQuad>> = mapOf(
        Direction.UP to listOf(),
        Direction.DOWN to listOf(),
        Direction.EAST to listOf(),
        Direction.WEST to listOf(),
        Direction.SOUTH to listOf(),
        Direction.NORTH to listOf(),
        );

    override fun getQuads(state: BlockState?, direction: Direction?, random: RandomSource): List<BakedQuad?>? { // perf? what's that?
        if (direction != null) {
            return mesh[direction]
        } else {
            var allquads: MutableList<BakedQuad> = mutableListOf()
            mesh.forEach { (_, quads) ->
                allquads.addAll(quads)
            }
            return allquads
        }
    }

    fun _bake(spriteGetter: Function<Material, TextureAtlasSprite>): BakedModel {
        bake { r: Material -> spriteGetter.apply(r) }
        return this
    }

    override fun getParticleIcon(): TextureAtlasSprite? {
        return atlas.apply(particle())
    }

    abstract fun bake(atlas: (Material) -> TextureAtlasSprite)

    abstract fun particle(): ResourceLocation

}

class SchizoConsumer {
    var mesh: Map<Direction, MutableList<BakedQuad>> = mapOf(
        Direction.UP to mutableListOf(),
        Direction.DOWN to mutableListOf(),
        Direction.EAST to mutableListOf(),
        Direction.WEST to mutableListOf(),
        Direction.SOUTH to mutableListOf(),
        Direction.NORTH to mutableListOf(),
    );

    var vertices: MutableList<Int> = mutableListOf();
    var sprite: TextureAtlasSprite? = null;
    var normal: Direction? = null;
    var tint_index = -1
    var shade = false

    fun startQuad(normal: Direction, sprite: TextureAtlasSprite, tint_index: Int = -1, shade: Boolean = false): SchizoConsumer {
        this.sprite = sprite
        this.normal = normal
        this.tint_index = tint_index
        this.shade = shade
        return this
    }

    fun vertex(x: Float, y: Float, z:Float, colorABGR: Int, u: Float, v: Float) { // uv are normalized
        if (sprite == null || normal == null) {
            throw Error("quad not started")
        }

//        NeoComputers.LOGGER.info("{} {} -> {} {} ", u, v, sprite!!.getU(u), sprite!!.getV(v))
        vertices.add(x.toBits())
        vertices.add(y.toBits())
        vertices.add(z.toBits())
        vertices.add(colorABGR)
        vertices.add(sprite!!.getU(u).toBits())
        vertices.add(sprite!!.getV(v).toBits())
        vertices.add(0)
        vertices.add(0)

        if (vertices.size == 4 * 8) {
            var quad = BakedQuad(vertices.toIntArray(), tint_index, normal!!, sprite!!, shade)
            mesh.get(normal)!!.add(quad)
            normal = null
            sprite = null
            vertices.clear()
        }
    } // TODO: add stuff like squares


}