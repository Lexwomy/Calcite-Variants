package lexwomy.calcitevariants;

import static net.minecraft.data.BlockFamilies.familyBuilder;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lexwomy.verticalslabs.VerticalSlabs;
import lexwomy.verticalslabs.VerticalSlabsDataGenerator;
import lexwomy.verticalslabs.data.BottomTopBasedBlockModelGenerator;
import lexwomy.verticalslabs.data.VerticalSlabDetails;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
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
        || CalciteVariants.CALCITE_VERTICAL_SLAB == null) {
      throw new RuntimeException(
          "Calcite Variants datagen needs to be run with vertical slabs dependency!");
    }

    CALCITE_VERTICAL_SLAB_DETAILS =
        new VerticalSlabDetails(
            "Calcite Vertical Slab",
            CalciteVariants.CALCITE_VERTICAL_SLAB,
            Blocks.CALCITE,
            BottomTopBasedBlockModelGenerator.simpleUVLockedBlockModel(),
            List.of(Blocks.CALCITE),
            List.of(Blocks.CALCITE));
    FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
    pack.addProvider(CalciteVariantsModelProvider::new);
    pack.addProvider(CalciteVariantsRecipeProvider::new);
    pack.addProvider(CalciteVariantsLanguageProvider::new);
    CalciteVariantsBlockTagProvider calciteVariantsBlockTagProvider =
        pack.addProvider(CalciteVariantsBlockTagProvider::new);
    pack.addProvider(
        (output, registriesFuture) ->
            new CalciteVariantsItemTagProvider(
                output, registriesFuture, calciteVariantsBlockTagProvider));
    pack.addProvider(CalciteVariantsBlockLootProvider::new);
  }

  private static class CalciteVariantsModelProvider extends FabricModelProvider {
    private CalciteVariantsModelProvider(FabricDataOutput output) {
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
    private static final List<Pair<String, String>> TRANSLATION_MAP =
        List.of(
            new Pair<>("block.calcite_variants.calcite_slab", "Calcite Slab"),
            new Pair<>("block.calcite_variants.calcite_stairs", "Calcite Stairs"),
            new Pair<>("block.calcite_variants.calcite_wall", "Calcite Wall"));

    private CalciteVariantsLanguageProvider(
        FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
      super(dataOutput, registryLookup);
    }

    @Override
    public void generateTranslations(
        HolderLookup.Provider provider, TranslationBuilder translationBuilder) {
      for (Pair<String, String> mapping : TRANSLATION_MAP) {
        translationBuilder.add(mapping.getFirst(), mapping.getSecond());
      }
      CALCITE_VERTICAL_SLAB_DETAILS.generateTranslation(translationBuilder);
    }
  }

  private static class CalciteVariantsRecipeProvider extends FabricRecipeProvider {
    private CalciteVariantsRecipeProvider(
        FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
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

  private static class CalciteVariantsBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    private CalciteVariantsBlockTagProvider(
        FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
      super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
      valueLookupBuilder(VerticalSlabs.VERTICAL_SLABS)
          .add(CALCITE_VERTICAL_SLAB_DETAILS.slab())
          .setReplace(false);
      valueLookupBuilder(VerticalSlabs.VERTICAL_MINEABLE_SLABS)
          .add(CALCITE_VERTICAL_SLAB_DETAILS.slab())
          .setReplace(false);
      valueLookupBuilder(BlockTags.MINEABLE_WITH_PICKAXE)
          .add(
              CalciteVariants.CALCITE_SLAB,
              CalciteVariants.CALCITE_WALL,
              CalciteVariants.CALCITE_STAIRS)
          .setReplace(false);
      valueLookupBuilder(BlockTags.SLABS).add(CalciteVariants.CALCITE_SLAB).setReplace(false);
      valueLookupBuilder(BlockTags.STAIRS).add(CalciteVariants.CALCITE_STAIRS).setReplace(false);
      valueLookupBuilder(BlockTags.WALLS).add(CalciteVariants.CALCITE_WALL).setReplace(false);
    }
  }

  private static class CalciteVariantsItemTagProvider extends FabricTagProvider.ItemTagProvider {
    private CalciteVariantsItemTagProvider(
        FabricDataOutput output,
        CompletableFuture<HolderLookup.Provider> registriesFuture,
        @Nullable BlockTagProvider blockTagProvider) {
      super(output, registriesFuture, blockTagProvider);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
      valueLookupBuilder(VerticalSlabs.VERTICAL_SLABS_ITEMS)
          .add(CALCITE_VERTICAL_SLAB_DETAILS.slab().asItem())
          .setReplace(false);
      valueLookupBuilder(VerticalSlabs.VERTICAL_MINEABLE_SLABS_ITEMS)
          .add(CALCITE_VERTICAL_SLAB_DETAILS.slab().asItem())
          .setReplace(false);
      valueLookupBuilder(ItemTags.SLABS)
          .add(CalciteVariants.CALCITE_SLAB.asItem())
          .setReplace(false);
      valueLookupBuilder(ItemTags.STAIRS)
          .add(CalciteVariants.CALCITE_STAIRS.asItem())
          .setReplace(false);
      valueLookupBuilder(ItemTags.WALLS)
          .add(CalciteVariants.CALCITE_WALL.asItem())
          .setReplace(false);
    }
  }

  private static class CalciteVariantsBlockLootProvider extends FabricBlockLootTableProvider {
    private CalciteVariantsBlockLootProvider(
        FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
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
