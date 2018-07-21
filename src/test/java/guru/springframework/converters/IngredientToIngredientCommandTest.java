package guru.springframework.converters;

import guru.springframework.commands.IngredientCommand;
import guru.springframework.domain.Ingredient;
import guru.springframework.domain.Recipe;
import guru.springframework.domain.UnitOfMeasure;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class IngredientToIngredientCommandTest {
    public static final Recipe RECIPE = new Recipe();
    public static final BigDecimal AMOUNT = new BigDecimal(1);
    public static final String DESCRIPTION = "Cheeseburger";
    public static final String ID_VALUE = "1";
    public static final String UOM_ID = "2";

    IngredientToIngredientCommand converter;

    @Before
    public void setUp() throws Exception {
        converter = new IngredientToIngredientCommand(new UnitOfMeasureToUnitOfMeasureCommand());
    }

    @Test
    public void testNullConverter() throws Exception {
        assertNull(converter.convert(null));
    }

    @Test
    public void testEmptyObject() throws Exception {
        assertNotNull(converter.convert(new Ingredient()));
    }

    @Test
    public void testConvertNullUOM() throws Exception {
        //given
        Ingredient ingredient = new Ingredient();
        ingredient.setId(ID_VALUE);
        //ingredient.setRecipe(RECIPE);
        ingredient.setAmount(AMOUNT);
        ingredient.setDescription(DESCRIPTION);
        ingredient.setUom(null);

        //when
        IngredientCommand command = converter.convert(ingredient);

        //then
        assertNull(command.getUom());
        assertEquals(ID_VALUE, command.getId());
        //assertEquals(RECIPE, command.get);
        assertEquals(AMOUNT, command.getAmount());
        assertEquals(DESCRIPTION,command.getDescription());

    }


    @Test
    public void testConvertWithUom() throws Exception {
        //givern
        Ingredient ingredient = new Ingredient();
        ingredient.setId(ID_VALUE);
        //ingredient.setRecipe(RECIPE);
        ingredient.setAmount(AMOUNT);
        ingredient.setDescription(DESCRIPTION);

        UnitOfMeasure uom = new UnitOfMeasure();
        uom.setId(UOM_ID);
        ingredient.setUom(uom);

        //when
        IngredientCommand command = converter.convert(ingredient);

        //then
        assertNotNull(command.getUom());
        assertEquals(UOM_ID, command.getUom().getId());
        assertEquals(ID_VALUE, command.getId());
        //assertEquals(RECIPE, command.get);
        assertEquals(AMOUNT, command.getAmount());
        assertEquals(DESCRIPTION,command.getDescription());

    }
}