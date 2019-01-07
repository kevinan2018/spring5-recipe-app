package guru.springframework.services;

import guru.springframework.commands.CategoryCommand;
import guru.springframework.commands.IngredientCommand;
import guru.springframework.commands.RecipeCommand;
import guru.springframework.converters.CategoryToCategoryCommand;
import guru.springframework.converters.RecipeCommandToRecipe;
import guru.springframework.converters.RecipeToRecipeCommand;
import guru.springframework.domain.Category;
import guru.springframework.domain.Notes;
import guru.springframework.domain.Recipe;
import guru.springframework.exceptions.NotFoundException;
import guru.springframework.repositories.reactive.CategoryReactiveRepository;
import guru.springframework.repositories.reactive.RecipeReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
//import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
public class RecipeServiceImpl implements RecipeService {

    private final RecipeReactiveRepository recipeReactiveRepository;
    private final RecipeCommandToRecipe recipeCommandToRecipe;
    private final RecipeToRecipeCommand recipeToRecipeCommand;

    public RecipeServiceImpl(RecipeReactiveRepository recipeRepository,
                             RecipeCommandToRecipe recipeCommandToRecipe,
                             RecipeToRecipeCommand recipeToRecipeCommand) {
        this.recipeReactiveRepository = recipeRepository;
        this.recipeCommandToRecipe = recipeCommandToRecipe;
        this.recipeToRecipeCommand = recipeToRecipeCommand;
    }

    @Override
    public Flux<Recipe> getRecipes() {
        log.debug("Get recipe list");

        //NOTE: set forEach might return elements in different order among multiple calls
        //Set<Recipe> recipeSet = new HashSet<>();
        //List<Recipe> recipeList = new ArrayList<>();

        //recipeReactiveRepository.findAll().iterator().forEachRemaining(recipeSet::add);
        //recipeReactiveRepository.findAll().toIterable().forEach(recipeList::add);
        //return recipeList;

        //return recipeReactiveRepository.findAll();
        Flux<Recipe> recips = recipeReactiveRepository.findAll();
        return recips;
    }

    @Override
    public Mono<Recipe> findById(String l) {

        //Optional<Recipe> recipeOptional = recipeReactiveRepository.findById(l).blockOptional();
        Mono<Recipe> recipeMono = recipeReactiveRepository.findById(l);
        if (recipeMono.equals(Mono.empty())) {
            //throw new RuntimeException("recipe not found");
            throw new NotFoundException("Recipe Not Found. Recipe Id: " + l);
        }
        return recipeMono;


    }

    @Override
    //@Transactional
    public Mono<RecipeCommand> findCommandById(String l) {

        return recipeReactiveRepository.findById(l)
                .map(recipe-> {
                    RecipeCommand recipeCommand = recipeToRecipeCommand.convert(recipe);

                    // TODO: need this?
                    recipeCommand.getIngredients().forEach(rc -> rc.setRecipeId(recipeCommand.getId()));

                    return recipeCommand;
                }); //map return Mono<T>

    }

    @Override
    //@Transactional
    public Mono<RecipeCommand> saveRecipeCommand(RecipeCommand command) {

        // new
        if (command.getId().isEmpty()) {
            //todo: mongo db does not generate id
            command.setId(UUID.randomUUID().toString().replace("-", ""));
        }

        // update
        return recipeReactiveRepository.save(recipeCommandToRecipe.convert(command))
                .map(recipeToRecipeCommand::convert);

    }

    @Override
    public void deleteById(String idToDelete) {
        recipeReactiveRepository.deleteById(idToDelete).subscribe();
    }

    @Override
    public Mono<RecipeCommand> newRecipeCommand() {
        Recipe recipe = new Recipe();
        recipe.setNotes(new Notes());//note id
        return Mono.just(recipeToRecipeCommand.convert(recipe));
    }
}
