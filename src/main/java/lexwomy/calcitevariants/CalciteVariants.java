package lexwomy.calcitevariants;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

public class CalciteVariants implements ModInitializer {
	public static final String MOD_ID = "calcite_variants";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static Block register(String name, Function<BlockBehaviour.Properties, Block> blockFactory, BlockBehaviour.Properties settings, boolean shouldRegisterItem) {
        ResourceKey<Block> blockKey = keyOfBlock(name);
        Block block = blockFactory.apply(settings.setId(blockKey));

        if (shouldRegisterItem) {
            ResourceKey<Item> itemKey = keyOfItem(name);

            BlockItem blockItem = new BlockItem(block, new Item.Properties().setId(itemKey).useBlockDescriptionPrefix());
            Registry.register(BuiltInRegistries.ITEM, itemKey, blockItem);
        }

        return Registry.register(BuiltInRegistries.BLOCK, blockKey, block);
    }

    private static ResourceKey<Block> keyOfBlock(String name) {
        return ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(MOD_ID, name));
    }

    private static ResourceKey<Item> keyOfItem(String name) {
        return ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(MOD_ID, name));
    }

    public static final Block CALCITE_SLAB = register("calcite_slab", SlabBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.CALCITE), true);
    public static final Block CALCITE_STAIRS = register("calcite_stairs", (properties) -> new StairBlock(Blocks.CALCITE.defaultBlockState(), properties), BlockBehaviour.Properties.ofFullCopy(Blocks.CALCITE), true);
    public static final Block CALCITE_WALL = register("calcite_wall", WallBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.CALCITE), true);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.BUILDING_BLOCKS).register((itemGroup) -> {
            itemGroup.accept(CALCITE_SLAB.asItem());
            itemGroup.accept(CALCITE_STAIRS.asItem());
            itemGroup.accept(CALCITE_WALL.asItem());
        });
	}
}