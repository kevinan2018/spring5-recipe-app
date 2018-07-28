package guru.springframework.services;

import guru.springframework.commands.IngredientCommand;
import guru.springframework.domain.Recipe;
import reactor.core.publisher.Mono;

public interface IngredientService {
    Mono<IngredientCommand> findByRecipeIdAndIngredientId(String recipeId, String ingredientId);
    Mono<IngredientCommand> saveIngredientCommand(IngredientCommand command);

    Mono<Recipe> deleteByRecipeIdAndIngredientId(String recipeId, String ingredientId);
}
