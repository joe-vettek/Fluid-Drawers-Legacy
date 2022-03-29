package xueluoanping.fluiddrawerslegacy.block;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import xueluoanping.fluiddrawerslegacy.ModContents;
import xueluoanping.fluiddrawerslegacy.block.tileentity.TileEntityFluidDrawer;
import xueluoanping.fluiddrawerslegacy.client.render.FluidDrawerItemStackTileEntityRenderer;
import xueluoanping.fluiddrawerslegacy.util.SafeClientAccess;
import xueluoanping.fluiddrawerslegacy.util.TooltipKey;

import javax.annotation.Nullable;
import java.util.List;

public class ItemFluidDrawer extends BlockItem {
    public ItemFluidDrawer(Block block, Properties properties) {
        super(block, properties.setISTER(() -> FluidDrawerItemStackTileEntityRenderer::new));
    }

    @Override
    public ITextComponent getName(ItemStack stack) {
        return super.getName(stack);
    }

    public static ITextComponent getNameStatic(ItemStack stack) {
        if (stack.getOrCreateTag().contains("tank")) {
            FluidStack fluidStack = FluidStack.loadFluidStackFromNBT((CompoundNBT) stack.getOrCreateTag().get("tank"));
            if (fluidStack.getAmount() == 0 || fluidStack.getFluid() == Fluids.EMPTY)
                return ModContents.itemBlock.getName(ModContents.itemBlock.getDefaultInstance());
            if (!Minecraft.getInstance().getLanguageManager().getSelected().getName().equals("简体中文"))
                return ((TranslationTextComponent) ModContents.itemBlock.getName(ModContents.itemBlock.getDefaultInstance()))
                        .append(new StringTextComponent(" ")
                                .append(new TranslationTextComponent("statement.fluiddrawerslegacy.fluiddrawer1"))
                                .append(new TranslationTextComponent("statement.fluiddrawerslegacy.fluiddrawer2"))
                                .append(new TranslationTextComponent(fluidStack.getTranslationKey()))
                                .append(" (" + fluidStack.getAmount() + " mB)"));
            return new TranslationTextComponent("statement.fluiddrawerslegacy.fluiddrawer1")
                    .append(new TranslationTextComponent(fluidStack.getTranslationKey()))
                    .append(new TranslationTextComponent("statement.fluiddrawerslegacy.fluiddrawer2"))
                    .append(ModContents.itemBlock.getName(ModContents.itemBlock.getDefaultInstance()))
                    .append(" (" + fluidStack.getAmount() + " mB)");
        }
        return ModContents.itemBlock.getName(ModContents.itemBlock.getDefaultInstance());
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return super.initCapabilities(stack, nbt);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World level, List<ITextComponent> componentList, ITooltipFlag flag) {
        super.appendHoverText(stack, level, componentList, flag);
//        FluidDrawersLegacyMod.LOGGER.info(level);
        if (level instanceof ClientWorld) {
            TooltipKey key = SafeClientAccess.getTooltipKey();
            if (key == TooltipKey.SHIFT || key == TooltipKey.UNKNOWN) {
                if (stack.getOrCreateTag().contains("tank")) {
                    FluidStack fluidStack = FluidStack.loadFluidStackFromNBT((CompoundNBT) stack.getOrCreateTag().get("tank"));

                    if (fluidStack.getAmount() > 0) {
                        if (stack.getOrCreateTag().toString().contains("storagedrawers:creative_vending_upgrade"))
                            fluidStack.setAmount(Integer.MAX_VALUE);
                        componentList.add(new TranslationTextComponent("statement.fluiddrawerslegacy.fluiddrawer1")
                                .append(String.valueOf(fluidStack.getAmount()))
                                .append("/" + TileEntityFluidDrawer.calcultaeCapacitybyStack(stack) + "mB")
                                .append(new TranslationTextComponent("statement.fluiddrawerslegacy.fluiddrawer2"))
                                .append(new TranslationTextComponent(fluidStack.getTranslationKey())));
                    }
                }
            }

//            }

        }
    }

}
