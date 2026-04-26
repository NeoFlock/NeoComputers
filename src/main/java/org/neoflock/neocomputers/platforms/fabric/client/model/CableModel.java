//package org.neoflock.neocomputers.platforms.fabric.client.model;
//
//import net.fabricmc.fabric.api.renderer.v1.Renderer;
//import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
//import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
//import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
//import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
//import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
//import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
//import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.renderer.block.model.BakedQuad;
//import net.minecraft.client.renderer.block.model.ItemOverrides;
//import net.minecraft.client.renderer.block.model.ItemTransforms;
//import net.minecraft.client.renderer.texture.TextureAtlas;
//import net.minecraft.client.renderer.texture.TextureAtlasSprite;
//import net.minecraft.client.resources.model.*;
//import net.minecraft.core.BlockPos;
//import net.minecraft.core.Direction;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.util.RandomSource;
//import net.minecraft.world.level.BlockAndTintGetter;
//import net.minecraft.world.level.block.state.BlockState;
//import org.jetbrains.annotations.Nullable;
//import org.neoflock.neocomputers.NeoComputers;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.List;
//import java.util.function.Function;
//import java.util.function.Supplier;
//
//// this totally could have been done with datagen, why do i do this
//public class CableModel implements BakedModel, UnbakedModel, FabricBakedModel {
////    private TextureAtlasSprite sprite = Minecraft
////    TextureAtlasSprite sprite = atlas.apply(new Material(TextureAtlas.LOCATION_BLOCKS, ResourceLocation.withDefaultNamespace("block/furnace_top")));
//
//    private float MIN = 6/16F;
//    private float MAX = 10/16F;
//
//    private BlockState state;
//
//    private Mesh mesh;
//    @Override
//    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction direction, RandomSource random) {
////        NeoComputers.INSTANCE.getLOGGER().info("Obtained blockstate!");
//        this.state = state;
//        return List.of();
//    }
//
//    @Override
//    public boolean useAmbientOcclusion() {
//        return false;
//    }
//
//    @Override
//    public boolean isGui3d() {
//        return true;
//    }
//
//    @Override
//    public boolean usesBlockLight() {
//        return true;
//    }
//
//    @Override
//    public boolean isCustomRenderer() {
//        return false;
//    }
//
//    @Override
//    public TextureAtlasSprite getParticleIcon() {
//        Function<ResourceLocation, TextureAtlasSprite> atlas = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS);
//        return atlas.apply(ResourceLocation.fromNamespaceAndPath(NeoComputers.MODID, "block/teto"));
//    }
//
//    @Override
//    public ItemTransforms getTransforms() {
//        return ItemTransforms.NO_TRANSFORMS;
//    }
//
//    @Override
//    public ItemOverrides getOverrides() {
//        return ItemOverrides.EMPTY;
//    }
//
//    @Override
//    public Collection<ResourceLocation> getDependencies() {
//        return List.of();
//    }
//
//    @Override
//    public void resolveParents(Function<ResourceLocation, UnbakedModel> resolver) {
//    }
//
//    @Override
//    public @Nullable BakedModel bake(ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState state) {
//        TextureAtlasSprite sprite = spriteGetter.apply(new Material(TextureAtlas.LOCATION_BLOCKS, ResourceLocation.fromNamespaceAndPath(NeoComputers.MODID, "block/teto")));
//
//        Renderer renderer = RendererAccess.INSTANCE.getRenderer();
//        MeshBuilder builder = renderer.meshBuilder();
//        QuadEmitter emitter = builder.getEmitter();
//
//        bakeCenter(emitter, sprite);
//
//
//        mesh = builder.build();
//        return this;
//    }
//
//    @Override
//    public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context) {
//        mesh.outputTo(context.getEmitter());
//    }
//
//    public void bakeCenter(QuadEmitter emitter, TextureAtlasSprite sprite) {
//        for (Direction dir : Direction.values()) {
//            emitter.square(dir, MIN, MIN, MAX, MAX, MIN);
//            emitter.spriteBake(sprite, MutableQuadView.BAKE_LOCK_UV);
//            emitter.color(-1, -1, -1, -1);
//            emitter.emit();
//        }
//    }
//    public void bakeConnection(Direction dir, QuadEmitter emitter, TextureAtlasSprite sprite) {
//        int mag = dir.getStepX()+dir.getStepZ(); // i dont want to hear it
//        float bottom = dir.getStepY()==0 ? 6/16F : (dir.getStepY()==1 ? 10/16F : 0F);
//
//        for (Direction d : dir.getAxis().getPlane().faces) {
//            emitter.square(d, (6/16F)+0.5F*mag, bottom, 6/16F)
//        }
//
//    }
//
////    @Override
////    public boolean isVanillaAdapter() {
////        return false; // TODO: let this be true so maybe forge and fabric can be unified
////    }
//}
