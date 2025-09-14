package xueluoanping.fluiddrawerslegacy;


import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;
import xueluoanping.fluiddrawerslegacy.block.BlockFluidDrawer;
import xueluoanping.fluiddrawerslegacy.block.ItemFluidDrawer;
import xueluoanping.fluiddrawerslegacy.block.blockentity.BlockEntityFluidDrawer;
import xueluoanping.fluiddrawerslegacy.block.framed.FramedBlockFluidDrawer;
import xueluoanping.fluiddrawerslegacy.block.framed.FramedItemFluidDrawer;
import xueluoanping.fluiddrawerslegacy.block.framed.blockentity.FramedBlockEntityFluidDrawer;
import xueluoanping.fluiddrawerslegacy.client.gui.ContainerFluiDrawer;

import java.util.IdentityHashMap;
import java.util.Map;


// import static xueluoanping.fluiddrawerslegacy.FluidDrawersLegacyMod.CREATIVE_TAB;

// You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
// Event bus for receiving Registry Events)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModContents {
    public static final DeferredRegister<Item> DREntityBlockItems = DeferredRegister.create(ForgeRegistries.ITEMS, FluidDrawersLegacyMod.MOD_ID);
    public static final DeferredRegister<Block> DREntityBlocks = DeferredRegister.create(ForgeRegistries.BLOCKS, FluidDrawersLegacyMod.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> DRBlockEntities = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, FluidDrawersLegacyMod.MOD_ID);

    public static final DeferredRegister<MenuType<?>> DRMenuType = DeferredRegister.create(ForgeRegistries.MENU_TYPES, FluidDrawersLegacyMod.MOD_ID);

    private static CreativeModeTab MAIN;

    @SuppressWarnings("removal")
    @SubscribeEvent
    public static void creativeModeTabRegister(RegisterEvent event) {
        event.register(Registries.CREATIVE_MODE_TAB, helper -> {
            helper.register(new ResourceLocation(FluidDrawersLegacyMod.MOD_ID, "fluiddrawers"),
                    CreativeModeTab.builder().icon(() -> new ItemStack(DREntityBlockItems.getEntries().stream().findFirst().get().get()))
                            .title(Component.translatable("itemGroup.fluiddrawers"))
                            .displayItems((params, output) -> {
                                DREntityBlockItems.getEntries().forEach((reg) -> {
                                    if (reg.get() instanceof BlockItem blockItem && blockItem.getBlock() instanceof FramedBlockFluidDrawer) return;
                                    output.accept(new ItemStack(reg.get()));
                                });
                            })
                            .build());
        });
    }

    @SubscribeEvent
    public static void blockRegister(RegisterEvent event) {
        event.register(Registries.BLOCK, blockRegisterHelper -> {
            blockRegisterHelper.register("test", new Block(BlockBehaviour.Properties.of()));
        });
    }

    // public static final RegistryObject<Block> fluiddrawer = DREntityBlocks.register("fluiddrawer", () -> new BlockFluidDrawer(BlockBehaviour.Properties.copy(Blocks.GLASS)
    //         .sound(SoundType.GLASS).strength(5.0F)
    //         .noOcclusion().isSuffocating(ModContents::predFalse).isRedstoneConductor(ModContents::predFalse), 1));
    // public static final RegistryObject<Item> itemBlock = DREntityBlockItems.register("fluiddrawer", () -> new ItemFluidDrawer(fluiddrawer.get(), new Item.Properties()));
    // public static final RegistryObject<BlockEntityType<BlockEntityFluidDrawer>> tankTileEntityType = DRBlockEntities.register("fluiddrawer",
    //         () -> BlockEntityType.Builder.of((pos, state) -> new BlockEntityFluidDrawer(1, pos, state), fluiddrawer.get()).build(null));

    public static final RegistryObject<MenuType<ContainerFluiDrawer>> containerType = DRMenuType.register("fluid_drawer_container_1", () -> IForgeMenuType.create(ContainerFluiDrawer::new));


    private static boolean predFalse(BlockState p_235436_0_, BlockGetter p_235436_1_, BlockPos p_235436_2_) {
        return false;
    }


    public static final Map<Block, Block> blockMappings = new IdentityHashMap<>();
    public static final Map<Block, Block> blockMappings2 = new IdentityHashMap<>();

    public static final Map<RegistryObject<Block>, RegistryObject<Block>> blockMappingsSupplier = new IdentityHashMap<>();

    public static void init() {
        int[] sizeclist = {1, 2, 4};
        String withhalf = "_half";
        for (int count : sizeclist) {
            String path = getend(count);
            for (int i = 0; i < 2; i++) {
                var isHalf = i == 1;
                if (isHalf) path += withhalf;
                RegistryObject<Block> fluiddrawer = DREntityBlocks.register(path, () -> new BlockFluidDrawer(BlockBehaviour.Properties.copy(Blocks.GLASS)
                        .sound(SoundType.GLASS).strength(5.0F)
                        .noOcclusion().isSuffocating(ModContents::predFalse).isRedstoneConductor(ModContents::predFalse), count, isHalf));
                RegistryObject<Item> itemBlock = DREntityBlockItems.register(path, () -> new ItemFluidDrawer(fluiddrawer.get(), new Item.Properties()));
                RegistryObject<BlockEntityType<BlockEntityFluidDrawer>> tankTileEntityType = DRBlockEntities.register(path,
                        () -> BlockEntityType.Builder.of((pos, state) -> new BlockEntityFluidDrawer(count, pos, state), fluiddrawer.get()).build(null));

                String s = "framed_" + path;
                RegistryObject<Block> framed_fluiddrawer = DREntityBlocks.register(s, () -> new FramedBlockFluidDrawer(BlockBehaviour.Properties.copy(Blocks.GLASS)
                        .sound(SoundType.GLASS).strength(5.0F)
                        .noOcclusion().isSuffocating(ModContents::predFalse).isRedstoneConductor(ModContents::predFalse), count, isHalf));
                RegistryObject<Item> framed_itemBlock = DREntityBlockItems.register(s, () -> new FramedItemFluidDrawer(framed_fluiddrawer.get(), new Item.Properties()));
                RegistryObject<BlockEntityType<FramedBlockEntityFluidDrawer>> framed_tankTileEntityType = DRBlockEntities.register(s,
                        () -> BlockEntityType.Builder.of((pos, state) -> new FramedBlockEntityFluidDrawer(count, pos, state), framed_fluiddrawer.get()).build(null));

                blockMappingsSupplier.put(fluiddrawer, framed_fluiddrawer);
            }
        }
    }

    public static FramedBlockFluidDrawer get(BlockFluidDrawer orginial) {
        if (blockMappings.isEmpty()) {
            blockMappingsSupplier.forEach(
                    (bo, bo2) -> blockMappings.put(bo.get(), bo2.get())
            );
        }
        return (FramedBlockFluidDrawer) blockMappings.get(orginial);
    }

    public static BlockFluidDrawer getReverse(FramedBlockFluidDrawer orginial) {
        if (blockMappings2.isEmpty()) {
            blockMappingsSupplier.forEach(
                    (bo, bo2) -> blockMappings2.put(bo2.get(), bo.get())
            );
        }
        return (BlockFluidDrawer) blockMappings2.get(orginial);
    }

    public static String getend(int s) {
        String path = "fluiddrawer";
        if (s == 4) return path + "_4";
        else if (s == 2) return path + "_2";
        else return path;
    }
}

