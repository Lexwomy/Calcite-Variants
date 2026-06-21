package lexwomy.calcitevariants;

import java.util.function.Function;
import lexwomy.calcitevariants.references.CalciteVariantsBlockItemIds;
import lexwomy.verticalslabs.VerticalSlabs;
import lexwomy.verticalslabs.block.VerticalSlab;
import lexwomy.verticalslabs.block.VerticalSlabBlock;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.references.BlockItemId;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockItemTagId;
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
          CalciteVariantsBlockItemIds.CALCITE_SLAB,
          SlabBlock::new,
          BlockBehaviour.Properties.ofFullCopy(Blocks.CALCITE),
          true);
  public static final Block CALCITE_STAIRS =
      register(
          CalciteVariantsBlockItemIds.CALCITE_STAIRS,
          (properties) -> new StairBlock(Blocks.CALCITE.defaultBlockState(), properties),
          BlockBehaviour.Properties.ofFullCopy(Blocks.CALCITE),
          true);
  public static final Block CALCITE_WALL =
      register(
          CalciteVariantsBlockItemIds.CALCITE_WALL,
          WallBlock::new,
          BlockBehaviour.Properties.ofFullCopy(Blocks.CALCITE),
          true);
  public static boolean hasVerticalSlabDependency;
  @Nullable public static Block VERTICAL_CALCITE_SLAB = null;

  public static BlockItemTagId createBlockItemTagId(final String path) {
    return BlockItemTagId.create(identifier(path), identifier(path));
  }

  public static BlockItemId createBlockItemId(final String name) {
    return BlockItemId.create(identifier(name), identifier(name));
  }

  public static Identifier identifier(String path) {
    return Identifier.fromNamespaceAndPath(MOD_ID, path);
  }

  private static Block register(
      BlockItemId name,
      Function<BlockBehaviour.Properties, Block> blockFactory,
      BlockBehaviour.Properties properties,
      boolean shouldRegisterItem) {
    Block block = blockFactory.apply(properties.setId(name.block()));

    if (shouldRegisterItem) {
      BlockItem blockItem =
          new BlockItem(
              block, new Item.Properties().setId(name.item()).useBlockDescriptionPrefix());
      Registry.register(BuiltInRegistries.ITEM, name.item(), blockItem);
    }

    return Registry.register(BuiltInRegistries.BLOCK, name.block(), block);
  }

  @Override
  public void onInitialize() {
    // This code runs as soon as Minecraft is in a mod-load-ready state.
    // However, some things (like resources) may still be uninitialized.
    // Proceed with mild caution.
    hasVerticalSlabDependency = FabricLoader.getInstance().isModLoaded(VerticalSlabs.MOD_ID);
    if (hasVerticalSlabDependency) {
      VERTICAL_CALCITE_SLAB =
          register(
              CalciteVariantsBlockItemIds.VERTICAL_CALCITE_SLAB,
              VerticalSlabBlock::new,
              BlockBehaviour.Properties.ofFullCopy(Blocks.CALCITE),
              true);
    } else {
      LOGGER.info("Vertical slab mod dependency not found, skipping calcite vertical slabs");
    }
    BuiltInRegistries.BLOCK.addAlias(
        identifier("calcite_vertical_slab"), identifier("vertical_calcite_slab"));
    BuiltInRegistries.ITEM.addAlias(
        identifier("calcite_vertical_slab"), identifier("vertical_calcite_slab"));
    CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.BUILDING_BLOCKS)
        .register(
            (itemGroup) -> {
              itemGroup.accept(CALCITE_SLAB.asItem());
              itemGroup.accept(CALCITE_STAIRS.asItem());
              itemGroup.accept(CALCITE_WALL.asItem());
              if (VERTICAL_CALCITE_SLAB != null) {
                itemGroup.accept(VERTICAL_CALCITE_SLAB.asItem());
              }
            });
    if (VERTICAL_CALCITE_SLAB != null) {
      CreativeModeTabEvents.modifyOutputEvent(VerticalSlab.VERTICAL_SLAB_GROUP_KEY)
          .register(
              (itemGroup) -> {
                itemGroup.accept(VERTICAL_CALCITE_SLAB);
              });
    }
  }
}
