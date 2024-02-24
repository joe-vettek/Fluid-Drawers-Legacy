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
// import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
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
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
// import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import xueluoanping.fluiddrawerslegacy.ModContents;
import xueluoanping.fluiddrawerslegacy.block.blockentity.BlockEntityFluidDrawer;
import xueluoanping.fluiddrawerslegacy.client.gui.ContainerFluiDrawer;
import xueluoanping.fluiddrawerslegacy.config.General;

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

    public BlockFluidDrawer(Properties properties) {
        super(properties);
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

        // FluidDrawersLegacyMod.logger("ss2s22");
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
                // deal with bucket
                // FluidDrawersLegacyMod.logger("0"+heldStack.getItem()+(heldStack.getItem() instanceof BucketItem ));
                // System.out.println(FluidUtil.interactWithFluidHandler(player, hand, tile.getTank()));
                if (heldStack.getItem() instanceof BucketItem bucketItem) {
                    if (bucketItem.getFluid() == Fluids.EMPTY
                            && tile.getTankFLuid().getAmount() >= FluidType.BUCKET_VOLUME) {
                        tile.getCapability(ForgeCapabilities.FLUID_HANDLER, Direction.DOWN)
                                .ifPresent(handler -> {
                                    FluidStack fluidStack = handler.drain(new FluidStack(tile.getTankFLuid().getFluid(), FluidType.BUCKET_VOLUME), IFluidHandler.FluidAction.EXECUTE);
                                    Fluid fluid = fluidStack.getFluid();

                                    if (heldStack.getCount() > 1) {
                                        if (!player.addItem(new ItemStack(fluid.getBucket())))

                                            Containers.dropItemStack(world, player.getX(), player.getY(), player.getZ(), new ItemStack(fluid.getBucket()));
                                        if (!player.isCreative())
                                            heldStack.shrink(1);
                                    } else {
                                        if (!player.isCreative()) {
                                            player.setItemInHand(hand, ItemUtils.createFilledResult(heldStack, player, new ItemStack(fluid.getBucket())));
                                        } else {
                                            //                                            player.addItem(new ItemStack(fluid.getBucket()));
                                        }
                                    }
                                });
                    }
                    else if (tile.hasNoFluid()) {
                        if (bucketItem.getFluid() == Fluids.EMPTY)
                            return InteractionResult.FAIL;
                        else {
                            tile.getCapability(ForgeCapabilities.FLUID_HANDLER, Direction.DOWN)
                                    .ifPresent(handler -> {
                                        int amount = handler.fill(new FluidStack(bucketItem.getFluid(), FluidType.BUCKET_VOLUME), IFluidHandler.FluidAction.SIMULATE);
                                        if (amount == FluidType.BUCKET_VOLUME) {
                                            handler.fill(new FluidStack(bucketItem.getFluid(), FluidType.BUCKET_VOLUME), IFluidHandler.FluidAction.EXECUTE);
                                            if (!player.isCreative())
                                                player.setItemInHand(hand, heldStack.getCraftingRemainingItem());
                                        }

                                    });
                            return InteractionResult.SUCCESS;
                        }
                    }
                    else {
                        if (tile.getTankFLuid().getAmount() + FluidType.BUCKET_VOLUME <= tile.getTankEffectiveCapacity()
                                && tile.getTankFLuid().getFluid() == bucketItem.getFluid()) {
                            tile.getCapability(ForgeCapabilities.FLUID_HANDLER, Direction.DOWN)
                                    .ifPresent(handler -> {
                                        handler.fill(new FluidStack(bucketItem.getFluid(), FluidType.BUCKET_VOLUME), IFluidHandler.FluidAction.EXECUTE);

                                    });
                            if (!player.isCreative())
                                player.setItemInHand(hand, heldStack.getCraftingRemainingItem());
                            return InteractionResult.SUCCESS;
                        }
                    }
                    return InteractionResult.SUCCESS;
                }
                // Maybe so simple
                else if (FluidUtil.interactWithFluidHandler(player, hand, tile.getTank())) {
                    // System.out.println(FluidUtil.interactWithFluidHandler(player, hand, tile.getTank()));
                    return InteractionResult.SUCCESS;
                } else if (heldStack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).isPresent()) {
                    heldStack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM)
                            .ifPresent((handler) -> {

                                if (tile.hasNoFluid()) {
                                    if (tile.getTankEffectiveCapacity() > handler.getTankCapacity(0)) {
                                        tile.getCapability(ForgeCapabilities.FLUID_HANDLER, Direction.DOWN)
                                                .ifPresent(TEhandler -> {
                                                    TEhandler.fill(handler.drain(handler.getTankCapacity(0), IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
                                                });

                                    } else {
                                        tile.getCapability(ForgeCapabilities.FLUID_HANDLER, Direction.DOWN)
                                                .ifPresent(TEhandler -> {
                                                    TEhandler.fill(handler.drain(tile.getTankEffectiveCapacity(), IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
                                                });
                                    }
                                } else if (tile.getTankFLuid().getFluid() == handler.drain(1, IFluidHandler.FluidAction.SIMULATE).getFluid()) {
                                    if (tile.getTankEffectiveCapacity() < handler.getTankCapacity(0) + tile.getTankFLuid().getAmount()) {
                                        tile.getCapability(ForgeCapabilities.FLUID_HANDLER, Direction.DOWN)
                                                .ifPresent(TEhandler -> {
                                                    TEhandler.fill(handler.drain(handler.getTankCapacity(0), IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
                                                    //                                               if(!player.isCreative())

                                                });
                                    } else {
                                        tile.getCapability(ForgeCapabilities.FLUID_HANDLER, Direction.DOWN)
                                                .ifPresent(TEhandler -> {
                                                    TEhandler.fill(handler.drain(tile.getTankEffectiveCapacity() - tile.getTankFLuid().getAmount(), IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
                                                });
                                    }
                                }

                            });
                    return InteractionResult.SUCCESS;
                }

            }


        }
        return InteractionResult.PASS;
    }


    @Override
    public RenderShape getRenderShape(BlockState p_149645_1_) {
        return RenderShape.MODEL;
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        ItemStack stack = ModContents.itemBlock.get().getDefaultInstance();
        BlockEntity tileEntity = level.getBlockEntity(pos);
        if (tileEntity instanceof BlockEntityFluidDrawer) {
            BlockEntityFluidDrawer tile = (BlockEntityFluidDrawer) tileEntity;
            final FluidStack[] fluidStackDown = new FluidStack[1];
            tile.getCapability(ForgeCapabilities.FLUID_HANDLER, Direction.DOWN)
                    .ifPresent(handler -> {
                        fluidStackDown[0] = handler.getFluidInTank(0);
                        CompoundTag nbt = new CompoundTag();
                        handler.getFluidInTank(0).writeToNBT(nbt);
                        stack.addTagElement("tank", ((BlockEntityFluidDrawer.betterFluidHandler) handler).serializeNBT());

                    });
            stack.addTagElement("Upgrades", tile.getUpdateTag().get("Upgrades"));


            EnumSet<LockAttribute> attrs = EnumSet.noneOf(LockAttribute.class);
            if (((IDrawerAttributesModifiable) tile.getDrawerAttributes()).isItemLocked(LockAttribute.LOCK_EMPTY))
                attrs.add(LockAttribute.LOCK_EMPTY);
            if (((IDrawerAttributesModifiable) tile.getDrawerAttributes()).isItemLocked(LockAttribute.LOCK_POPULATED))
                attrs.add(LockAttribute.LOCK_POPULATED);
            if (!attrs.isEmpty()) {

                stack.getOrCreateTag().putByte("Lock", (byte) LockAttribute.getBitfield(attrs));
            }

            if (((IDrawerAttributesModifiable) tile.getDrawerAttributes()).isConcealed())
                stack.getOrCreateTag().putBoolean("Shr", true);

            if (((IDrawerAttributesModifiable) tile.getDrawerAttributes()).isShowingQuantity())
                stack.getOrCreateTag().putBoolean("Qua", true);

        }
        return stack;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return (BlockState) this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
        BlockEntity tileEntity = level.getBlockEntity(pos);
        if (tileEntity instanceof BlockEntityFluidDrawer &&
                stack.hasTag()) {
            BlockEntityFluidDrawer tile = (BlockEntityFluidDrawer) tileEntity;

            if (stack.getTag().contains("Upgrades")) {
                CompoundTag nbt = new CompoundTag();
                nbt.put("Upgrades", stack.getTag().get("Upgrades"));
                tile.upgrades().read(nbt);
            }
            CompoundTag tag = stack.getOrCreateTag();
            if (tag.contains("Lock")) {
                EnumSet<LockAttribute> attrs = LockAttribute.getEnumSet(tag.getByte("Lock"));
                if (attrs != null) {
                    ((IDrawerAttributesModifiable) tile.getDrawerAttributes()).setItemLocked(LockAttribute.LOCK_EMPTY, attrs.contains(LockAttribute.LOCK_EMPTY));
                    ((IDrawerAttributesModifiable) tile.getDrawerAttributes()).setItemLocked(LockAttribute.LOCK_POPULATED, attrs.contains(LockAttribute.LOCK_POPULATED));
                }
            } else {
                ((IDrawerAttributesModifiable) tile.getDrawerAttributes()).setItemLocked(LockAttribute.LOCK_EMPTY, false);
                ((IDrawerAttributesModifiable) tile.getDrawerAttributes()).setItemLocked(LockAttribute.LOCK_POPULATED, false);
            }
            if (stack.getTag().contains("Shr")) {
                ((IDrawerAttributesModifiable) tile.getDrawerAttributes()).setIsConcealed(tag.getBoolean("Shr"));
            } else {
                ((IDrawerAttributesModifiable) tile.getDrawerAttributes()).setIsConcealed(false);
            }
            if (stack.getTag().contains("Qua")) {
                ((IDrawerAttributesModifiable) tile.getDrawerAttributes()).setIsShowingQuantity(tag.getBoolean("Qua"));
            } else {
                ((IDrawerAttributesModifiable) tile.getDrawerAttributes()).setIsShowingQuantity(false);
            }
            if (entity != null && entity.getOffhandItem().getItem() == ModItems.DRAWER_KEY.get()) {
                IDrawerAttributes _attrs = (IDrawerAttributes) tile.getCapability(CapabilityDrawerAttributes.DRAWER_ATTRIBUTES_CAPABILITY).orElse(new EmptyDrawerAttributes());
                if (_attrs instanceof IDrawerAttributesModifiable) {
                    IDrawerAttributesModifiable attrs = (IDrawerAttributesModifiable) _attrs;
                    attrs.setItemLocked(LockAttribute.LOCK_EMPTY, true);
                    attrs.setItemLocked(LockAttribute.LOCK_POPULATED, true);

                }
            }
            if (stack.getOrCreateTag().contains("tank")) {
                tile.setCutStartAnimation(true);
                BlockEntityFluidDrawer.betterFluidHandler tank = (BlockEntityFluidDrawer.betterFluidHandler) tile.getTank();
                tank.deserializeNBT((CompoundTag) stack.getOrCreateTag().get("tank"));
            }
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

    @org.jetbrains.annotations.Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BlockEntityFluidDrawer(pos, state);
    }
}
