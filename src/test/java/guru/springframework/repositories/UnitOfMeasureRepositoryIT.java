package guru.springframework.repositories;

import guru.springframework.domain.UnitOfMeasure;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;
import java.util.OptionalInt;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class UnitOfMeasureRepositoryIT {

    @Autowired
    UnitOfMeasureRepository unitOfMeasureRepository;

    @Before
    public void setUp() throws Exception {
    }

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