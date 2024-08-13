package xueluoanping.fluiddrawerslegacy.capability;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import xueluoanping.fluiddrawerslegacy.FluidDrawersLegacyMod;

import java.util.HashMap;
import java.util.Map;

public class FluidDrawerControllerSave extends SavedData {

    // public static FluidDrawerControllerSave fluidDrawerControllerSave;
    private final Map<BlockPos, FluidStack> chunkPosData = new HashMap<>();

    public FluidDrawerControllerSave() {
    }


    public FluidDrawerControllerSave(HolderLookup.Provider provider,CompoundTag tag) {
        ListTag list = tag.getList("fluid", Tag.TAG_COMPOUND);
        for (Tag t : list) {
            CompoundTag manaTag = (CompoundTag) t;
            BlockPos chunkPos = new BlockPos(manaTag.getInt("x"), manaTag.getInt("y"), manaTag.getInt("z"));
            chunkPosData.put(chunkPos, FluidStack.parseOptional(provider,manaTag));
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
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider pRegistries) {
        ListTag list = new ListTag();
        chunkPosData.forEach((chunkPos, fluidStack) -> {
            CompoundTag fluidPosTag = new CompoundTag();
            fluidPosTag.putInt("x", chunkPos.getX());
            fluidPosTag.putInt("y", chunkPos.getY());
            fluidPosTag.putInt("z", chunkPos.getZ());
            fluidStack.save(pRegistries,fluidPosTag);
            list.add(fluidPosTag);
        });
        tag.put("fluid", list);
        return tag;
    }


    public static FluidDrawerControllerSave get(ServerLevel serverLevel) {
        DimensionDataStorage storage = serverLevel.getDataStorage();
        return storage.computeIfAbsent(
                new Factory<>(() -> create(serverLevel),
                        ((compoundTag, provider) -> load(serverLevel, compoundTag, provider))),
                FluidDrawersLegacyMod.MOD_ID);
    }

    private static FluidDrawerControllerSave load(ServerLevel serverLevel, CompoundTag compoundTag, HolderLookup.Provider provider) {
        return new FluidDrawerControllerSave(serverLevel.registryAccess(),compoundTag);
    }

    private static FluidDrawerControllerSave create(ServerLevel serverLevel) {
        return new FluidDrawerControllerSave();
    }
}
