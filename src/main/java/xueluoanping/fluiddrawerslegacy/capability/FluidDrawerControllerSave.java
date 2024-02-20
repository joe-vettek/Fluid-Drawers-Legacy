package xueluoanping.fluiddrawerslegacy.capability;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class FluidDrawerControllerSave extends SavedData {

    // public static FluidDrawerControllerSave fluidDrawerControllerSave;
    private final Map<BlockPos, FluidStack> chunkPosData = new HashMap<>();

    public FluidDrawerControllerSave() {
    }

    public FluidDrawerControllerSave(CompoundTag tag) {
        ListTag list = tag.getList("fluid", Tag.TAG_COMPOUND);
        for (Tag t : list) {
            CompoundTag manaTag = (CompoundTag) t;
            BlockPos chunkPos = new BlockPos(manaTag.getInt("x"), manaTag.getInt("y"), manaTag.getInt("z"));
            chunkPosData.put(chunkPos, FluidStack.loadFluidStackFromNBT(manaTag));
        }
    }

    public void update(BlockPos blockPos, FluidStack fluid) {
        // if(fluidDrawerControllerSave!=null){
        chunkPosData.put(blockPos, fluid);
        setDirty();
        // }else fluidDrawerControllerSave=new FluidDrawerControllerSave();
    }

    public FluidStack get(BlockPos blockPos) {
        return chunkPosData.getOrDefault(blockPos, FluidStack.EMPTY);
    }

    public void remove(BlockPos blockPos) {
        chunkPosData.remove(blockPos);
        setDirty();
    }

    @Override
    public @NotNull CompoundTag save(CompoundTag tag) {
        ListTag list = new ListTag();
        chunkPosData.forEach((chunkPos, mana) -> {
            CompoundTag manaTag = new CompoundTag();
            manaTag.putInt("x", chunkPos.getX());
            manaTag.putInt("y", chunkPos.getY());
            manaTag.putInt("z", chunkPos.getZ());
            mana.writeToNBT(manaTag);
            list.add(manaTag);
        });
        tag.put("fluid", list);
        return tag;
    }

    public static FluidDrawerControllerSave get(Level worldIn) {
        if (!(worldIn instanceof ServerLevel)) {
            throw new RuntimeException("Attempted to get the data from a client world. This is wrong.");
        }
        // ServerLevel world = worldIn.getServer().getLevel(Level.OVERWORLD);
        ServerLevel world = (ServerLevel) worldIn;
        /***
         *   如果你需要每个纬度都有一个自己的World Saved Data。
         *  用 ServerWorld world = (ServerWorld)world; 代替上面那句。
         */
        DimensionDataStorage storage = world.getDataStorage();
        return storage.computeIfAbsent(FluidDrawerControllerSave::new, FluidDrawerControllerSave::new, "fluid");
    }


}
