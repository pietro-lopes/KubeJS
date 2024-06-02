package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonPrimitive;
import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.bindings.BlockWrapper;
import dev.latvian.mods.kubejs.block.state.BlockStatePredicate;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public record BlockComponent() implements RecipeComponent<Block> {
	public static final RecipeComponent<Block> BLOCK = new BlockComponent();

	@Override
	public Codec<Block> codec() {
		return BuiltInRegistries.BLOCK.byNameCodec();
	}

	@Override
	public TypeInfo typeInfo() {
		return TypeInfo.of(Block.class);
	}

	@Override
	public Block wrap(Context cx, KubeRecipe recipe, Object from) {
		return switch (from) {
			case Block b -> b;
			case BlockState s -> s.getBlock();
			case JsonPrimitive json -> BlockWrapper.parseBlockState(json.getAsString()).getBlock();
			case null, default -> BlockWrapper.parseBlockState(String.valueOf(from)).getBlock();
		};
	}

	@Override
	public boolean isInput(KubeRecipe recipe, Block value, ReplacementMatch match) {
		return match instanceof BlockStatePredicate m2 && m2.testBlock(value);
	}

	@Override
	public boolean isOutput(KubeRecipe recipe, Block value, ReplacementMatch match) {
		return match instanceof BlockStatePredicate m2 && m2.testBlock(value);
	}

	@Override
	public String checkEmpty(RecipeKey<Block> key, Block value) {
		if (value == Blocks.AIR) {
			return "Block '" + key.name + "' can't be empty!";
		}

		return "";
	}

	@Override
	public String toString() {
		return "block";
	}
}
