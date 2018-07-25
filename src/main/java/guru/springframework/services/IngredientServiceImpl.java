package guru.springframework.services;

import guru.springframework.commands.IngredientCommand;
import guru.springframework.converters.IngredientCommandToIngredient;
import guru.springframework.converters.IngredientToIngredientCommand;
import guru.springframework.domain.Ingredient;
import guru.springframework.domain.Recipe;
import guru.springframework.repositories.reactive.RecipeReactiveRepository;
import guru.springframework.repositories.reactive.UnitOfMeasureReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Slf4j
@Service
public class IngredientServiceImpl implements IngredientService {

    private final IngredientToIngredientCommand ingredientToIngredientCommand;
    private final RecipeReactiveRepository recipeReactiveRepository;
    private final UnitOfMeasureReactiveRepository unitOfMeasureReactiveRepository;
    private final IngredientCommandToIngredient ingredientCommandToIngredient;

    public IngredientServiceImpl(IngredientToIngredientCommand ingredientToIngredientCommand,
                                 RecipeReactiveRepository recipeRepository,
                                 UnitOfMeasureReactiveRepository unitOfMeasureRepository,
                                 IngredientCommandToIngredient ingredientCommandToIngredient) {
        this.ingredientToIngredientCommand = ingredientToIngredientCommand;
        this.recipeReactiveRepository = recipeRepository;
        this.unitOfMeasureReactiveRepository = unitOfMeasureRepository;
        this.ingredientCommandToIngredient = ingredientCommandToIngredient;
    }

    @Override
    public Mono<IngredientCommand> findByRecipeIdAndIngredientId(String recipeId, String ingredientId) {

        // Pure spring reactive way Mono<T>
        return recipeReactiveRepository
                .findById(recipeId)
                .flatMapIterable(Recipe::getIngredients)
                .filter(ingredient -> ingredient.getId().equalsIgnoreCase(ingredientId))
                .single()
                .map(ingredient -> {
                    IngredientCommand command = ingredientToIngredientCommand.convert(ingredient);
                    command.setRecipeId(recipeId);
                    return command;
                });

//Mix reactive and java stream
//        return recipeReactiveRepository.findById(recipeId)
//                .map(recipe -> recipe.getIngredients()
//                    .stream()
//                    .filter(ingredient -> ingredient.getId().equalsIgnoreCase(ingredientId))
//                    .findFirst()//Optional<Recipe>
//                )
//                .filter(Optional::isPresent) //Mono<T>.filter is a publisher.filter which filter T/Optional and returns a Mono
//                .map(ingredient -> {
//                    IngredientCommand command = ingredientToIngredientCommand.convert(ingredient.get());
//                    command.setRecipeId(recipeId);
//                    return command;
//                });
//Optional way:
//        Optional<Recipe> recipeOptional = recipeReactiveRepository.findById(recipeId).blockOptional();
//
//        if (!recipeOptional.isPresent()) {
//            //TODO: imple error handling
//            log.error("recipe id not found id: " + recipeId);
//        }
//
//        Recipe recipe = recipeOptional.get();
//
//        Optional<IngredientCommand> ingredientCommandOptional = recipe.getIngredients().stream()
//                .filter(ingredient -> ingredient.getId().equals(ingredientId))
//                .map(ingredient -> ingredientToIngredientCommand.convert(ingredient)).findFirst();
//
//        if (!ingredientCommandOptional.isPresent()) {
//            //TODO: impl error handling
//            log.error("Ingredient id not found: " + ingredientId);
//        }
//
//        //TODO: ingredient's recipeId remove to avoid circular reference, set it manually
//        ingredientCommandOptional.get().setRecipeId(recipeId);
//
//        return Mono.just(ingredientCommandOptional.get());
    }

    @Override
    public Mono<IngredientCommand> saveIngredientCommand(IngredientCommand command) {
        Optional<Recipe> recipeOptional = recipeReactiveRepository.findById(command.getRecipeId()).blockOptional();

        if (!recipeOptional.isPresent()) {
            //TODO: toss error if not found, need to be handled
            log.error("Recipe not found for id: " + command.getRecipeId());
            return Mono.just(new IngredientCommand());//empty ingredient command
        }

        Recipe recipe = recipeOptional.get();

        Optional<Ingredient> ingredientOptional = recipe
                .getIngredients()
                .stream()
                .filter(ingredient -> ingredient.getId().equals(command.getId()))
                .findFirst();

        if (ingredientOptional.isPresent()) {
            // id is the same but description and uom can be updated
            Ingredient ingredientFound = ingredientOptional.get();
            ingredientFound.setDescription(command.getDescription());
            ingredientFound.setAmount(command.getAmount());

            //Optional<UnitOfMeasure> ou = unitOfMeasureReactiveRepository.findById(command.getUom().getId());

            ingredientFound.setUom(unitOfMeasureReactiveRepository
                    .findById(command.getUom().getId()).blockOptional()
                    .orElseThrow(()->new RuntimeException("UOM NOT FOUND")));//TODO: address this
        } else {
            // add new ingredient to the target recipe
            Ingredient ingredient = ingredientCommandToIngredient.convert(command);
            recipe.addIngredient(ingredient);
            //ingredient.setRecipe(recipe);

        }

        // Save the recipe with Hibernate
        Recipe savedRecipe = recipeReactiveRepository.save(recipe).block();

        Optional<Ingredient> savedIngredientOptional = savedRecipe.getIngredients().stream()
                .filter(ingredient -> ingredient.getId().equals(command.getId()))
                .findFirst();

        // check by description
        if (!savedIngredientOptional.isPresent()) {
            // not totally safe, but best guess
            savedIngredientOptional = savedRecipe.getIngredients().stream()
                    .filter(recipeIngredient -> recipeIngredient.getDescription().equals(command.getDescription()))
                    .filter(recipeIngredient -> recipeIngredient.getAmount().equals(command.getAmount()))
                    .filter(recipeIngredient -> recipeIngredient.getUom().getId().equals(command.getUom().getId()))
                    .findFirst();
        }

        //TODO: check for fail
        //return ingredientToIngredientCommand.convert(savedIngredientOptional.get());

        // Ingredient has no recipe id anymore, manually set recipe it
        IngredientCommand savedIngredientCommand = ingredientToIngredientCommand.convert(savedIngredientOptional.get());
        savedIngredientCommand.setRecipeId(recipe.getId());
        return Mono.just(savedIngredientCommand);
    }

    @Override
    public Mono<Void> deleteByRecipeIdAndIngredientId(String recipeId, String ingredientId) {
        log.debug("Deleting ingredient: " + recipeId + ":" + ingredientId);

        Optional<Recipe> recipeOptional = recipeReactiveRepository.findById(recipeId).blockOptional();

        if (recipeOptional.isPresent()){
            Recipe recipe = recipeOptional.get();
            log.debug("found recipe");

            Optional<Ingredient> ingredientOptional = recipe.getIngredients().stream()
                    .filter(ingredient -> ingredient.getId().equals(ingredientId))
                    .findFirst();

            if (ingredientOptional.isPresent()){
                log.debug("found Ingredient" + ingredientId);
                Ingredient ingredientToDelete = ingredientOptional.get();
                //ingredientToDelete.setRecipe(null);

                recipe.getIngredients().remove(ingredientOptional.get());
                Mono<Recipe> recipeMono = recipeReactiveRepository.save(recipe);

                // Mockito test return null
                if (recipeMono != null) {
                    recipeMono.block();
                }

            } else {
                log.debug("Ingredient Id Not found, Id: " + ingredientId);
            }
        } else {
            log.debug("Recipe Id Not found, Id: " + recipeId);
        }
        return Mono.empty();//Mono<Void>

    }
}
