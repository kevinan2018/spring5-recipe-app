package guru.springframework.domain;

import lombok.*;
import org.springframework.data.annotation.Id;

@Getter
@Setter
public class Notes {

    @Id
    private String id;

    //TODO: To avoid circular dependency
    //private Recipe recipe;

    private String recipeNotes;
}
