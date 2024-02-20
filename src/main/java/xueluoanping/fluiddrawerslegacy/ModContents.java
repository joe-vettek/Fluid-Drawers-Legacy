package xueluoanping.fluiddrawerslegacy;


import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.extensions.IForgeMenuType;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import xueluoanping.fluiddrawerslegacy.block.BlockFluidDrawer;
import xueluoanping.fluiddrawerslegacy.block.ItemFluidDrawer;
import xueluoanping.fluiddrawerslegacy.block.blockentity.BlockEntityFluidDrawer;
import xueluoanping.fluiddrawerslegacy.client.gui.ContainerFluiDrawer;


import static xueluoanping.fluiddrawerslegacy.FluidDrawersLegacyMod.CREATIVE_TAB;

// You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
// Event bus for receiving Registry Events)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModContents {
    public static final DeferredRegister<Item> DREntityBlockItems = DeferredRegister.create(ForgeRegistries.ITEMS, FluidDrawersLegacyMod.MOD_ID);
    public static final DeferredRegister<Block> DREntityBlocks = DeferredRegister.create(ForgeRegistries.BLOCKS, FluidDrawersLegacyMod.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> DRBlockEntities = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, FluidDrawersLegacyMod.MOD_ID);

    public static final DeferredRegister<MenuType<?>> DRMenuType = DeferredRegister.create(ForgeRegistries.MENU_TYPES, FluidDrawersLegacyMod.MOD_ID);

    // public static void creativeModeTabRegister(CreativeModeTabEvent.Register event) {
    //     MAIN = event.registerCreativeModeTab(new ResourceLocation(MOD_ID, "fluiddrawers"), builder -> builder
    //             .icon(() -> ModContents.itemBlock.get().getDefaultInstance()
    //             .title(Component.translatable("fluiddrawers"))
    //             .displayItems((features, output, hasPermissions) -> {
    //                 ITEM_REGISTER.getEntries().forEach((reg) -> {
    //                     output.accept(new ItemStack(reg.get()));
    //                 });
    //             }));
    // }

    public static final RegistryObject<Block> fluiddrawer = DREntityBlocks.register("fluiddrawer", () -> new BlockFluidDrawer(BlockBehaviour.Properties.of(Material.GLASS)
            .sound(SoundType.GLASS).strength(5.0F)
            .noOcclusion().isSuffocating(ModContents::predFalse).isRedstoneConductor(ModContents::predFalse)));
    public static final RegistryObject<Item> itemBlock = DREntityBlockItems.register("fluiddrawer", () -> new ItemFluidDrawer(fluiddrawer.get(), new Item.Properties().tab(CREATIVE_TAB)));
    public static final RegistryObject<BlockEntityType<BlockEntityFluidDrawer>> tankTileEntityType = DRBlockEntities.register("fluiddrawer",
            () ->  BlockEntityType.Builder.of(BlockEntityFluidDrawer::new, fluiddrawer.get()).build( null));

    public static final RegistryObject<MenuType<ContainerFluiDrawer>> containerType = DRMenuType.register("fluid_drawer_container_1", () -> IForgeMenuType.create( ContainerFluiDrawer::new));


    private static boolean predFalse(BlockState p_235436_0_, BlockGetter p_235436_1_, BlockPos p_235436_2_) {
        return false;
    }

}

