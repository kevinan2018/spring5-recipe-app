package guru.springframework.services;

import guru.springframework.commands.RecipeCommand;
import guru.springframework.converters.RecipeCommandToRecipe;
import guru.springframework.converters.RecipeToRecipeCommand;
import guru.springframework.domain.Recipe;
import guru.springframework.exceptions.NotFoundException;
import guru.springframework.repositories.reactive.RecipeReactiveRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class RecipeServiceImplTest {

    RecipeServiceImpl recipeService;

    @Mock
    RecipeReactiveRepository recipeReactiveRepository;

    @Mock
    RecipeCommandToRecipe recipeCommandToRecipe;

    @Mock
    RecipeToRecipeCommand recipeToRecipeCommand;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        recipeService = new RecipeServiceImpl(recipeReactiveRepository, recipeCommandToRecipe, recipeToRecipeCommand);
    }

    @Test
    public void getRecipesTest() throws Exception {

        // Add recipe to recipe list
        Recipe recipe = new Recipe();
        List<Recipe> recipeData = new ArrayList<>();
        recipeData.add(recipe);

        when(recipeService.getRecipes()).thenReturn(Flux.fromIterable(recipeData));

        List<Recipe> recipes = recipeService.getRecipes().collectList().block();

        //assertEquals(recipes.size(), 0); //Mockito by default generate empty list
        assertEquals(recipes.size(), 1); //thenReturn()
        //RecipeRepository.findAll() called exactly 1 time!
        verify(recipeReactiveRepository, times(1)).findAll();
    }

    @Test
    public void getRecipeByIdTest() throws Exception {
        /* Mock up the database for recipes */
        Recipe recipe = new Recipe();
        recipe.setId("1");
        //Optional<Recipe> recipeOptional = Optional.of(recipe);

        when(recipeReactiveRepository.findById(anyString())).thenReturn(Mono.just(recipe));

        Recipe recipeReturned = recipeService.findById("1").block();

        assertNotNull("Null recipe returned", recipeReturned);
        verify(recipeReactiveRepository, times(1)).findById(anyString());
        verify(recipeReactiveRepository, never()).findAll();
    }

    @Test(expected = NotFoundException.class)
    public void getReecipeByIdTestNotFound() throws Exception {

        //Optional<Recipe> recipeOptional = Optional.empty();

        when(recipeReactiveRepository.findById(anyString())).thenReturn(Mono.empty());

        Recipe recipReturned = recipeService.findById("1").block();//NotFoundException here
    }

    @Test
    public void getRecipeCommandByIdTest() throws Exception {
        Recipe recipe = new Recipe();
        recipe.setId("1");
        //Optional<Recipe> recipeOptional = Optional.of(recipe);

        when(recipeReactiveRepository.findById(anyString())).thenReturn(Mono.just(recipe));

        RecipeCommand recipeCommand = new RecipeCommand();
        recipeCommand.setId("1");

        when(recipeToRecipeCommand.convert(any())).thenReturn(recipeCommand);

        RecipeCommand command = recipeService.findCommandById("1").block();

        assertNotNull("Null recipe returned", command);
        verify(recipeReactiveRepository, times(1)).findById(anyString());
        verify(recipeReactiveRepository, never()).findAll();
    }


    @Test
    public void deleteByIdTest() throws Exception {
        //given
        String idToDelete = "2";

        when(recipeReactiveRepository.deleteById(anyString())).thenReturn(Mono.empty());

        //when
        recipeService.deleteById(idToDelete);

        //no 'when' since methtod has void return type

        //then
        verify(recipeReactiveRepository, times(1)).deleteById(anyString());
    }

}