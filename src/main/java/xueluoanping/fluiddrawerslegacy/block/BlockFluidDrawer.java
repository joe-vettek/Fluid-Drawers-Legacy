package xueluoanping.fluiddrawerslegacy.block;

import com.jaquadro.minecraft.storagedrawers.api.storage.EmptyDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributesModifiable;
import com.jaquadro.minecraft.storagedrawers.api.storage.INetworked;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.capabilities.CapabilityDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.config.CommonConfig;
import com.jaquadro.minecraft.storagedrawers.core.ModItems;
import com.jaquadro.minecraft.storagedrawers.item.ItemQuantifyKey;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.network.NetworkHooks;
import xueluoanping.fluiddrawerslegacy.FluidDrawersLegacyMod;
import xueluoanping.fluiddrawerslegacy.ModContents;
import xueluoanping.fluiddrawerslegacy.block.tileentity.TileEntityFluidDrawer;
import xueluoanping.fluiddrawerslegacy.client.gui.ContainerFluiDrawer;

import javax.annotation.Nullable;
import java.util.EnumSet;


public class BlockFluidDrawer extends HorizontalBlock implements INetworked {
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

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return VoxelShapes.or(center, base, column1, column2, column3, column4, top);
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
//        player.getFoodData().eat(2, 0.1F);
        if (hit.getDirection() == Direction.UP || hit.getDirection() == Direction.DOWN)
            return ActionResultType.PASS;

        TileEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof TileEntityFluidDrawer) {
            TileEntityFluidDrawer tile = (TileEntityFluidDrawer) tileEntity;
            ItemStack heldStack = player.getItemInHand(hand);
            ItemStack offhandStack = player.getOffhandItem();
//            FluidDrawersLegacyMod.logger("hello，screen" + world + player.isShiftKeyDown());
            if (heldStack.getItem() == ModItems.DRAWER_KEY) {

                IDrawerAttributes _attrs = tile.getCapability(CapabilityDrawerAttributes.DRAWER_ATTRIBUTES_CAPABILITY).orElse(new EmptyDrawerAttributes());
                if (_attrs instanceof IDrawerAttributesModifiable) {
//                    IDrawerAttributesModifiable attrs = (IDrawerAttributesModifiable) _attrs;
//                    attrs.setItemLocked(LockAttribute.LOCK_EMPTY, true);
//                    attrs.setItemLocked(LockAttribute.LOCK_POPULATED, true);
                    return ActionResultType.PASS;
                }

            }
            if (heldStack.getItem() == ModItems.QUANTIFY_KEY) {

                IDrawerAttributes _attrs = tile.getCapability(CapabilityDrawerAttributes.DRAWER_ATTRIBUTES_CAPABILITY).orElse(new EmptyDrawerAttributes());
                if (_attrs instanceof IDrawerAttributesModifiable) {
//                    IDrawerAttributesModifiable attrs = (IDrawerAttributesModifiable) _attrs;
//                    attrs.setIsShowingQuantity(!attrs.isShowingQuantity());
                    return ActionResultType.PASS;
                }

            }
            if (heldStack.getItem() == ModItems.SHROUD_KEY) {

                IDrawerAttributes _attrs = tile.getCapability(CapabilityDrawerAttributes.DRAWER_ATTRIBUTES_CAPABILITY).orElse(new EmptyDrawerAttributes());
                if (_attrs instanceof IDrawerAttributesModifiable) {
//                    IDrawerAttributesModifiable attrs = (IDrawerAttributesModifiable) _attrs;
//                    attrs.setIsSealed(!attrs.isSealed());
                    return ActionResultType.PASS;
                }

            }
            if (heldStack.isEmpty() && player.isShiftKeyDown()) {
                if (CommonConfig.GENERAL.enableUI.get() && !world.isClientSide()) {
//                    FluidDrawersLegacyMod.logger("hello，screen");
                    NetworkHooks.openGui((ServerPlayerEntity) player, new INamedContainerProvider() {
                        @Override
                        public ITextComponent getDisplayName() {
                            return new TranslationTextComponent("gui.fluiddrawerslegacy.tittle");
                        }

                        @Nullable
                        @Override
                        public Container createMenu(int windowId, PlayerInventory playerInv, PlayerEntity playerEntity) {

                            return new ContainerFluiDrawer(windowId, playerInv, tile);
                        }
                    }, extraData -> {
                        extraData.writeBlockPos(pos);
                    });
                    return ActionResultType.SUCCESS;
                }
            }
            if (offhandStack == ItemStack.EMPTY) {
                if (heldStack.getItem() instanceof BucketItem) {
                    BucketItem bucketItem = (BucketItem) heldStack.getItem();
                    if (bucketItem.getFluid() == Fluids.EMPTY && tile.getTankFLuid().getAmount() >= FluidAttributes.BUCKET_VOLUME) {
                        tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, Direction.DOWN)
                                .ifPresent(handler -> {
                                    FluidStack fluidStack = handler.drain(new FluidStack(tile.getTankFLuid().getFluid(), FluidAttributes.BUCKET_VOLUME), IFluidHandler.FluidAction.EXECUTE);
                                    Fluid fluid = fluidStack.getFluid();
                                    if (heldStack.getCount() > 1) {
                                        if (!player.addItem(new ItemStack(fluid.getBucket())))
                                            InventoryHelper.dropItemStack(world, player.getX(), player.getY(), player.getZ(), new ItemStack(fluid.getBucket()));
                                        if (!player.isCreative())
                                            heldStack.shrink(1);
                                    } else {
                                        if (!player.isCreative()) {
                                            player.setItemInHand(hand, DrinkHelper.createFilledResult(heldStack, player, new ItemStack(fluid.getBucket())));
                                        } else {
//                                            player.addItem(new ItemStack(fluid.getBucket()));
                                        }
                                    }
                                });
                    } else if (tile.hasNoFluid()) {
                        tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, Direction.DOWN)
                                .ifPresent(handler -> {
                                    handler.fill(new FluidStack(bucketItem.getFluid(), FluidAttributes.BUCKET_VOLUME), IFluidHandler.FluidAction.EXECUTE);
                                    if (!player.isCreative())
                                        player.setItemInHand(hand, heldStack.getContainerItem());
                                });
                        return ActionResultType.SUCCESS;
                    } else {
                        if (tile.getTankFLuid().getAmount() + FluidAttributes.BUCKET_VOLUME <= tile.getEffectiveCapacity()
                                && tile.getTankFLuid().getFluid() == bucketItem.getFluid()) {
                            tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, Direction.DOWN)
                                    .ifPresent(handler -> {
                                        handler.fill(new FluidStack(bucketItem.getFluid(), FluidAttributes.BUCKET_VOLUME), IFluidHandler.FluidAction.EXECUTE);

                                    });
                            if (!player.isCreative())
                                player.setItemInHand(hand, heldStack.getContainerItem());
                            return ActionResultType.SUCCESS;
                        }
                    }
                    return ActionResultType.SUCCESS;
                }
//                Maybe so simple
                else if (FluidUtil.interactWithFluidHandler(player, hand, tile.getTank())) {
                    return ActionResultType.SUCCESS;
                } else if (heldStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent()) {
                    heldStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY)
                            .ifPresent((handler) -> {

                                if (tile.hasNoFluid()) {
                                    if (tile.getEffectiveCapacity() > handler.getTankCapacity(0)) {
                                        tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, Direction.DOWN)
                                                .ifPresent(TEhandler -> {
                                                    TEhandler.fill(handler.drain(handler.getTankCapacity(0), IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
                                                });

                                    } else {
                                        tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, Direction.DOWN)
                                                .ifPresent(TEhandler -> {
                                                    TEhandler.fill(handler.drain(tile.getEffectiveCapacity(), IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
                                                });
                                    }
                                } else if (tile.getTankFLuid().getFluid() == handler.drain(1, IFluidHandler.FluidAction.SIMULATE).getFluid()) {
                                    if (tile.getEffectiveCapacity() < handler.getTankCapacity(0) + tile.getTankFLuid().getAmount()) {
                                        tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, Direction.DOWN)
                                                .ifPresent(TEhandler -> {
                                                    TEhandler.fill(handler.drain(handler.getTankCapacity(0), IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
//                                               if(!player.isCreative())

                                                });
                                    } else {
                                        tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, Direction.DOWN)
                                                .ifPresent(TEhandler -> {
                                                    TEhandler.fill(handler.drain(tile.getEffectiveCapacity() - tile.getTankFLuid().getAmount(), IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
                                                });
                                    }
                                }

                            });
                    return ActionResultType.SUCCESS;
                }

            }

        }


        return ActionResultType.PASS;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TileEntityFluidDrawer();
//        return null;
    }

    @Override
    public BlockRenderType getRenderShape(BlockState p_149645_1_) {
        return BlockRenderType.MODEL;
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader blockReader, BlockPos pos, PlayerEntity player) {
        ItemStack stack = ModContents.itemBlock.asItem().getDefaultInstance();
        TileEntity tileEntity = blockReader.getBlockEntity(pos);
        if (tileEntity instanceof TileEntityFluidDrawer) {
            TileEntityFluidDrawer tile = (TileEntityFluidDrawer) tileEntity;
            final FluidStack[] fluidStackDown = new FluidStack[1];
            tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, Direction.DOWN)
                    .ifPresent(handler -> {
                        fluidStackDown[0] = handler.getFluidInTank(0);
                        CompoundNBT nbt = new CompoundNBT();
                        handler.getFluidInTank(0).writeToNBT(nbt);
                        stack.addTagElement("tank", nbt);

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
    public ItemStack getCloneItemStack(IBlockReader blockReader, BlockPos pos, BlockState state) {
        ItemStack stack = ModContents.itemBlock.asItem().getDefaultInstance();
        TileEntity tileEntity = blockReader.getBlockEntity(pos);
        if (tileEntity instanceof TileEntityFluidDrawer) {
            TileEntityFluidDrawer tile = (TileEntityFluidDrawer) tileEntity;
            final FluidStack[] fluidStackDown = new FluidStack[1];
            tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, Direction.DOWN)
                    .ifPresent(handler -> {
                        fluidStackDown[0] = handler.getFluidInTank(0);
                        CompoundNBT nbt = new CompoundNBT();
                        handler.getFluidInTank(0).writeToNBT(nbt);

                        stack.addTagElement("tank", nbt);

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
//        return super.getCloneItemStack(blockReader, pos, state);
    }


    @Override
    public void setPlacedBy(World level, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
        TileEntity tileEntity = level.getBlockEntity(pos);
        if (tileEntity instanceof TileEntityFluidDrawer) {
            TileEntityFluidDrawer tile = (TileEntityFluidDrawer) tileEntity;

            if (stack.getTag().contains("Upgrades")) {
                CompoundNBT nbt = new CompoundNBT();
                nbt.put("Upgrades", stack.getTag().get("Upgrades"));
                tile.upgrades().read(nbt);
            }
            CompoundNBT tag = stack.getOrCreateTag();
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
            if (entity != null && entity.getOffhandItem().getItem() == ModItems.DRAWER_KEY) {
                IDrawerAttributes _attrs = (IDrawerAttributes) tile.getCapability(CapabilityDrawerAttributes.DRAWER_ATTRIBUTES_CAPABILITY).orElse(new EmptyDrawerAttributes());
                if (_attrs instanceof IDrawerAttributesModifiable) {
                    IDrawerAttributesModifiable attrs = (IDrawerAttributesModifiable) _attrs;
                    attrs.setItemLocked(LockAttribute.LOCK_EMPTY, true);
                    attrs.setItemLocked(LockAttribute.LOCK_POPULATED, true);

                }
            }
            if (stack.getOrCreateTag().contains("tank")) {
                TileEntityFluidDrawer.betterFluidHandler tank = (TileEntityFluidDrawer.betterFluidHandler) tile.getTank();
                tank.deserializeNBT((CompoundNBT) stack.getOrCreateTag().get("tank"));
            }
        }

        super.setPlacedBy(level, pos, state, entity, stack);
    }


    @Override
    public void destroy(IWorld p_176206_1_, BlockPos p_176206_2_, BlockState p_176206_3_) {
        super.destroy(p_176206_1_, p_176206_2_, p_176206_3_);
        p_176206_1_.playSound(null, p_176206_2_, Fluids.WATER.getAttributes().getEmptySound(), SoundCategory.BLOCKS, 1.0F, 1.0F);

    }

    public boolean isSignalSource(BlockState state) {
        return true;
    }

    public int getSignal(BlockState state, IBlockReader blockAccess, BlockPos pos, Direction side) {
        if (!this.isSignalSource(state)||!(blockAccess.getBlockEntity(pos) instanceof TileEntityFluidDrawer)) {
            return 0;
        } else {
            TileEntityFluidDrawer tile = (TileEntityFluidDrawer) blockAccess.getBlockEntity(pos);
//            FluidDrawersLegacyMod.logger("get"+tile.isRedstone()+tile.getRedstoneLevel() +tile.upgrades().serializeNBT());
            return tile != null && tile.isRedstone() ? tile.getRedstoneLevel() : 0;
        }
    }

    public int getDirectSignal(BlockState state, IBlockReader worldIn, BlockPos pos, Direction side) {
        return side == Direction.UP ? this.getSignal(state, worldIn, pos, side):0 ;
    }
}
