package dev.latvian.mods.kubejs.recipe.filter;

import dev.latvian.mods.kubejs.recipe.IngredientMatch;

/**
 * @author LatvianModder
 */
public class OutputFilter implements RecipeFilter {
	private final IngredientMatch match;

	public OutputFilter(IngredientMatch match) {
		this.match = match;
	}

	@Override
	public boolean test(FilteredRecipe r) {
		return r.hasOutput(match);
	}

	@Override
	public String toString() {
		return "OutputFilter{" + match + '}';
	}
}
