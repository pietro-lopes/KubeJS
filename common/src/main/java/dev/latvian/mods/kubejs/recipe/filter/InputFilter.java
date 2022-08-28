package dev.latvian.mods.kubejs.recipe.filter;

import dev.latvian.mods.kubejs.recipe.IngredientMatch;

/**
 * @author LatvianModder
 */
public class InputFilter implements RecipeFilter {
	private final IngredientMatch match;

	public InputFilter(IngredientMatch match) {
		this.match = match;
	}

	@Override
	public boolean test(FilteredRecipe r) {
		return r.hasInput(match);
	}

	@Override
	public String toString() {
		return "InputFilter{" + match + '}';
	}
}
