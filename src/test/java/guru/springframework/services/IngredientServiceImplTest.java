package guru.springframework.services;

import guru.springframework.commands.IngredientCommand;
import guru.springframework.commands.UnitOfMeasureCommand;
import guru.springframework.converters.IngredientCommandToIngredient;
import guru.springframework.converters.IngredientToIngredientCommand;
import guru.springframework.converters.UnitOfMeasureCommandToUnitOfMeasure;
import guru.springframework.converters.UnitOfMeasureToUnitOfMeasureCommand;
import guru.springframework.domain.Ingredient;
import guru.springframework.domain.Recipe;
import guru.springframework.domain.UnitOfMeasure;
import guru.springframework.repositories.reactive.RecipeReactiveRepository;
import guru.springframework.repositories.reactive.UnitOfMeasureReactiveRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class IngredientServiceImplTest {

    private final IngredientToIngredientCommand ingredientToIngredientCommand;
    private final IngredientCommandToIngredient ingredientCommandToIngredient;

    @Mock
    RecipeReactiveRepository recipeReactiveRepository;

    @Mock
    UnitOfMeasureReactiveRepository unitOfMeasureReactiveRepository;

    /////@Mock IngredientCommandToIngredient.convert() return null!
    ///IngredientCommandToIngredient ingredientCommandToIngredient;

    private IngredientService ingredientService;

    //final memeber can ONLY be initialized in ctor
    public IngredientServiceImplTest() {
        this.ingredientToIngredientCommand = new IngredientToIngredientCommand(new UnitOfMeasureToUnitOfMeasureCommand());
        this.ingredientCommandToIngredient = new IngredientCommandToIngredient(new UnitOfMeasureCommandToUnitOfMeasure());
    }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        ingredientService = new IngredientServiceImpl(
                ingredientToIngredientCommand,
                recipeReactiveRepository,
                unitOfMeasureReactiveRepository,
                ingredientCommandToIngredient);
    }

    @Test
    public void findbyReceipeIdAndReceipeIdHappyPath() throws Exception {
        //given
        Recipe recipe = new Recipe();
        recipe.setId("1");

        Ingredient ingredient1 = new Ingredient();
        ingredient1.setId("1");

        Ingredient ingredient2 = new Ingredient();
        ingredient2.setId("2");

        Ingredient ingredient3 = new Ingredient();
        ingredient3.setId("3");

        recipe.addIngredient(ingredient1);
        recipe.addIngredient(ingredient2);
        recipe.addIngredient(ingredient3);
        Optional<Recipe> recipeOptional = Optional.of(recipe);

        when(recipeReactiveRepository.findById(anyString())).thenReturn(Mono.just(recipeOptional.get()));

        //when
        IngredientCommand ingredientCommand = ingredientService.findByRecipeIdAndIngredientId("1", "3").block();

        //then
        assertEquals("3", ingredientCommand.getId());
        assertEquals("1", ingredientCommand.getRecipeId());
        verify(recipeReactiveRepository, times(1)).findById(anyString());

    }

    @Test
    public void testSaveRecipeCommand() throws Exception {
        //given
        IngredientCommand command = new IngredientCommand();
        command.setId("3");//ingredient id
        command.setUom(new UnitOfMeasureCommand());
        command.setRecipeId("2");

        Optional<Recipe> recipeOptional = Optional.of(new Recipe());
        Ingredient ingredient = new Ingredient();
        ingredient.setId("3");//match ingredient command above
        recipeOptional.get().addIngredient(ingredient);

        Recipe savedRecipe = new Recipe();
        savedRecipe.addIngredient(new Ingredient());
        savedRecipe.getIngredients().iterator().next().setId("3");

        when(recipeReactiveRepository.findById(anyString())).thenReturn(Mono.just(recipeOptional.get()));
        when(recipeReactiveRepository.save(any())).thenReturn(Mono.just(savedRecipe));

        //NOTE: any() instead of anyString() needed here, since findById(null) called in saveIngredientCommand()!
        when(unitOfMeasureReactiveRepository.findById(Mockito.<String>any())).thenReturn(Mono.just(new UnitOfMeasure()));

        //when
        IngredientCommand savedIngredientCommand = ingredientService.saveIngredientCommand(command).block();

        //then
        assertEquals("3", savedIngredientCommand.getId());
        verify(recipeReactiveRepository, times(1)).findById(anyString());
        verify(recipeReactiveRepository, times(1)).save(any());
    }

    @Test
    public void findByRecipeIdAndIngredientId1() {
        //TODO
    }

    @Test
    public void saveIngredientCommand() {
        //TODO:
    }

    @Test
    public void deleteByRecipeIdAndIngredientId() {
        //given
        Recipe recipe = new Recipe();
        Ingredient ingredient = new Ingredient();
        ingredient.setId("3");
        recipe.addIngredient(ingredient);
        //ingredient.setRecipe(recipe);
        Optional<Recipe> recipeOptional = Optional.of(recipe);

        when(recipeReactiveRepository.findById(anyString())).thenReturn(Mono.just(recipeOptional.get()));
        when(recipeReactiveRepository.save(Mockito.<Recipe>any())).thenReturn(Mono.empty());

        //when
        ingredientService.deleteByRecipeIdAndIngredientId("1", "3");

        //then
        verify(recipeReactiveRepository, times(1)).findById(anyString());
        verify(recipeReactiveRepository, times(1)).save(any(Recipe.class));
    }
}
