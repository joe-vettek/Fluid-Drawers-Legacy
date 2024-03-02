package xueluoanping.fluiddrawerslegacy.client.gui;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
// import com.jaquadro.minecraft.storagedrawers.client.renderer.StorageRenderItem;
import com.jaquadro.minecraft.storagedrawers.inventory.SlotUpgrade;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
// import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.registries.ForgeRegistries;
import org.joml.Matrix4f;
import xueluoanping.fluiddrawerslegacy.FluidDrawersLegacyMod;
import xueluoanping.fluiddrawerslegacy.api.IFluidDrawerGroup;
import xueluoanping.fluiddrawerslegacy.block.blockentity.BlockEntityFluidDrawer;

import java.awt.*;
import java.util.*;
import java.util.List;


public class Screen extends AbstractContainerScreen<ContainerFluiDrawer> {

    private static final int smDisabledX = 176;
    private static final int smDisabledY = 0;
    private final ResourceLocation background;
    private final Inventory inventory;

    public Screen(ContainerFluiDrawer container, Inventory playerInv, Component name, ResourceLocation bg) {
        super(container, playerInv, name);
        this.imageWidth = 176;
        this.imageHeight = 199;
        this.background = bg;
        this.inventory = playerInv;
    }

    protected void init() {
        super.init();
    }

    public boolean hasFluidInfo() {
        boolean result = false;
        for (BlockEntityFluidDrawer.FluidDrawerData data : this.menu.getTileEntityFluidDrawer().getTank().getFluidDrawerDataList()) {
            if (!data.getTank().isEmpty())
                result = true;
        }
        return result;
    }

    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderTooltip(graphics, mouseX, mouseY);
        if (hasFluidInfo()) {
            var tile = menu.getTileEntityFluidDrawer();
            int size = tile.getDrawerCount();
            for (int i = 0; i < size; i++) {
                var geo = SlotGeometry.get(i + 1, size, this.width, this.height, this.imageHeight, this.imageWidth);
                // if (geo.left() < mouseX && mouseX < geo.left() + 17)
                //     if (geo.top() - 17 < mouseY && mouseY < geo.top())
                if (isInside(mouseX, mouseY, 17, 17, geo.left(), geo.top())) {
                    var tank = (BlockEntityFluidDrawer.betterFluidHandler) tile.getDrawer(i).getTank();
                    FluidStack fluidStackDown = tank.getFluid();
                    if (fluidStackDown.isEmpty()) {
                        var cache = tank.getCacheFluid();
                        if (!cache.isEmpty())
                            fluidStackDown = new FluidStack(cache, FluidType.BUCKET_VOLUME);
                    }
                    if (!fluidStackDown.isEmpty()) {
                        List<Component> list = new ArrayList<>();
                        // list.add();
                        list.add(Component.translatable(new FluidStack(fluidStackDown, fluidStackDown.getAmount()).getDisplayName().getString()+" §e"+tank.getFluidAmount() + "mB"));
                        ModList modList = ModList.get();
                        FluidStack finalFluidStackDown = fluidStackDown;

                        Optional<Map.Entry<ResourceKey<Fluid>, Fluid>> fluidInfo = ForgeRegistries.FLUIDS.getEntries().stream()
                                .filter(resourceKeyFluidEntry -> resourceKeyFluidEntry.getValue() == finalFluidStackDown.getFluid())
                                .findFirst();

                        fluidInfo.ifPresent(resourceKeyFluidEntry -> {
                            String modId = resourceKeyFluidEntry.getKey().location().getNamespace();
                            // ForgeRegistries.FLUIDS.getKey(finalFluidStackDown.getFluid()).namespace
                            // String modId = finalFluidStackDown.getTranslationKey().split("\\.")[1];
                            Optional<String> modName = modList.getMods().stream().filter((modInfo) -> modInfo.getModId().equals(modId))
                                    .map(IModInfo::getDisplayName)
                                    .findFirst();
                            modName.ifPresent(s -> list.add(Component.literal("§9§o" + s)));

                        });

                        // renderComponentTooltip(stack, list, mouseX, mouseY);
                        graphics.renderComponentTooltip(this.getMinecraft().font, list, mouseX, mouseY);
                    }
                }
            }
        }


    }

    /**
     * 渲染顶点
     *
     * @param matrix  渲染矩阵
     * @param builder builder
     * @param x       顶点x坐标
     * @param y       顶点y坐标
     * @param z       顶点z坐标
     * @param u       顶点对应贴图的u坐标
     * @param v       顶点对应贴图的v坐标
     * @param overlay 覆盖
     * @param light   光照
     */
    public static void buildMatrix(Matrix4f matrix, VertexConsumer builder, float x, float y, float z, float u, float v, int overlay, int RGBA, float alpha, int light) {
        float red = ((RGBA >> 16) & 0xFF) / 255f;
        float green = ((RGBA >> 8) & 0xFF) / 255f;
        float blue = ((RGBA) & 0xFF) / 255f;

        builder.vertex(matrix, x, y, z)
                .color(red, green, blue, alpha)
                .uv(u, v)
                .overlayCoords(overlay)
                .uv2(light)
                .normal(0f, 1f, 0f)
                .endVertex();
    }

    public static void buildMatrix(Matrix4f matrix, VertexConsumer builder, float x, float y, float z, float u, float v, int RGBA) {
        float red = ((RGBA >> 16) & 0xFF) / 255f;
        float green = ((RGBA >> 8) & 0xFF) / 255f;
        float blue = ((RGBA >> 0) & 0xFF) / 255f;
        int alpha = 1;

        builder.vertex(matrix, x, y, z)
                .color(red, green, blue, alpha)
                .uv(u, v)
                .endVertex();
    }

    public static TextureAtlasSprite getBlockSprite(ResourceLocation sprite) {
        return Minecraft.getInstance().getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS).getSprite(sprite);
    }

    /**
     * 在GUI中渲染流体
     *
     * @param matrix 渲染矩阵
     * @param fluid  需要渲染的流体（FluidStack）
     * @param width  需要渲染的流体宽度
     * @param height 需要渲染的流体高度
     * @param x      x（绝对）
     * @param y      y（绝对）
     */
    public static void renderFluidStackInGUI(Matrix4f matrix, FluidStack fluid, int width, int height, float x, float y) {
        // 正常渲染透明度
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        // 获取sprite
        FluidType attributes = fluid.getFluid().getFluidType();
        TextureAtlasSprite FLUID = getBlockSprite(IClientFluidTypeExtensions.of(fluid.getFluid()).getStillTexture(fluid));

        // 绑atlas
        //        Minecraft.getInstance().getTextureManager().bindForSetup(InventoryMenu.BLOCK_ATLAS);
        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);

        //        注意color要这样写，后面的是无效的
        // int color = IClientFluidTypeExtensions.of(fluid.getFluid()).getTintColor();
        int color = IClientFluidTypeExtensions.of(fluid.getFluid()).getTintColor(fluid);
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;
        float a = ((color >> 24) & 0xFF) / 255f;
        RenderSystem.setShaderColor(r, g, b, a);


        /*
         * 获取横向和纵向层数
         * 每16像素为1层，通过将给定渲染长宽不加类型转换除16来获取层数
         * 通过取余获取数值大小在16以下的额外数值
         */
        int wFloors = width / 16;
        int extraWidth = wFloors == 0 ? width : width % 16;
        int hFloors = height / 16;
        int extraHeight = hFloors == 0 ? height : height % 16;
        extraHeight = Math.max(1, extraHeight);
        // add it to avoid too much
        if (height==16)extraHeight=0;

        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        float u0 = FLUID.getU0();
        float v0 = FLUID.getV0();
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder builder = tessellator.getBuilder();

        /*
         * 渲染循环
         * 该循环通过两个嵌套循环完成
         * 外层循环处理 y 和 v 的变更（高度层），内层处理 x 和 u 的变更（宽度层），渲染主代码存于内层
         * 渲染逻辑是 [先从最下面的高度层开始，向右渲染此高度层含的所有宽度层并渲染额外宽度层]
         * [第一层（高）渲染完毕后渲染第二层，依此类推渲染所有高度层和额外高度层，以达成渲染任意长宽的流体矩形的目的]
         * 对于层，若层数为0（渲染数值小于16），则直接将渲染数值设为额外层数值。
         */
        for (int i = hFloors; i >= 0; i--) {
            // i为流程控制码，若i=0则代表高度层已全部渲染完毕，此时若额外层高度为0（渲染高度参数本来就是16的整数倍）则跳出
            if (i == 0 && extraHeight == 0)
                break;
            float yStart = y - ((hFloors - i) * 16);
            // 获取本层/额外层的高度，若高度层渲染完毕则设为额外层高度
            float yOffset = i == 0 ? (float) extraHeight : 16;
            // 获取v1
            float v1 = i == 0 ? FLUID.getV0() + ((FLUID.getV1() - v0) * ((float) extraHeight / 16f)) : FLUID.getV1();

            // x层以此类推
            for (int j = wFloors; j >= 0; j--) {
                if (j == 0 && extraWidth == 0)
                    break;
                float xStart = x + (wFloors - j) * 16;
                float xOffset = j == 0 ? (float) extraWidth : 16;
                float u1 = j == 0 ? FLUID.getU0() + ((FLUID.getU1() - u0) * ((float) extraWidth / 16f)) : FLUID.getU1();

                // 渲染主代码
                builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
                buildMatrix(matrix, builder, xStart, yStart - yOffset, 0.0f, u0, v0, color);
                buildMatrix(matrix, builder, xStart, yStart, 0.0f, u0, v1, color);
                buildMatrix(matrix, builder, xStart + xOffset, yStart, 0.0f, u1, v1, color);
                buildMatrix(matrix, builder, xStart + xOffset, yStart - yOffset, 0.0f, u1, v0, color);
                tessellator.end();
            }
        }
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.disableBlend();
    }


    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title.getString(), 8.0F, 6.0F, 4210752, false);
        graphics.drawString(this.font, I18n.get("container.storagedrawers.upgrades"), 8.0F, 75.0F, 4210752, false);
        graphics.drawString(this.font, this.inventory.getDisplayName().getString(), 8.0F, (float) (this.imageHeight - 96 + 2), 4210752, false);

    }


    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, this.background);
        int guiX = (this.width - this.imageWidth) / 2;
        int guiY = (this.height - this.imageHeight) / 2;
        graphics.blit(this.background, guiX, guiY, 0, 0, this.imageWidth, this.imageHeight);

        List<Slot> upgradeSlots = this.menu.getUpgradeSlots();

        for (Slot slot : upgradeSlots) {
            if (slot instanceof SlotUpgrade && !((SlotUpgrade) slot).canTakeStack()) {
                graphics.blit(this.background, guiX + slot.x, guiY + slot.y, 176, 0, 16, 16);
            }
        }

        PoseStack poseStack = graphics.pose();
        poseStack.pushPose();
        // poseStack.translate(guiX + upgradeSlots.get(3).x, guiY + 52, 0);
        int slot = 0;
        var dlis = menu.getTileEntityFluidDrawer().getTank().getFluidDrawerDataList();
        for (BlockEntityFluidDrawer.FluidDrawerData data : dlis) {
            slot++;
            BlockEntityFluidDrawer.betterFluidHandler betterFluidHandler = data.getTank();
            FluidStack fluidStackDown = betterFluidHandler.getFluid().copy();

            int capacity = this.menu.getTileEntityFluidDrawer().getCapacityTank();
            int amount = fluidStackDown.getAmount();
            if (capacity < amount)
                amount = capacity;
            float h = 0.0f;
            h = (float) amount / (float) capacity;
            if (((float) amount / (float) capacity) <= 0.0625 && ((float) amount / (float) capacity) >= 0.01)
                h = 0.01f;
            if (((float) amount / (float) capacity) > 0.9375 && ((float) amount / (float) capacity) < 0.99)
                h = 0.9375f;
            int h0 = (int) (h * 16.0f);
            if (this.menu.getTileEntityFluidDrawer().upgrades().hasVendingUpgrade())
                h0 = 16;
            var geo = SlotGeometry.get(slot, dlis.size(), this.width, this.height, this.imageHeight, this.imageWidth);
            if (!fluidStackDown.isEmpty())
                renderFluidStackInGUI(poseStack.last().pose(), fluidStackDown, 16, h0, geo.left(), geo.top());

            if (!fluidStackDown.isEmpty()) {
                // int amount = fluidStackDown.getAmount();
                graphics.pose().pushPose();
                float scale_x=0.6f;
                float scale_y=0.6f;
                graphics.pose().scale(scale_x, scale_y, 1.0F);
                double roundedAmount = Math.floor(amount * 10/1000f) / 10; // Round down to one decimal place
                String amountLabel = String.format("%.1f",roundedAmount) + "B";
                if (this.menu.getTileEntityFluidDrawer().upgrades().hasVendingUpgrade())
                    amount = Integer.MAX_VALUE;
                //  one *10 occur 3.0f pixel
                if(amount>=10000){
                    amountLabel = ((int)Math.floor(amount/1000f)) + "B";
                }
                if(amount>=1000*1000){
                    amountLabel = String.valueOf((int)Math.floor(amount/1000f/1000f)) + "K";
                }
                if(amount>=1000*1000*1000){
                    amountLabel = String.valueOf((int)Math.floor(amount/1000f/1000f/1000f)) + "M";
                }
                if (amount >= 1000*1000*1000) {
                    amountLabel = "∞";
                }
                int   textWidth = font.width(amountLabel);
                int innerX = (int) ((geo.left()+16 )/scale_x- textWidth+1);
                int innerY = (int) (geo.top()/scale_y - font.lineHeight +1);
                // (geo.left()+16-textWidth-imageWidth)/2,(geo.top()+16-imageHeight)/2

                // 16777215
                // 2237562
                // Color.DARK_GRAY.hashCode()

                int color=Color.YELLOW.hashCode();
                // for (int i = 0; i < FLUID.contents().width(); i++) {
                //     for (int j = 0; j <FLUID.contents().height(); j++) {
                //         color=FLUID.getPixelRGBA(0,i,j);
                //     }
                // }
                // color=color/(FLUID.contents().width()*FLUID.contents().height());


                graphics.drawString(font, amountLabel, innerX+0.8f*scale_x, innerY+0.8f*scale_y,  Color.DARK_GRAY.hashCode(), false);
                graphics.drawString(font, amountLabel, innerX, innerY,  color, false);

                graphics.pose().popPose();
            }
            // FluidDrawersLegacyMod.logger(mouseX,geo.left());
            if (isInside(mouseX, mouseY, 17, 17, geo.left(), geo.top()))
                // if (geo.left() < mouseX && mouseX < geo.left() + 17)
                //     if (geo.top() - 17 < mouseY && mouseY < geo.top())
                graphics.fill(geo.left(), geo.top() - 16, geo.left() + 16, geo.top(), 0, 0x88FFFFFF);

        }

        poseStack.popPose();
    }

    protected boolean isInside(int x, int y, int w, int h, double originX, double originY) {
        if (x - w < originX && originX < x)
            return y < originY && originY < y + h;
        return false;

    }

    @Override
    protected boolean isHovering(int x, int y, int width, int height, double originX, double originY) {
        return super.isHovering(x, y, width, height, originX, originY);
    }

    public record SlotGeometry(int left, int top, int w, int h) {

        public static SlotGeometry get(int slot, int count, int width, int height, int imageHeight, int imageWidth) {
            int guiX = (width - imageWidth) / 2;
            int guiY = (height - imageHeight) / 2;
            if (count == 4) {
                int orderY = 0;
                int orderX = 0;
                switch (slot) {
                    case 1 -> {
                        orderX = 1;
                        orderY = 1;
                    }
                    case 2 -> {
                        orderX = 2;
                        orderY = 1;
                    }
                    case 3 -> {
                        orderX = 1;
                        orderY = 2;
                    }
                    case 4 -> {
                        orderX = 2;
                        orderY = 2;
                    }
                }
                int left = guiX + 93 + (orderX - 2) * 26;
                int top = guiY + 39 + (orderY - 1) * 26;
                return new SlotGeometry(left, top, 16, 16);
            } else if (count == 2) {
                int orderY = slot == 1 ? 1 : 2;
                return new SlotGeometry(guiX + 80, guiY + 39 + (orderY - 1) * 26, 16, 16);
            }
            return new SlotGeometry(guiX + 80, guiY + 52, 16, 16);
        }
    }

    public static ResourceLocation getBgByType(IFluidDrawerGroup group) {
        int s = group.getDrawerCount();

        if (s == 4)
            return new ResourceLocation(StorageDrawers.MOD_ID, "textures/gui/drawers_4.png");
        else if (s == 2)
            return new ResourceLocation(StorageDrawers.MOD_ID, "textures/gui/drawers_2.png");
        else
            return new ResourceLocation(StorageDrawers.MOD_ID, "textures/gui/drawers_1.png");
    }

    public static class Slot1 extends Screen {
        public Slot1(ContainerFluiDrawer container, Inventory playerInv, Component name) {
            super(container, playerInv, name, getBgByType(container.getTileEntityFluidDrawer()));
        }
    }

}
