package xueluoanping.fluiddrawerslegacy;


import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.core.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
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
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.RegisterEvent;
import xueluoanping.fluiddrawerslegacy.block.BlockFluidDrawer;
import xueluoanping.fluiddrawerslegacy.block.ItemFluidDrawer;
import xueluoanping.fluiddrawerslegacy.block.blockentity.BlockEntityFluidDrawer;
import xueluoanping.fluiddrawerslegacy.capability.CapabilityProvider_FluidDrawerController;
import xueluoanping.fluiddrawerslegacy.client.gui.ContainerFluiDrawer;

import java.util.function.Supplier;


// import static xueluoanping.fluiddrawerslegacy.FluidDrawersLegacyMod.CREATIVE_TAB;

// You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
// Event bus for receiving Registry Events)
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class ModContents {
    public static final DeferredRegister<Item> DREntityBlockItems = DeferredRegister.create(Registries.ITEM, FluidDrawersLegacyMod.MOD_ID);
    public static final DeferredRegister<Block> DREntityBlocks = DeferredRegister.create(Registries.BLOCK, FluidDrawersLegacyMod.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> DRBlockEntities = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, FluidDrawersLegacyMod.MOD_ID);

    public static final DeferredRegister<MenuType<?>> DRMenuType = DeferredRegister.create(Registries.MENU, FluidDrawersLegacyMod.MOD_ID);

    private static CreativeModeTab MAIN;

    @SubscribeEvent
    public static void creativeModeTabRegister(RegisterEvent event) {
        event.register(Registries.CREATIVE_MODE_TAB, helper -> {
            helper.register(FluidDrawersLegacyMod.rl( "fluiddrawers"),
                    CreativeModeTab.builder().icon(() -> new ItemStack(DREntityBlockItems.getEntries().stream().findFirst().get().get()))
                            .title(Component.translatable("itemGroup.fluiddrawers"))
                            .displayItems((params, output) -> {
                                DREntityBlockItems.getEntries().forEach((reg) -> {
                                    output.accept(new ItemStack(reg.get()));
                                });
                            })
                            .build());
        });
    }

    @SubscribeEvent
    public static void blockRegister(RegisterEvent event) {
        event.register(Registries.BLOCK, blockRegisterHelper -> {
            blockRegisterHelper.register(ResourceLocation.parse("test"), new Block(BlockBehaviour.Properties.of()));
        });
    }

    // public static final RegistryObject<Block> fluiddrawer = DREntityBlocks.register("fluiddrawer", () -> new BlockFluidDrawer(BlockBehaviour.Properties.copy(Blocks.GLASS)
    //         .sound(SoundType.GLASS).strength(5.0F)
    //         .noOcclusion().isSuffocating(ModContents::predFalse).isRedstoneConductor(ModContents::predFalse), 1));
    // public static final RegistryObject<Item> itemBlock = DREntityBlockItems.register("fluiddrawer", () -> new ItemFluidDrawer(fluiddrawer.get(), new Item.Properties()));
    // public static final RegistryObject<BlockEntityType<BlockEntityFluidDrawer>> tankTileEntityType = DRBlockEntities.register("fluiddrawer",
    //         () -> BlockEntityType.Builder.of((pos, state) -> new BlockEntityFluidDrawer(1, pos, state), fluiddrawer.get()).build(null));

    public static final Supplier<MenuType<ContainerFluiDrawer>> containerType = DRMenuType.register("fluid_drawer_container_1", () -> IMenuTypeExtension.create(ContainerFluiDrawer::new));


    private static boolean predFalse(BlockState p_235436_0_, BlockGetter p_235436_1_, BlockPos p_235436_2_) {
        return false;
    }


    public static void init() {
        int[] sizeclist = {1, 2, 4};
        String withhalf = "_half";
        for (int count : sizeclist) {
            String path = getend(count);
            for (int i = 0; i < 2; i++) {
                var isHalf=i==1;
                if(isHalf)path+=withhalf;
                DeferredHolder<Block, BlockFluidDrawer> fluiddrawer = DREntityBlocks.register(path, () -> new BlockFluidDrawer(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)
                        .sound(SoundType.GLASS).strength(5.0F)
                        .noOcclusion().isSuffocating(ModContents::predFalse).isRedstoneConductor(ModContents::predFalse), count, isHalf));
                DeferredHolder<Item,ItemFluidDrawer> itemBlock = DREntityBlockItems.register(path, () -> new ItemFluidDrawer(fluiddrawer.get(), new Item.Properties()));
                DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityFluidDrawer>> tankTileEntityType = DRBlockEntities.register(path,
                        () -> BlockEntityType.Builder.of((pos, state) -> new BlockEntityFluidDrawer(count, pos, state), fluiddrawer.get()).build(null));

            }
        }
    }

    public static String getend(int s) {
        String path = "fluiddrawer";
        if (s == 4) return path + "_4";
        else if (s == 2) return path + "_2";
        else return path;
    }

    // Create the DeferredRegister for attachment types
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, FluidDrawersLegacyMod.MOD_ID);

    // Serialization via INBTSerializable
    public static final Supplier<AttachmentType<CapabilityProvider_FluidDrawerController>> HANDLER = ATTACHMENT_TYPES.register(
            "fluid_ctrl", () -> AttachmentType.serializable(() -> new CapabilityProvider_FluidDrawerController()).build()
    );
    //
    // public static final Supplier<AttachmentType<CapabilityProvider_FluidControllerProxy>> HANDLER_2 = ATTACHMENT_TYPES.register(
    //         "fluid_proxy", () -> AttachmentType.serializable(() -> new CapabilityProvider_FluidControllerProxy()).build()
    // );

    public static void onTileCapabilities(RegisterCapabilitiesEvent event) {
        // FluidDrawersLegacyMod.logger(event.getObject().getLevel());

        for (var entry : ModContents.DRBlockEntities.getEntries()) {
            event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, entry.value(), (entity, context) -> {
                if (entity instanceof BlockEntityFluidDrawer blockEntityFluidDrawer)
                    return ((BlockEntityFluidDrawer.FluidGroupData)(blockEntityFluidDrawer.getGroup())).tank;
                else return null;
            });
            event.registerBlockEntity(ModConstants.DRAWER_ATTRIBUTES_CAPABILITY, entry.value(), (entity, context) -> {
                return ((BlockEntityFluidDrawer)entity).getDrawerAttributes();
            });

            event.registerBlockEntity(ModConstants.DRAWER_GROUP_CAPABILITY, entry.value(), (entity, context) -> {
                return ((BlockEntityFluidDrawer)entity).getGroup();
            });
        }




        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.CONTROLLER.get(), (entity, context) -> {
            CapabilityProvider_FluidDrawerController data = entity.getData(ModContents.HANDLER);
            if (!data.hasTile()) data.setTile(entity);
            return data.getCapability(entity, context);
        });

        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.CONTROLLER_IO.get(), (entity, context) -> {
            CapabilityProvider_FluidDrawerController data = entity.getData(ModContents.HANDLER);
            boolean isvalid=entity.getController() != null && entity.getController().isValidIO(entity.getBlockPos());
            if (!isvalid) {
                entity.removeData(ModContents.HANDLER);
                return null;
            }
            if (!data.hasTile()) data.setTile(entity.getController());
            return data.getCapability(entity, context);
        });
    }
}

