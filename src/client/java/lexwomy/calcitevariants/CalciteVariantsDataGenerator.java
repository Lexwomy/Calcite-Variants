package lexwomy.calcitevariants;

import static net.minecraft.data.BlockFamilies.familyBuilder;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lexwomy.calcitevariants.references.CalciteVariantsBlockItemIds;
import lexwomy.verticalslabs.VerticalSlabs;
import lexwomy.verticalslabs.VerticalSlabsDataGenerator;
import lexwomy.verticalslabs.data.BottomTopBasedBlockModelGenerator;
import lexwomy.verticalslabs.data.VerticalSlabDetails;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootSubProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.BlockItemTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jspecify.annotations.Nullable;

public class CalciteVariantsDataGenerator implements DataGeneratorEntrypoint {
  private static final BlockFamily CALCITE =
      familyBuilder(Blocks.CALCITE)
          .slab(CalciteVariants.CALCITE_SLAB)
          .stairs(CalciteVariants.CALCITE_STAIRS)
          .wall(CalciteVariants.CALCITE_WALL)
          .getFamily();
  private static VerticalSlabDetails CALCITE_VERTICAL_SLAB_DETAILS;

  @Override
  public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
    if (!CalciteVariants.hasVerticalSlabDependency
        || CalciteVariants.VERTICAL_CALCITE_SLAB == null) {
      throw new RuntimeException(
          "Calcite Variants datagen needs to be run with vertical slabs dependency!");
    }

    CALCITE_VERTICAL_SLAB_DETAILS =
        new VerticalSlabDetails(
            "Vertical Calcite Slab",
            CalciteVariantsBlockItemIds.VERTICAL_CALCITE_SLAB,
            CalciteVariants.VERTICAL_CALCITE_SLAB,
            Blocks.CALCITE,
            BottomTopBasedBlockModelGenerator.simpleUVLockedBlockModel(),
            List.of(Blocks.CALCITE),
            List.of(Blocks.CALCITE));
    FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
    pack.addProvider(CalciteVariantsModelProvider::new);
    pack.addProvider(CalciteVariantsRecipeProvider::new);
    pack.addProvider(CalciteVariantsLanguageProvider::new);
    CalciteVariantsBlockTagsProvider calciteVariantsBlockTagsProvider =
        pack.addProvider(CalciteVariantsBlockTagsProvider::new);
    pack.addProvider(
        (output, registriesFuture) ->
            new CalciteVariantsItemTagsProvider(
                output, registriesFuture, calciteVariantsBlockTagsProvider));
    pack.addProvider(CalciteVariantsBlockLootProvider::new);
  }

  private static class CalciteVariantsModelProvider extends FabricModelProvider {
    private CalciteVariantsModelProvider(FabricPackOutput output) {
      super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockModelGenerators) {
      blockModelGenerators.family(CALCITE.getBaseBlock()).generateFor(CALCITE);
      CALCITE_VERTICAL_SLAB_DETAILS.generateBlockModels(blockModelGenerators);
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerators) {}
  }

  private static class CalciteVariantsLanguageProvider extends FabricLanguageProvider {
    private static final List<Pair<Block, String>> TRANSLATION_MAP =
        List.of(
            new Pair<>(CalciteVariants.CALCITE_SLAB, "Calcite Slab"),
            new Pair<>(CalciteVariants.CALCITE_STAIRS, "Calcite Stairs"),
            new Pair<>(CalciteVariants.CALCITE_WALL, "Calcite Wall"));

    private CalciteVariantsLanguageProvider(
        FabricPackOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
      super(dataOutput, registryLookup);
    }

    @Override
    public void generateTranslations(
        HolderLookup.Provider provider, TranslationBuilder translationBuilder) {
      for (Pair<Block, String> mapping : TRANSLATION_MAP) {
        translationBuilder.add(mapping.getFirst(), mapping.getSecond());
      }
      CALCITE_VERTICAL_SLAB_DETAILS.generateTranslation(translationBuilder);
    }
  }

  private static class CalciteVariantsRecipeProvider extends FabricRecipeProvider {
    private CalciteVariantsRecipeProvider(
        FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
      super(output, registriesFuture);
    }

    @Override
    protected RecipeProvider createRecipeProvider(
        HolderLookup.Provider provider, RecipeOutput recipeOutput) {
      return new RecipeProvider(provider, recipeOutput) {
        @Override
        public void buildRecipes() {
          this.generateRecipes(CALCITE, FeatureFlagSet.of(FeatureFlags.VANILLA));
          this.stonecutterResultFromBase(
              RecipeCategory.BUILDING_BLOCKS, CalciteVariants.CALCITE_SLAB, Blocks.CALCITE, 2);
          this.stonecutterResultFromBase(
              RecipeCategory.BUILDING_BLOCKS, CalciteVariants.CALCITE_STAIRS, Blocks.CALCITE);
          this.stonecutterResultFromBase(
              RecipeCategory.BUILDING_BLOCKS, CalciteVariants.CALCITE_WALL, Blocks.CALCITE);
          CALCITE_VERTICAL_SLAB_DETAILS.generateRecipes(this, recipeOutput);
        }
      };
    }

    @Override
    public String getName() {
      return "Calcite Variants Recipe Provider";
    }
  }

  private static class CalciteVariantsBlockTagsProvider
      extends FabricTagsProvider.BlockTagsProvider {
    private CalciteVariantsBlockTagsProvider(
        FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
      super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
      this.tag(VerticalSlabs.VERTICAL_SLABS.block())
          .add(CALCITE_VERTICAL_SLAB_DETAILS.slabId().block())
          .setReplace(false);
      this.tag(VerticalSlabs.VERTICAL_MINEABLE_SLABS.block())
          .add(CALCITE_VERTICAL_SLAB_DETAILS.slabId().block())
          .setReplace(false);
      this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
          .add(CalciteVariantsBlockItemIds.CALCITE_SLAB.block())
          .add(CalciteVariantsBlockItemIds.CALCITE_WALL.block())
          .add(CalciteVariantsBlockItemIds.CALCITE_STAIRS.block())
          .setReplace(false);
      this.tag(BlockTags.SLABS)
          .add(CalciteVariantsBlockItemIds.CALCITE_SLAB.block())
          .setReplace(false);
      this.tag(BlockTags.STAIRS)
          .add(CalciteVariantsBlockItemIds.CALCITE_STAIRS.block())
          .setReplace(false);
      this.tag(BlockTags.WALLS)
          .add(CalciteVariantsBlockItemIds.CALCITE_WALL.block())
          .setReplace(false);
    }
  }

  private static class CalciteVariantsItemTagsProvider extends FabricTagsProvider.ItemTagsProvider {
    private CalciteVariantsItemTagsProvider(
        FabricPackOutput output,
        CompletableFuture<HolderLookup.Provider> registriesFuture,
        @Nullable BlockTagsProvider blockTagsProvider) {
      super(output, registriesFuture, blockTagsProvider);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
      this.tag(VerticalSlabs.VERTICAL_SLABS.item())
          .add(CALCITE_VERTICAL_SLAB_DETAILS.slabId().item())
          .setReplace(false);
      this.tag(VerticalSlabs.VERTICAL_MINEABLE_SLABS.item())
          .add(CALCITE_VERTICAL_SLAB_DETAILS.slabId().item())
          .setReplace(false);
      this.tag(BlockItemTags.SLABS.item())
          .add(CalciteVariantsBlockItemIds.CALCITE_SLAB.item())
          .setReplace(false);
      this.tag(BlockItemTags.STAIRS.item())
          .add(CalciteVariantsBlockItemIds.CALCITE_STAIRS.item())
          .setReplace(false);
      this.tag(BlockItemTags.WALLS.item())
          .add(CalciteVariantsBlockItemIds.CALCITE_WALL.item())
          .setReplace(false);
    }
  }

  private static class CalciteVariantsBlockLootProvider extends FabricBlockLootSubProvider {
    private CalciteVariantsBlockLootProvider(
        FabricPackOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
      super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
      this.add(CalciteVariants.CALCITE_SLAB, this::createSlabItemTable);
      this.dropSelf(CalciteVariants.CALCITE_STAIRS);
      this.dropSelf(CalciteVariants.CALCITE_WALL);
      this.add(
          CALCITE_VERTICAL_SLAB_DETAILS.slab(),
          VerticalSlabsDataGenerator.VerticalSlabsBlockLootProvider.createVerticalSlabLootTable(
              this, CALCITE_VERTICAL_SLAB_DETAILS.slab()));
    }
  }
}
