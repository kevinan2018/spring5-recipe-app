package guru.springframework.repositories;

import guru.springframework.bootstrap.RecipeBootstrap;
import guru.springframework.domain.UnitOfMeasure;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;
import java.util.OptionalInt;

import static org.junit.Assert.*;

//@Ignore
@RunWith(SpringRunner.class)
//@DataJpaTest
@DataMongoTest
public class UnitOfMeasureRepositoryIT {

    @Autowired
    UnitOfMeasureRepository unitOfMeasureRepository;

    //@Autowired //this won't help
    //RecipeRepository recipeRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    RecipeRepository recipeRepository;

    @Before
    public void setUp() throws Exception {

        // Clean up since mongodd has not tranaction, no rollback
        recipeRepository.deleteAll();
        unitOfMeasureRepository.deleteAll();
        categoryRepository.deleteAll();

        //NOTE: we call ctor here directly, so autowired won't work in RecipeBootstrap!
        // mimic the spring context
        RecipeBootstrap recipeBootstrap = new RecipeBootstrap(categoryRepository, recipeRepository, unitOfMeasureRepository);
        recipeBootstrap.onApplicationEvent(null);
    }

//    @BeforeAll
//    public void setUp() throws Exception {
//
//        // mimic the spring context
//        RecipeBootstrap recipeBootstrap = new RecipeBootstrap(categoryRepository, recipeRepository, unitOfMeasureRepository);
//        recipeBootstrap.onApplicationEvent(null);
//    }

    @Test
    public void findBydescription() {

        Optional<UnitOfMeasure> uomOptional = unitOfMeasureRepository.findBydescription("Teaspoon");

        assertEquals("Teaspoon", uomOptional.get().getDescription());
    }


    @Test
    public void findBydescriptionCup() {

        Optional<UnitOfMeasure> uomOptional = unitOfMeasureRepository.findBydescription("Cup");

        assertEquals("Cup", uomOptional.get().getDescription());
    }
}

/*
Fix: unitOfMeasureRepository.deleteAll()
     categoryRepository.deleteAll()

Problem:
@Before run for every test, insert data twice!
org.springframework.dao.IncorrectResultSizeDataAccessException: Query { "$java" : Query: { "description" : "Each" }, Fields: { }, Sort: { } } returned non unique result.
 */