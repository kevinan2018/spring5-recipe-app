package guru.springframework.domain;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Document
public class Recipe {

    @Id
    private String id;

    private String description;
    private Integer prepTime;
    private Integer cookTime;
    private Integer servings;
    private String source;
    private String url;
    private String directions;
    private Set<Ingredient> ingredients = new HashSet<>();
    private Byte[] image; //byte[] can be null, springs team recommends using Byte[] instead!
    private Difficulty difficulty;
    private Notes notes;

    @DBRef
    private Set<Category> categories = new HashSet<>();

    /*
     * lombox @Data won't work here!
     * setNotes and addIngredient have to be created manually
     */
    public void setNotes(Notes notes) {
        if (notes != null) {
            this.notes = notes;

            // bidirectional relationship with notes
            //TODO: To avoid circular dependency
            //notes.setRecipe(this);
        }
    }

    // bidirectional relationship with ingredient
    public Recipe addIngredient(Ingredient ingredient){
        if (ingredient != null) {
            this.ingredients.add(ingredient);
            //TODO: To avoid circular dependency
            //ingredient.setRecipe(this);
        }
        return this;
    }

}
