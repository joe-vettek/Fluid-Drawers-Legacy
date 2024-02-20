package xueluoanping.fluiddrawerslegacy;



import com.mojang.datafixers.types.Type;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xueluoanping.fluiddrawerslegacy.block.BlockFluidDrawer;
import xueluoanping.fluiddrawerslegacy.block.ItemFluidDrawer;
import xueluoanping.fluiddrawerslegacy.block.blockentity.BlockEntityFluidDrawer;
import xueluoanping.fluiddrawerslegacy.client.gui.ContainerFluiDrawer;


import static xueluoanping.fluiddrawerslegacy.FluidDrawersLegacyMod.CREATIVE_TAB;

// You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
// Event bus for receiving Registry Events)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModContents {
    public static Block fluiddrawer = null;
    public static BlockEntityType<BlockEntityFluidDrawer> tankTileEntityType = null;
    public static BlockItem itemBlock = null;
    public static MenuType<ContainerFluiDrawer> containerType = null;

    @SubscribeEvent
    public static void onBlocksRegistry(final RegistryEvent.Register<Block> event) {
        // register a new block here
        FluidDrawersLegacyMod.logger("Register Block");
        fluiddrawer = new BlockFluidDrawer(BlockBehaviour.Properties.of(Material.GLASS)
                .sound(SoundType.GLASS).strength(5.0F)
                .noOcclusion().isSuffocating(ModContents::predFalse).isRedstoneConductor(ModContents::predFalse)

        );
        event.getRegistry().register(fluiddrawer.setRegistryName("fluiddrawer"));
    }

    private static boolean predFalse(BlockState p_235436_0_, BlockGetter p_235436_1_, BlockPos p_235436_2_) {
        return false;
    }

    @SubscribeEvent
    public static void registerTileEntities(RegistryEvent.Register<BlockEntityType<?>> event) {
        FluidDrawersLegacyMod.logger("Register Block Entity");
        tankTileEntityType = (BlockEntityType<BlockEntityFluidDrawer>) BlockEntityType.Builder.of(BlockEntityFluidDrawer::new, fluiddrawer).build((Type) null).setRegistryName(new ResourceLocation(FluidDrawersLegacyMod.MOD_ID, "fluiddrawer"));
        event.getRegistry().register(tankTileEntityType);

    }

    @SubscribeEvent
    public static void registerItems(final RegistryEvent.Register<Item> event) {
        itemBlock = new ItemFluidDrawer(fluiddrawer, new Item.Properties().tab(CREATIVE_TAB));
        event.getRegistry().register(itemBlock.setRegistryName(fluiddrawer.getRegistryName()));
        FluidDrawersLegacyMod.logger("Register Item");
    }


    @SubscribeEvent
    public static void onContainerRegistry(final RegistryEvent.Register<MenuType<?>> event) {
        containerType =
                (MenuType<ContainerFluiDrawer>) IForgeMenuType.create(ContainerFluiDrawer::new).setRegistryName("container_1");
        event.getRegistry().register(containerType);
    }


}

