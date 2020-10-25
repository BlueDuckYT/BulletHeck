package net.digitalphantasia.bulletheck.renderer;

import org.lwjgl.opengl.GL11;

import net.digitalphantasia.bulletheck.entity.EntityHeckOrb;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;

public class RenderHeckOrb extends Render<EntityHeckOrb>
{
    public RenderHeckOrb(RenderManager renderManager)
    {
        super(renderManager);
    }

    @Override
    public void doRender(EntityHeckOrb entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        TextureAtlasSprite sprite = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getParticleIcon(Items.FIRE_CHARGE);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        float minU = sprite.getMinU();
        float maxU = sprite.getMaxU();
        float minV = sprite.getMinV();
        float maxV = sprite.getMaxV();
        int brightness = entity.getBrightnessForRender();

        GlStateManager.pushMatrix();
        bindEntityTexture(entity);
        GlStateManager.translate((float)x, (float)y, (float)z);
        GlStateManager.enableRescaleNormal();
        GlStateManager.scale(1.3F, 1.3F, 1.3F);
        GlStateManager.rotate(180.0F - renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate((renderManager.options.thirdPersonView == 2 ? -1 : 1) * -renderManager.playerViewX, 1.0F, 0.0F, 0.0F);

        if(renderOutlines)
        {
            GlStateManager.enableColorMaterial();
            GlStateManager.enableOutlineMode(getTeamColor(entity));
        }

        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableLighting();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 61680.0F, 0.0F);
        GlStateManager.enableLighting();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getMinecraft().entityRenderer.setupFogColor(true);

        bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
        bufferBuilder.pos(-0.5D, -0.25D, 0.0D).tex(minU, maxV).color(204, 0, 250, 255).normal(0.0F, 1.0F, 0.0F).endVertex();
        bufferBuilder.pos(0.5D, -0.25D, 0.0D).tex(maxU, maxV).color(204, 0, 250, 255).normal(0.0F, 1.0F, 0.0F).endVertex();
        bufferBuilder.pos(0.5D, 0.75D, 0.0D).tex(maxU, minV).color(204, 0, 250, 255).normal(0.0F, 1.0F, 0.0F).endVertex();
        bufferBuilder.pos(-0.5D, 0.75D, 0.0D).tex(minU, minV).color(204, 0, 250, 255).normal(0.0F, 1.0F, 0.0F).endVertex();
        tessellator.draw();

        Minecraft.getMinecraft().entityRenderer.setupFogColor(false);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightness % 65536, brightness / 65536);
        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();

        if(renderOutlines)
        {
            GlStateManager.disableOutlineMode();
            GlStateManager.disableColorMaterial();
        }

        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityHeckOrb entity)
    {
        return TextureMap.LOCATION_BLOCKS_TEXTURE;
    }
}
