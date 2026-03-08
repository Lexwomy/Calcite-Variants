package lexwomy.calcitevariants;

import com.mojang.datafixers.util.Pair;
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
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static net.minecraft.data.BlockFamilies.familyBuilder;

public class CalciteVariantsDataGenerator implements DataGeneratorEntrypoint {
    private static final BlockFamily CALCITE = familyBuilder(Blocks.CALCITE)
            .slab(CalciteVariants.CALCITE_SLAB)
            .stairs(CalciteVariants.CALCITE_STAIRS)
            .wall(CalciteVariants.CALCITE_WALL)
            .getFamily();

    private static class CalciteVariantsModelProvider extends FabricModelProvider {
        private CalciteVariantsModelProvider(FabricDataOutput output) {
            super(output);
        }

        @Override
        public void generateBlockStateModels(BlockModelGenerators blockModelGenerators) {
            blockModelGenerators.family(CALCITE.getBaseBlock()).generateFor(CALCITE);
        }

        @Override
        public void generateItemModels(ItemModelGenerators itemModelGenerators) {

        }
    }
    private static class CalciteVariantsLanguageProvider extends FabricLanguageProvider {
        private static final List<Pair<String, String>> TRANSLATION_MAP = List.of(
            new Pair<>("block.calcite_variants.calcite_slab", "Calcite Slab"),
            new Pair<>("block.calcite_variants.calcite_stairs", "Calcite Stairs"),
            new Pair<>("block.calcite_variants.calcite_wall", "Calcite Wall")
        );
        private CalciteVariantsLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
            super(dataOutput, registryLookup);
        }

        @Override
        public void generateTranslations(HolderLookup.Provider provider, TranslationBuilder translationBuilder) {
            for (Pair<String, String> mapping : TRANSLATION_MAP) {
                translationBuilder.add(mapping.getFirst(), mapping.getSecond());
            }
        }
    }
    private static class CalciteVariantsRecipeProvider extends FabricRecipeProvider {
        private CalciteVariantsRecipeProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider provider, RecipeOutput recipeOutput) {
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
        }
      };
        }

        @Override
        public String getName() {
            return "CalciteVariantsRecipeProvider";
        }
    }
    private static class CalciteVariantsBlockTagProvider extends FabricTagProvider.BlockTagProvider {
        private CalciteVariantsBlockTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        protected void addTags(HolderLookup.Provider wrapperLookup) {
            valueLookupBuilder(BlockTags.MINEABLE_WITH_PICKAXE).add(CalciteVariants.CALCITE_SLAB, CalciteVariants.CALCITE_WALL, CalciteVariants.CALCITE_STAIRS).setReplace(false);
        }
    }
    private static class CalciteVariantsBlockLootProvider extends FabricBlockLootTableProvider {
        private CalciteVariantsBlockLootProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
            super(dataOutput, registryLookup);
        }

        @Override
        public void generate() {
            this.add(CalciteVariants.CALCITE_SLAB, this::createSlabItemTable);
            this.dropSelf(CalciteVariants.CALCITE_STAIRS);
            this.dropSelf(CalciteVariants.CALCITE_WALL);
        }
    }
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(CalciteVariantsModelProvider::new);
        pack.addProvider(CalciteVariantsRecipeProvider::new);
        pack.addProvider(CalciteVariantsLanguageProvider::new);
        pack.addProvider(CalciteVariantsBlockTagProvider::new);
        pack.addProvider(CalciteVariantsBlockLootProvider::new);
	}
}
