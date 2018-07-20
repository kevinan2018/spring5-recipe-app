package guru.springframework.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;
    private Integer prepTime;
    private Integer cookTime;
    private Integer servings;
    private String source;
    private String url;

    /* Fixed with Lob
    Caused by: org.h2.jdbc.JdbcSQLException: Value too long for column "DIRECTIONS VARCHAR(255)": "STRINGDECODE('1 Cut avocado, remove flesh: Cut the avocados in half. Remove seed. Score the inside of the avocado with a blunt k... (1348)";
    SQL statement: insert into recipe (id, cook_time, description, difficulty, directions, image, notes_id, prep_time, servings, source, url) values (null, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) [22001-197]
    */
    @Lob
    private String directions;

    //NOTE: mappedBy specifies the target property in ingredient Entity
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "recipe")
    private Set<Ingredient> ingredients = new HashSet<>();

    @Lob
    private Byte[] image; //byte[] can be null, springs team recommends using Byte[] instead!

    @Enumerated(value = EnumType.STRING)
    private Difficulty difficulty;

    @OneToOne(cascade = CascadeType.ALL)
    private Notes notes;

    @ManyToMany
    @JoinTable(name = "recipe_category", joinColumns = @JoinColumn(name = "recipe_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Set<Category> categories = new HashSet<>();


    /*
     * lombox @Data won't work here!
     * setNotes and addIngredient have to be created manually
     */
    public void setNotes(Notes notes) {
        if (notes != null) {
            this.notes = notes;
            // bidirectional relationship with notes
            notes.setRecipe(this);
        }
    }

    // bidirectional relationship with ingredient
    public Recipe addIngredient(Ingredient ingredient){
        if (ingredient != null) {
            ingredient.setRecipe(this);
            this.ingredients.add(ingredient);
        }
        return this;
    }

}
