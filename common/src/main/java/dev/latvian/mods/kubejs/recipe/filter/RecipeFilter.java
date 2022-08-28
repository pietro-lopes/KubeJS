package dev.latvian.mods.kubejs.recipe.filter;

import dev.latvian.mods.kubejs.recipe.IngredientMatch;
import dev.latvian.mods.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.ListJS;
import dev.latvian.mods.kubejs.util.MapJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

/**
 * @author LatvianModder
 */
@FunctionalInterface
public interface RecipeFilter extends Predicate<FilteredRecipe> {
	RecipeFilter ALWAYS_TRUE = r -> true;
	RecipeFilter ALWAYS_FALSE = r -> false;

	@Override
	boolean test(FilteredRecipe r);

	static RecipeFilter of(@Nullable Object o) {
		if (o == null || o == ALWAYS_TRUE) {
			return ALWAYS_TRUE;
		} else if (o == ALWAYS_FALSE) {
			return ALWAYS_FALSE;
		}

		var list = ListJS.orSelf(o);

		if (list.isEmpty()) {
			return ALWAYS_TRUE;
		} else if (list.size() > 1) {
			var predicate = new OrFilter();

			for (var o1 : list) {
				var p = of(o1);

				if (p == ALWAYS_TRUE) {
					return ALWAYS_TRUE;
				} else if (p != ALWAYS_FALSE) {
					predicate.list.add(p);
				}
			}

			return predicate.list.isEmpty() ? ALWAYS_FALSE : predicate.list.size() == 1 ? predicate.list.get(0) : predicate;
		}

		var map = MapJS.of(list.get(0));

		if (map == null || map.isEmpty()) {
			return ALWAYS_TRUE;
		}

		var predicate = new AndFilter();

		if (map.get("or") != null) {
			predicate.list.add(of(map.get("or")));
		}

		if (map.get("not") != null) {
			predicate.list.add(new NotFilter(of(map.get("not"))));
		}

		try {
			var id = map.get("id");

			if (id != null) {
				var pattern = UtilsJS.parseRegex(id);
				predicate.list.add(pattern == null ? new IDFilter(UtilsJS.getMCID(id)) : new RegexIDFilter(pattern));
			}

			var type = map.get("type");

			if (type != null) {
				predicate.list.add(new TypeFilter(UtilsJS.getMCID(type)));
			}

			var group = map.get("group");

			if (group != null) {
				predicate.list.add(new GroupFilter(group.toString()));
			}

			var mod = map.get("mod");

			if (mod != null) {
				predicate.list.add(new ModFilter(mod.toString()));
			}

			var input = map.get("input");

			if (input != null) {
				predicate.list.add(new InputFilter(IngredientMatch.of(input)));
			}

			var output = map.get("output");

			if (output != null) {
				predicate.list.add(new OutputFilter(IngredientMatch.of(output)));
			}

			return predicate.list.isEmpty() ? ALWAYS_TRUE : predicate.list.size() == 1 ? predicate.list.get(0) : predicate;
		} catch (RecipeExceptionJS ex) {
			if (ex.error) {
				ConsoleJS.SERVER.error(ex.getMessage());
			} else {
				ConsoleJS.SERVER.warn(ex.getMessage());
			}

			return ALWAYS_FALSE;
		}
	}
}