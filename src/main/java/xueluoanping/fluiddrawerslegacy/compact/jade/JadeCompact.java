package xueluoanping.fluiddrawerslegacy.compact.jade;


import com.jaquadro.minecraft.storagedrawers.block.BlockController;
import com.jaquadro.minecraft.storagedrawers.block.BlockSlave;

import com.jaquadro.minecraft.storagedrawers.block.BlockTrim;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityController;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntitySlave;
import net.minecraft.world.entity.monster.Slime;
import snownee.jade.api.*;
import xueluoanping.fluiddrawerslegacy.block.BlockFluidDrawer;

@WailaPlugin
public class JadeCompact implements IWailaPlugin {

    //todo:
    //use IServerExtensionProvider and IClientExtensionProvider instaed
    //use FluidView.overrideText change text
    //https://github.com/Snownee/Jade/blob/1.19.1-forge/src/main/java/snownee/jade/test/ExampleFluidStorageProvider.java

    public JadeCompact() {
    }


    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(ControllerProviderFixer.INSTANCE, BlockEntityController.class);
        registration.registerBlockDataProvider(ControllerSlaveProviderFixer.INSTANCE, BlockEntitySlave.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(FluidDrawerProvider.INSTANCE,  BlockFluidDrawer.class);
        registration.registerBlockComponent(ControllerProviderFixer.INSTANCE, BlockController.class);
        registration.registerBlockComponent(ControllerSlaveProviderFixer.INSTANCE,  BlockSlave.class);
        // registration.registerBlockComponent(TrimProviderFixer.INSTANCE,  BlockTrim.class);
// 11.3
        // registration.usePickedResult(ModContents.fluiddrawer.get());
    }
}
