package xueluoanping.fluiddrawerslegacy.block;

import com.jaquadro.minecraft.storagedrawers.api.storage.INetworked;
import com.jaquadro.minecraft.storagedrawers.config.CommonConfig;
import com.jaquadro.minecraft.storagedrawers.item.ItemUpgrade;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import xueluoanping.fluiddrawerslegacy.FluidDrawersLegacyMod;
import xueluoanping.fluiddrawerslegacy.ModContents;
import xueluoanping.fluiddrawerslegacy.block.blockentity.BlockEntityFluidDrawer;
import xueluoanping.fluiddrawerslegacy.client.gui.ContainerFluiDrawer;
import xueluoanping.fluiddrawerslegacy.compat.ModHandlerManager;
import xueluoanping.fluiddrawerslegacy.config.General;
import xueluoanping.fluiddrawerslegacy.util.MathUtils;


import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;


public class BlockFluidDrawer extends HorizontalDirectionalBlock implements INetworked, EntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;


    private static final VoxelShape shape;
    private static final VoxelShape shapef;
    public final Map<Direction, VoxelShape> getShape;


    static {
        var base = Block.box(0, 0, 1, 16, 16, 16);
        var column1 = Block.box(0, 0, 0, 1, 16, 1);
        var column2 = column1.move(15 / 16f, 0, 0);
        var column3 = Block.box(1, 0, 0, 15, 1, 1);
        var column4 = column3.move(0, 15 / 16f, 0);
        var column = Shapes.or(column1, column2, column3, column4);
        shape = Shapes.or(base, column);

        var basef = Block.box(0, 0, 9, 16, 16, 16);
        shapef = Shapes.or(basef, column.move(0, 0, 8 / 16f));
    }


    //    public FluidDrawer(int drawerCount, boolean halfDepth, int storageUnits, Properties properties) {
    //        super(properties);
    //    }
    private final int slotCount;
    private final boolean half;

    public BlockFluidDrawer(Properties properties, int slotCount, boolean half) {
        super(properties);
        this.slotCount = slotCount;
        this.half = half;
        getShape = new HashMap<>() {{
            var s = isHalf() ? shapef : shape;
            put(Direction.EAST, MathUtils.getShapefromAngle(s, 270));
            put(Direction.SOUTH, MathUtils.getShapefromAngle(s, 180));
            put(Direction.WEST, MathUtils.getShapefromAngle(s, 90));
            put(Direction.NORTH, s);
        }};
    }

    private boolean isHalf() {
        return this.half;
    }

    // Add all the properties here, or may cause a null point exception.
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_49915_) {
        super.createBlockStateDefinition(p_49915_.add(FACING));
    }


    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return getShape.get(state.getValue(FACING));
    }


    @Override
    public @NotNull InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        var facing = state.getValue(FACING);
        var playerFrom = hit.getDirection();

        if (playerFrom == Direction.UP || playerFrom == Direction.DOWN) return InteractionResult.PASS;
        if (isHalf() && playerFrom != facing) return InteractionResult.PASS;


        BlockEntity tileEntity = world.getBlockEntity(pos);
        // Must be a FluidDrawer
        if (tileEntity instanceof BlockEntityFluidDrawer tile) {

            ItemStack heldStack = player.getItemInHand(hand);
            ItemStack offhandStack = player.getOffhandItem();
            // open GUI when squat

            if (facing == playerFrom && heldStack.isEmpty() && player.isShiftKeyDown()) {
                if (CommonConfig.GENERAL.enableUI.get() && !world.isClientSide()) {
                    //                    FluidDrawersLegacyMod.logger("hello，screen");
                    NetworkHooks.openScreen((ServerPlayer) player, new MenuProvider() {
                        @Override
                        public Component getDisplayName() {
                            // return Component.translatable("gui.fluiddrawerslegacy.tittle");
                            return BlockFluidDrawer.this.getName();
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
            else if (facing == playerFrom && heldStack.getItem() instanceof ItemUpgrade) {
                if (tile.upgrades().canAddUpgrade(heldStack)) {
                    if (tile.upgrades().addUpgrade(heldStack)) {
                        if (!player.isCreative()) heldStack.shrink(1);
                        return InteractionResult.SUCCESS;
                    } else if (!world.isClientSide()) {
                        player.displayClientMessage(Component.translatable("message.storagedrawers.max_upgrades"), true);
                    }

                } else if (!world.isClientSide()) {
                    player.displayClientMessage(Component.translatable("message.storagedrawers.cannot_add_upgrade"), true);
                }
            }
            // need an empty left hand
            else if (offhandStack == ItemStack.EMPTY && !player.isShiftKeyDown()) {
                // Case 1: From equal Facing and  then all can get
                // Case 2: From can be nearst last from Facing then get 2,4 or 1,2 or 1
                // Case default: get 1,3 or 1,2 or 1

                // use it we can get a facing always without z
                var loc = hit.getLocation().add(-pos.getX(), -pos.getY(), -pos.getZ());

                int tankSlot = getSlotByVec(loc, facing, playerFrom, getSlotCount());
                IFluidHandler tank = tankSlot == -1 ?
                        tile.getTank() :
                        (IFluidHandler) tile.getDrawer(tankSlot).getTank();

                if (ModHandlerManager.tryHandleByMod(tank, player, hand))
                    return InteractionResult.SUCCESS;
                else if (FluidUtil.interactWithFluidHandler(player, hand, tank)) {
                    return InteractionResult.SUCCESS;
                } else if (ModHandlerManager.mayConsume(player, hand)) {
                    return InteractionResult.CONSUME;
                }
            }
        }

        return InteractionResult.PASS;
    }

    public static int getSlotByVec(Vec3 loc, Direction facing, Direction playerFrom, int slotCount) {
        int tankSlot = 0;
        if (slotCount == 2) {
            tankSlot = loc.y() > 0.5 ? 0 : 1;
        } else if (slotCount == 4) {
            int angle = (int) (facing.toYRot() - playerFrom.toYRot());
            FluidDrawersLegacyMod.logger(angle, facing.toYRot());
            if (angle == 0) {
                var p = new MathUtils.Point(loc);
                p = MathUtils.Point.rotatePoint(p, facing.toYRot());
                int xo = (int) (Math.floor(p.x / 0.5f) + 1);
                int yo = (int) (1 - Math.floor(p.y / 0.5f));
                yo = yo == 0 ? 0 : 2;
                tankSlot = xo + yo - 1;
            } else if (angle == 90 || angle == -270) {
                tankSlot = loc.y() > 0.5 ? 1 : 3;
            } else {
                tankSlot = loc.y() > 0.5 ? 0 : 2;
            }
        }

        //  to avoid error
        tankSlot = tankSlot < 0 || tankSlot >= slotCount ? -1 : tankSlot;

        return tankSlot;
    }


    @Override
    public RenderShape getRenderShape(BlockState p_149645_1_) {
        return RenderShape.MODEL;
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
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player playerEntity) {
        super.playerWillDestroy(level, pos, state, playerEntity);
        if (level instanceof ServerLevel) {
            if (!General.retainFluid.get() && level.getBlockEntity(pos) instanceof BlockEntityFluidDrawer blockEntityFluidDrawer) {
                blockEntityFluidDrawer.getCapability(ForgeCapabilities.FLUID_HANDLER).ifPresent(iFluidHandler -> {
                    for (int i = 0; i < iFluidHandler.getTanks(); i++) {
                        iFluidHandler.drain(iFluidHandler.getFluidInTank(i), IFluidHandler.FluidAction.EXECUTE);
                    }
                });
            }
        }
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
        return new BlockEntityFluidDrawer(getSlotCount(), pos, state);
    }

    private int getSlotCount() {
        return this.slotCount;
    }
}
