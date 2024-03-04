package xueluoanping.fluiddrawerslegacy.data.loot;



import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;


//  I inherited DTLootTableProvider, but many of its functions are private,
//  so I have to copy them to facilitate modification.

public class DTFTLootTableProvider extends LootTableProvider {

    private final PackOutput generator;
    private final String modId;
    private final ExistingFileHelper existingFileHelper;

    public DTFTLootTableProvider(PackOutput generator, String modId, ExistingFileHelper existingFileHelper) {
        super(generator,Set.of(), List.of());
        this.generator = generator;
        this.modId = modId;
        this.existingFileHelper = existingFileHelper;
    }

    // The reason why these functions appear is that
    // the loot table of the leaves block needs to be overwritten.

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {

        return super.run(cache);
    }

}
