package guru.springframework.repositories;

import guru.springframework.bootstrap.RecipeBootstrap;
import guru.springframework.domain.UnitOfMeasure;
import guru.springframework.repositories.reactive.CategoryReactiveRepository;
import guru.springframework.repositories.reactive.RecipeReactiveRepository;
import guru.springframework.repositories.reactive.UnitOfMeasureReactiveRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.Assert.*;

//@Ignore
@RunWith(SpringRunner.class)
//@DataJpaTest
@DataMongoTest
public class UnitOfMeasureRepositoryIT {

    @Autowired
    UnitOfMeasureReactiveRepository unitOfMeasureReactiveRepository;

    //@Autowired //this won't help
    //RecipeRepository recipeReactiveRepository;

    @Autowired
    CategoryReactiveRepository categoryReactiveRepository;

    @Autowired
    RecipeReactiveRepository recipeReactiveRepository;

    @Before
    public void setUp() throws Exception {

        // Clean up since mongodd has not tranaction, no rollback
        recipeReactiveRepository.deleteAll().block();
        unitOfMeasureReactiveRepository.deleteAll().block();
        categoryReactiveRepository.deleteAll().block();

        //NOTE: we call ctor here directly, so autowired won't work in RecipeBootstrap!
        // mimic the spring context
        RecipeBootstrap recipeBootstrap = new RecipeBootstrap(categoryReactiveRepository, recipeReactiveRepository, unitOfMeasureReactiveRepository);
        recipeBootstrap.onApplicationEvent(null);
    }

//    @BeforeAll
//    public void setUp() throws Exception {
//
//        // mimic the spring context
//        RecipeBootstrap recipeBootstrap = new RecipeBootstrap(categoryReactiveRepository, recipeReactiveRepository, unitOfMeasureReactiveRepository);
//        recipeBootstrap.onApplicationEvent(null);
//    }

    @Test
    public void findBydescription() {

        Optional<UnitOfMeasure> uomOptional = unitOfMeasureReactiveRepository.findBydescription("Teaspoon").blockOptional();

        assertEquals("Teaspoon", uomOptional.get().getDescription());
    }


    @Test
    public void findBydescriptionCup() {

        Optional<UnitOfMeasure> uomOptional = unitOfMeasureReactiveRepository.findBydescription("Cup").blockOptional();

        assertEquals("Cup", uomOptional.get().getDescription());
    }
}

/*
Fix: unitOfMeasureReactiveRepository.deleteAll()
     categoryReactiveRepository.deleteAll()

Problem:
@Before run for every test, insert data twice!
org.springframework.dao.IncorrectResultSizeDataAccessException: Query { "$java" : Query: { "description" : "Each" }, Fields: { }, Sort: { } } returned non unique result.
 */