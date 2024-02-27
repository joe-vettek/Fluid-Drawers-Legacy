package xueluoanping.fluiddrawerslegacy.client.gui;

import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
// import com.jaquadro.minecraft.storagedrawers.client.renderer.StorageRenderItem;
import com.jaquadro.minecraft.storagedrawers.inventory.SlotUpgrade;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;

import net.minecraft.client.Minecraft;
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
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.registries.ForgeRegistries;
import org.joml.Matrix4f;
import xueluoanping.fluiddrawerslegacy.block.blockentity.BlockEntityFluidDrawer;

import java.util.*;


public class Screen extends AbstractContainerScreen<ContainerFluiDrawer> {
    private static final ResourceLocation guiTextures1 = new ResourceLocation("storagedrawers", "textures/gui/drawers_1.png");

    private static final int smDisabledX = 176;
    private static final int smDisabledY = 0;
    // private static StorageRenderItem storageItemRender;
    // private static StorageGuiGraphics storageGuiGraphics;
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
        // if (storageGuiGraphics == null && minecraft != null) {
        //     storageGuiGraphics  = new StorageGuiGraphics(minecraft, minecraft.renderBuffers().bufferSource());
        // }

    }

    public boolean hasFluidInfo() {
        FluidStack fluidStackDown = this.menu.getTileEntityFluidDrawer().getTankFLuid();
        return fluidStackDown.getFluid() != Fluids.EMPTY;
    }

    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        // ((ContainerFluiDrawer) this.menu).activeGuiGraphics = storageGuiGraphics;
        // super.render(storageGuiGraphics, mouseX, mouseY, partialTicks);
        // ((ContainerFluiDrawer) this.menu).activeGuiGraphics = null;
        // storageGuiGraphics.overrideStack = ItemStack.EMPTY;

        // can not render empty


        super.render(graphics, mouseX, mouseY, partialTicks);

        renderTooltip(graphics, mouseX, mouseY);
        //        this.font.draw(stack, mouseY + "|" + mouseX, mouseX, mouseY, 4210752);
        //        this.font.draw(stack, "屏高" + imageHeight + ",屏宽" + imageWidth, mouseX, mouseY + 8, 4210752);
        //        this.font.draw(stack, "屏高" + height + ",屏宽" + width, mouseX, mouseY + 16, 4210752);

    }

    @Override
    protected void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderTooltip(graphics, mouseX, mouseY);

        if (hasFluidInfo() && isHovering(mouseX, mouseY, 17, 17, mouseX, mouseY)) {
            FluidStack fluidStackDown = this.menu.getTileEntityFluidDrawer().getTankFLuid();
            int capacity = this.menu.getTileEntityFluidDrawer().getCapacityTank();
            int amount = fluidStackDown.getAmount();
            if (capacity < amount)
                amount = capacity;
            List<Component> list = new ArrayList<>();

            if (this.menu.getTileEntityFluidDrawer().getDrawerAttributes().isItemLocked(LockAttribute.LOCK_EMPTY)) {
                BlockEntityFluidDrawer.betterFluidHandler betterFluidHandler = null;
                // FluidStack fluidStackDown = FluidStack.EMPTY;
                // int capacity = 0;
                // boolean isLocked = tile.getDrawerAttributes().isItemLocked(LockAttribute.LOCK_EMPTY);
                for (BlockEntityFluidDrawer.FluidDrawerData data : this.menu.getTileEntityFluidDrawer().getTank().getFluidDrawerDataList()) {
                    betterFluidHandler = data.getTank();
                }
                if (fluidStackDown.getAmount() <= 0 &&
                        betterFluidHandler.getCacheFluid().getRawFluid() != Fluids.EMPTY) {
                    fluidStackDown = new FluidStack(betterFluidHandler.getCacheFluid(), 1000);
                }
            }
            list.add(new FluidStack(fluidStackDown, fluidStackDown.getAmount()).getDisplayName());
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
        extraHeight=Math.max(1,extraHeight);

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
        FluidStack fluidStackDown = ((ContainerFluiDrawer) this.menu).getTileEntityFluidDrawer().getTankFLuid();
        int amount = fluidStackDown.getAmount();
        String amountLabel = String.valueOf(amount) + "mB";
        int textWidth = font.width(amountLabel);

        if (this.menu.getTileEntityFluidDrawer().upgrades().hasVendingUpgrade())
            amount = Integer.MAX_VALUE;
        //        每个数量级3.0f
        if (amount > 10000000) {
            amountLabel = "∞";
            textWidth = font.width(amountLabel);
        }
        graphics.drawString(this.font, amountLabel, (float) ((this.imageWidth - textWidth) / 2.0), (float) (this.imageHeight - 144.5 + 2), 2237562, false);


    }


    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, this.background);
        int guiX = (this.width - this.imageWidth) / 2;
        int guiY = (this.height - this.imageHeight) / 2;
        graphics.blit(this.background, guiX, guiY, 0, 0, this.imageWidth, this.imageHeight);
        //        List<Slot> storageSlots = ((ContainerFluiDrawer)this.menu).getStorageSlots();
        //        Iterator var8 = storageSlots.iterator();
        //
        //        while(var8.hasNext()) {
        //            Slot slot = (Slot)var8.next();
        //            this.blit(stack, guiX + slot.x, guiY + slot.y, 176, 0, 16, 16);
        //        }

        List<Slot> upgradeSlots = ((ContainerFluiDrawer) this.menu).getUpgradeSlots();
        Iterator var12 = upgradeSlots.iterator();

        while (var12.hasNext()) {
            Slot slot = (Slot) var12.next();
            if (slot instanceof SlotUpgrade && !((SlotUpgrade) slot).canTakeStack()) {
                graphics.blit(this.background, guiX + slot.x, guiY + slot.y, 176, 0, 16, 16);
            }
        }


        FluidStack fluidStackDown = this.menu.getTileEntityFluidDrawer().getTankFLuid();


        // FluidType attributes = fluidStackDown.getFluid().getFluidType();
        // TextureAtlasSprite still = getBlockSprite(IClientFluidTypeExtensions.of(fluidStackDown.getFluid()).getStillTexture());
        // int colorRGB = IClientFluidTypeExtensions.of(fluidStackDown.getFluid()).getTintColor();

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
        //        FluidDrawersLegacyMod.LOGGER.info(""+h+amount+"/]]"+capacity);


        PoseStack poseStack = graphics.pose();
        poseStack.pushPose();
        // poseStack.translate(guiX + upgradeSlots.get(3).x, guiY + 52, 0);
        if (hasFluidInfo())
            renderFluidStackInGUI(poseStack.last().pose(), fluidStackDown, 16, h0, guiX + upgradeSlots.get(3).x, guiY + 52);
        if (isHovering(mouseX, mouseY, 17, 17, mouseX, mouseY))
            graphics.fill(guiX + upgradeSlots.get(3).x, guiY + 36, guiX + upgradeSlots.get(3).x + 16, guiY + 52, 0, 0x88FFFFFF);

        poseStack.popPose();
    }

    protected boolean isHovering(int x, int y, int width, int height, double originX, double originY) {


        int innerX = x - getGuiLeft();
        int innerY = y - getGuiTop();
        int startX = 78;
        int startY = 35;
        int bound = 17;

        if (innerX > startX - 1 && innerX - bound - 1 < startX) {
            if (innerY > startY - 1 && innerY - bound - 1 < startY) {
                return true;
            }
        }

        return super.isHovering(x, y, width, height, originX, originY);

    }

    // private ItemRenderer setItemRender(ItemRenderer renderItem) {
    //     ItemRenderer prev = this.itemRenderer;
    //     this.itemRenderer = renderItem;
    //     return prev;
    // }


    public static class Slot1 extends Screen {
        public Slot1(ContainerFluiDrawer container, Inventory playerInv, Component name) {
            super(container, playerInv, name, Screen.guiTextures1);
        }
    }

}
