package xueluoanping.fluiddrawerslegacy.jade;


import com.jaquadro.minecraft.storagedrawers.block.BlockController;
import com.jaquadro.minecraft.storagedrawers.block.BlockSlave;
import com.jaquadro.minecraft.storagedrawers.block.tile.ChamTileEntity;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityController;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntitySlave;
import mcp.mobius.waila.api.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import xueluoanping.fluiddrawerslegacy.ModContents;
import xueluoanping.fluiddrawerslegacy.block.BlockFluidDrawer;

@WailaPlugin
public class JadeCompact implements IWailaPlugin {


    public JadeCompact() {
    }


    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(ComponentProvider.INSTANCE, TileEntityController.class);
        registration.registerBlockDataProvider(ComponentProvider.INSTANCE, TileEntitySlave.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerComponentProvider(DrawerCompenProvider.INSTANCE, TooltipPosition.BODY, BlockFluidDrawer.class);
        registration.registerComponentProvider(ComponentProvider.INSTANCE, TooltipPosition.BODY, BlockController.class);
        registration.registerComponentProvider(ComponentProvider.INSTANCE, TooltipPosition.BODY, BlockSlave.class);
        registration.usePickedResult(ModContents.fluiddrawer);
    }
}
