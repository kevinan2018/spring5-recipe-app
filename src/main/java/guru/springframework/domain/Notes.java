package guru.springframework.domain;

import lombok.*;
import org.springframework.data.annotation.Id;

import java.util.UUID;

@Getter
@Setter
public class Notes {

    //@Id won't work with mongo db unless with @Document
    private String id = UUID.randomUUID().toString().replace("-", "");

    //TODO: To avoid circular dependency
    //private Recipe recipe;

    private String recipeNotes;
}
