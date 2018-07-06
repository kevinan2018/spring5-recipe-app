package guru.springframework;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class) //bring in spring context! Slow, expensive!
@SpringBootTest //This is integration test NOT unit test!
public class Spring5RecipeAppApplicationTests {

    @Test
    public void contextLoads() {
    }

}
