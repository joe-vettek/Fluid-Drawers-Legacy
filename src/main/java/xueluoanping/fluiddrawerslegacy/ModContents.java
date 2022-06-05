package xueluoanping.fluiddrawerslegacy;


import com.mojang.datafixers.types.Type;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xueluoanping.fluiddrawerslegacy.block.BlockFluidDrawer;
import xueluoanping.fluiddrawerslegacy.block.ItemFluidDrawer;
import xueluoanping.fluiddrawerslegacy.block.tileentity.TileEntityFluidDrawer;
import xueluoanping.fluiddrawerslegacy.client.gui.ContainerFluiDrawer;

import static xueluoanping.fluiddrawerslegacy.FluidDrawersLegacyMod.CREATIVE_TAB;

// You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
// Event bus for receiving Registry Events)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModContents {
    public static Block fluiddrawer = null;
    public static TileEntityType<TileEntityFluidDrawer> tankTileEntityType = null;
    public static BlockItem itemBlock = null;
    public static ContainerType<ContainerFluiDrawer> containerType = null;

    @SubscribeEvent
    public static void onBlocksRegistry(final RegistryEvent.Register<Block> event) {
        // register a new block here
        FluidDrawersLegacyMod.logger("Register Block");
        fluiddrawer = new BlockFluidDrawer(AbstractBlock.Properties.of(Material.STONE)
                .sound(SoundType.GLASS).strength(5.0F)
                .noOcclusion().isSuffocating(ModContents::predFalse).isRedstoneConductor(ModContents::predFalse)

        );
        event.getRegistry().register(fluiddrawer.setRegistryName("fluiddrawer"));
    }

    private static boolean predFalse(BlockState p_235436_0_, IBlockReader p_235436_1_, BlockPos p_235436_2_) {
        return false;
    }

    @SubscribeEvent
    public static void registerTileEntities(RegistryEvent.Register<TileEntityType<?>> event) {
        FluidDrawersLegacyMod.logger("Register Block Entity");
        tankTileEntityType = (TileEntityType<TileEntityFluidDrawer>) TileEntityType.Builder.of(TileEntityFluidDrawer::new, fluiddrawer).build((Type) null).setRegistryName(new ResourceLocation(FluidDrawersLegacyMod.MOD_ID, "fluiddrawer"));
        event.getRegistry().register(tankTileEntityType);

    }

    @SubscribeEvent
    public static void registerItems(final RegistryEvent.Register<Item> event) {
        itemBlock = new ItemFluidDrawer(fluiddrawer, new Item.Properties().tab(CREATIVE_TAB));
        event.getRegistry().register(itemBlock.setRegistryName(fluiddrawer.getRegistryName()));
        FluidDrawersLegacyMod.logger("Register Item");
    }


    @SubscribeEvent
    public static void onContainerRegistry(final RegistryEvent.Register<ContainerType<?>> event) {
        containerType = (ContainerType<ContainerFluiDrawer>) IForgeContainerType.create(ContainerFluiDrawer::new).setRegistryName("container_1");
        event.getRegistry().register(containerType);
    }


}

