package xueluoanping.fluiddrawerslegacy.block;


import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
// import net.minecraftforge.client.IItemRenderProperties;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import xueluoanping.fluiddrawerslegacy.client.render.FluidDrawerItemStackTileEntityRenderer;
import xueluoanping.fluiddrawerslegacy.util.SafeClientAccess;
import xueluoanping.fluiddrawerslegacy.util.TooltipKey;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Consumer;

public class ItemFluidDrawer extends BlockItem {


    public ItemFluidDrawer(Block block, Properties properties) {
        super(block, properties);
    }

    // @Override
    // public void initializeClient(Consumer<IClientItemExtensions> consumer) {
    //     super.initializeClient(consumer);
    //     consumer.accept(new IClientItemExtensions() {
    //         @Override
    //         public BlockEntityWithoutLevelRenderer getCustomRenderer() {
    //             return new FluidDrawerItemStackTileEntityRenderer(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    //
    //         }
    //     });
    // }

    // @Override
    // public void initializeClient(java.util.function.Consumer<net.minecraftforge.client.IItemRenderProperties> consumer)
    // {
    //     super.initializeClient(consumer);
    //
    //     consumer.accept(new IItemRenderProperties()
    //     {
    //         @Override
    //         public BlockEntityWithoutLevelRenderer getItemStackRenderer()
    //         {
    //             return new FluidDrawerItemStackTileEntityRenderer(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    //         }
    //     });
    // }


    @Override
    public Component getName(ItemStack stack) {
        return super.getName(stack);
    }


    @Override
    public void appendHoverText(ItemStack stack, TooltipContext pContext, List<Component> componentList, TooltipFlag flag) {
        super.appendHoverText(stack, pContext, componentList, flag);
        if (pContext.level() instanceof ClientLevel) {
            TooltipKey key = SafeClientAccess.getTooltipKey();
            if (key == TooltipKey.SHIFT || key == TooltipKey.UNKNOWN) {
                boolean hasFluid = false;
                var tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
                if (tag.contains("tank")) {
                    ListTag tanklist = new ListTag();
                    tanklist.add(tag.getCompound("tank"));
                    tag.put("tanks", tanklist);
                }
                if (tag.contains("tanks")) {
                    int slotCouont = 0;
                    for (Tag tank : tag.getList("tanks", ListTag.TAG_COMPOUND)) {
                        slotCouont++;
                        FluidStack fluidStack = FluidStack.parseOptional(pContext.registries(), ((CompoundTag) (tank)).getCompound("Fluid"));
                        if (tag.toString().contains("storagedrawers:creative_vending_upgrade"))
                            fluidStack.setAmount(Integer.MAX_VALUE);
                        if (fluidStack.getAmount() > 0) {
                            hasFluid = true;

                            String str = I18n.get("statement.fluiddrawerslegacy.fluiddrawer.slot", slotCouont, fluidStack.getAmount(), fluidStack.getHoverName().getString());

                            componentList
                                    .add(Component.translatable(str));
//                                             .append(Component.translatable("statement.fluiddrawerslegacy.fluiddrawer1"))
//                                             .append(String.valueOf(fluidStack.getAmount()))
// //                                .append("/" + TileEntityFluidDrawer.calcultaeCapacitybyStack(stack) + "mB")
//                                             .append(Component.translatable("statement.fluiddrawerslegacy.fluiddrawer2"))
//                                             .append(Component.translatable(fluidStack.getTranslationKey())))
                            ;
                        }
                    }
                }
                if (tag.contains("Lock")) {
                    Byte b = tag.getByte("Lock");
                    EnumSet<LockAttribute> attrs = LockAttribute.getEnumSet(b);
                    if (attrs.contains(LockAttribute.LOCK_EMPTY)) {
                        String fluidNameShow = "";
                        if (!hasFluid) {
                            if (tag.contains("tank") && tag.getCompound("tank").contains("cache")) {
                                FluidStack fluidStack = FluidStack.parseOptional(pContext.registries(), (CompoundTag) tag.getCompound("tank").getCompound("cache"));

                                fluidNameShow = fluidStack.getHoverName().getString() + " ";
                            }
                        }

                        componentList.add(Component.translatable(" §7(" + fluidNameShow + I18n.get("tooltip.storagedrawers.waila.locked") + ") "));

                    }
                }
            }
        }
    }

}
