package guru.springframework.converters;

import guru.springframework.commands.CategoryCommand;
import guru.springframework.commands.IngredientCommand;
import guru.springframework.commands.NotesCommand;
import guru.springframework.commands.RecipeCommand;
import guru.springframework.domain.Difficulty;
import guru.springframework.domain.Recipe;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class RecipeCommandToRecipeTest {
    public static final String RECIPE_ID = "1";
    public static final Integer COOK_TIME = Integer.valueOf("5");
    public static final Integer PREP_TIME = Integer.valueOf("7");
    public static final String DESCRIPTION = "My Recipe";
    public static final String DIRECTIONS = "Directions";
    public static final Difficulty DIFFICULTY = Difficulty.EASY;
    public static final Integer SERVINGS = Integer.valueOf("3");
    public static final String SOURCE = "Source";
    public static final String URL = "Some URL";
    public static final String CAT_ID_1 = "1";
    public static final String CAT_ID_2 = "2";
    public static final String INGRED_ID_1 = "3";
    public static final String INGRED_ID_2 = "4";
    public static final String NOTES_ID = "9";

    RecipeCommandToRecipe converter;

    @Before
    public void setUp() throws Exception {
        converter = new RecipeCommandToRecipe(new CategoryCommandToCategory(),
                new IngredientCommandToIngredient(new UnitOfMeasureCommandToUnitOfMeasure()),
                new NotesCommandToNotes());
    }

    @Test
    public void testNullObject() throws Exception {
        assertNull(converter.convert(null));
    }

    @Test
    public void testEmptyObject() throws Exception {
        assertNotNull(converter.convert(new RecipeCommand()));
    }


    @Test
    public void convert() {
        //given
        RecipeCommand command = new RecipeCommand();
        command.setId(RECIPE_ID);
        command.setCookTime(COOK_TIME);
        command.setPrepTime(PREP_TIME);
        command.setDescription(DESCRIPTION);
        command.setDifficulty(DIFFICULTY);
        command.setDirections(DIRECTIONS);
        command.setServings(SERVINGS);
        command.setSource(SOURCE);
        command.setUrl(URL);

        NotesCommand notesCommand = new NotesCommand();
        notesCommand.setId(NOTES_ID);
        command.setNotes(notesCommand);

        CategoryCommand categoryCommand = new CategoryCommand();
        categoryCommand.setId(CAT_ID_1);
        command.getCategories().add(categoryCommand);

        categoryCommand = new CategoryCommand();
        categoryCommand.setId(CAT_ID_2);
        command.getCategories().add(categoryCommand);


        IngredientCommand ingredientCommand = new IngredientCommand();
        ingredientCommand.setId(INGRED_ID_1);
        command.getIngredients().add(ingredientCommand);


        ingredientCommand = new IngredientCommand();
        ingredientCommand.setId(INGRED_ID_2);
        command.getIngredients().add(ingredientCommand);

        //when
        Recipe recipe = converter.convert(command);

        //then
        assertNotNull(recipe);
        assertEquals(RECIPE_ID, recipe.getId());
        assertEquals(COOK_TIME, recipe.getCookTime());
        assertEquals(PREP_TIME, recipe.getPrepTime());
        assertEquals(DESCRIPTION, recipe.getDescription());
        assertEquals(DIFFICULTY, recipe.getDifficulty());
        assertEquals(DIRECTIONS, recipe.getDirections());
        assertEquals(SERVINGS, recipe.getServings());
        assertEquals(SOURCE, recipe.getSource());
        assertEquals(URL, recipe.getUrl());
        assertEquals(NOTES_ID, recipe.getNotes().getId());
        assertEquals(2, recipe.getCategories().size());
        assertEquals(2, recipe.getIngredients().size());


    }

}