package lexwomy.calcitevariants;

import java.util.function.Function;
import lexwomy.verticalslabs.VerticalSlabs;
import lexwomy.verticalslabs.block.VerticalSlab;
import lexwomy.verticalslabs.block.VerticalSlabBlock;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.loader.api.FabricLoader;
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
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CalciteVariants implements ModInitializer {
  public static final String MOD_ID = "calcite_variants";
  public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
  public static final Block CALCITE_SLAB =
      register(
          "calcite_slab", SlabBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.CALCITE));
  public static final Block CALCITE_STAIRS =
      register(
          "calcite_stairs",
          (properties) -> new StairBlock(Blocks.CALCITE.defaultBlockState(), properties),
          BlockBehaviour.Properties.ofFullCopy(Blocks.CALCITE));
  public static final Block CALCITE_WALL =
      register(
          "calcite_wall", WallBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.CALCITE));
  public static boolean hasVerticalSlabDependency;
  @Nullable public static Block CALCITE_VERTICAL_SLAB = null;

  private static Block register(
      String name,
      Function<BlockBehaviour.Properties, Block> blockFactory,
      BlockBehaviour.Properties settings) {
    ResourceKey<Block> blockKey = keyOfBlock(name);
    Block block = blockFactory.apply(settings.setId(blockKey));

    ResourceKey<Item> itemKey = keyOfItem(name);

    BlockItem blockItem =
        new BlockItem(block, new Item.Properties().setId(itemKey).useBlockDescriptionPrefix());
    Registry.register(BuiltInRegistries.ITEM, itemKey, blockItem);

    return Registry.register(BuiltInRegistries.BLOCK, blockKey, block);
  }

  private static ResourceKey<Block> keyOfBlock(String name) {
    return ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(MOD_ID, name));
  }

  private static ResourceKey<Item> keyOfItem(String name) {
    return ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(MOD_ID, name));
  }

  @Override
  public void onInitialize() {
    // This code runs as soon as Minecraft is in a mod-load-ready state.
    // However, some things (like resources) may still be uninitialized.
    // Proceed with mild caution.
    hasVerticalSlabDependency = FabricLoader.getInstance().isModLoaded(VerticalSlabs.MOD_ID);
    if (hasVerticalSlabDependency) {
      CALCITE_VERTICAL_SLAB =
          register(
              "calcite_vertical_slab",
              VerticalSlabBlock::new,
              BlockBehaviour.Properties.ofFullCopy(Blocks.CALCITE));
    } else {
      LOGGER.info("Vertical slab mod dependency not found, skipping calcite vertical slabs");
    }

    ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.BUILDING_BLOCKS)
        .register(
            (itemGroup) -> {
              itemGroup.accept(CALCITE_SLAB.asItem());
              itemGroup.accept(CALCITE_STAIRS.asItem());
              itemGroup.accept(CALCITE_WALL.asItem());
              if (CALCITE_VERTICAL_SLAB != null) {
                itemGroup.accept(CALCITE_VERTICAL_SLAB.asItem());
              }
            });
    if (CALCITE_VERTICAL_SLAB != null) {
      ItemGroupEvents.modifyEntriesEvent(VerticalSlab.VERTICAL_SLAB_GROUP_KEY)
          .register(
              (itemGroup) -> {
                itemGroup.accept(CALCITE_VERTICAL_SLAB);
              });
    }
  }
}
