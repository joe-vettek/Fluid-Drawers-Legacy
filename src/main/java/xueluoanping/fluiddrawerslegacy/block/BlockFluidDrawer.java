package xueluoanping.fluiddrawerslegacy.block;

import com.jaquadro.minecraft.storagedrawers.api.storage.EmptyDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributesModifiable;
import com.jaquadro.minecraft.storagedrawers.api.storage.INetworked;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
import com.jaquadro.minecraft.storagedrawers.capabilities.CapabilityDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.config.CommonConfig;
import com.jaquadro.minecraft.storagedrawers.core.ModItems;
import com.jaquadro.minecraft.storagedrawers.item.ItemUpgrade;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import xueluoanping.fluiddrawerslegacy.ModContents;
import xueluoanping.fluiddrawerslegacy.block.blockentity.BlockEntityFluidDrawer;
import xueluoanping.fluiddrawerslegacy.client.gui.ContainerFluiDrawer;
import xueluoanping.fluiddrawerslegacy.compat.ModHandlerManager;


import javax.annotation.Nullable;
import java.util.EnumSet;


public class BlockFluidDrawer extends HorizontalDirectionalBlock implements INetworked, EntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public static final VoxelShape center = Block.box(1, 1, 1, 15, 15, 15);
    public static final VoxelShape base = Block.box(0, 0, 0, 16, 1, 16);
    public static final VoxelShape column1 = Block.box(0, 1, 0, 1, 15, 1);
    public static final VoxelShape column2 = Block.box(15, 1, 0, 16, 15, 1);
    public static final VoxelShape column3 = Block.box(0, 1, 15, 1, 15, 16);
    public static final VoxelShape column4 = Block.box(15, 1, 15, 16, 15, 16);
    public static final VoxelShape top = Block.box(0, 15, 0, 16, 16, 16);


    //    public FluidDrawer(int drawerCount, boolean halfDepth, int storageUnits, Properties properties) {
    //        super(properties);
    //    }
    private final int slotCount;
    public BlockFluidDrawer(Properties properties, int slotCount) {
        super(properties);
        this.slotCount = slotCount;
    }

    // Add all the properties here, or may cause a null point exception.
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_49915_) {
        super.createBlockStateDefinition(p_49915_.add(FACING));
    }


    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return Shapes.or(center, base, column1, column2, column3, column4, top);
    }

    @Override
    public @NotNull InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {

        if (hit.getDirection() == Direction.UP || hit.getDirection() == Direction.DOWN)
            return InteractionResult.PASS;

        BlockEntity tileEntity = world.getBlockEntity(pos);
        // Must be a FluidDrawer
        if (tileEntity instanceof BlockEntityFluidDrawer tile) {

            ItemStack heldStack = player.getItemInHand(hand);
            ItemStack offhandStack = player.getOffhandItem();
            // open GUI when squat
            if (heldStack.isEmpty() && player.isShiftKeyDown()) {
                if (CommonConfig.GENERAL.enableUI.get() && !world.isClientSide()) {
                    //                    FluidDrawersLegacyMod.logger("hello，screen");
                    NetworkHooks.openScreen((ServerPlayer) player, new MenuProvider() {
                        @Override
                        public Component getDisplayName() {
                            return Component.translatable("gui.fluiddrawerslegacy.tittle");
                        }
                        @Nullable
                        @Override
                        public AbstractContainerMenu createMenu(int windowId, Inventory playerInv, Player playerEntity) {

                            return new ContainerFluiDrawer(ModContents.containerType.get(), windowId, playerInv, tile);
                        }
                    }, extraData -> {
                        extraData.writeBlockPos(pos);
                    });
                    return InteractionResult.SUCCESS;
                }
            }
            // insert upgrade
            else if (heldStack.getItem() instanceof ItemUpgrade) {
                if (tile.upgrades().canAddUpgrade(heldStack)) {
                    if (tile.upgrades().addUpgrade(heldStack)) {
                        if (!player.isCreative())
                            heldStack.shrink(1);
                        return InteractionResult.SUCCESS;
                    } else if (!world.isClientSide()) {
                        player.displayClientMessage(Component.translatable("message.storagedrawers.max_upgrades"), true);
                    }

                } else if (!world.isClientSide()) {
                    player.displayClientMessage(Component.translatable("message.storagedrawers.cannot_add_upgrade"), true);
                }
            }
            // need an empty left hand
            else if (offhandStack == ItemStack.EMPTY) {
                if (ModHandlerManager.tryHandleByMod(tile, player, hand))
                    return InteractionResult.SUCCESS;
                else if (FluidUtil.interactWithFluidHandler(player, hand, tile.getTank())) {
                    return InteractionResult.SUCCESS;
                }
            }
        }

        return InteractionResult.PASS;
    }


    @Override
    public RenderShape getRenderShape(BlockState p_149645_1_) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        ItemStack stack = asItem().getDefaultInstance();
        BlockEntity tileEntity = level.getBlockEntity(pos);
        if (tileEntity instanceof BlockEntityFluidDrawer tile) {
            tile.writePortable(stack.getOrCreateTag());
        }
        return stack;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
        BlockEntity tileEntity = level.getBlockEntity(pos);
        if (tileEntity instanceof BlockEntityFluidDrawer tile) {
            tile.readPortable(stack.getOrCreateTag());
        }
        super.setPlacedBy(level, pos, state, entity, stack);
    }


    @Override
    public void destroy(LevelAccessor level, BlockPos pos, BlockState state) {
        super.destroy(level, pos, state);
        level.playSound(null, pos, Fluids.WATER.getFluidType().getSound(SoundActions.BUCKET_EMPTY), SoundSource.BLOCKS, 1.0F, 1.0F);

    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }


    @Override
    public int getSignal(BlockState state, BlockGetter blockAccess, BlockPos pos, Direction side) {
        if (!this.isSignalSource(state) || !(blockAccess.getBlockEntity(pos) instanceof BlockEntityFluidDrawer)) {
            return 0;
        } else {
            BlockEntityFluidDrawer tile = (BlockEntityFluidDrawer) blockAccess.getBlockEntity(pos);
            //            FluidDrawersLegacyMod.logger("get"+tile.isRedstone()+tile.getRedstoneLevel() +tile.upgrades().serializeNBT());
            return tile != null && tile.isRedstone() ? tile.getRedstoneLevel() : 0;
        }
    }

    @Override
    public int getDirectSignal(BlockState state, BlockGetter worldIn, BlockPos pos, Direction side) {
        return side == Direction.UP ? this.getSignal(state, worldIn, pos, side) : 0;
    }



    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BlockEntityFluidDrawer(getSlotCount(),pos, state);
    }

    private int getSlotCount() {
        return this.slotCount;
    }
}
