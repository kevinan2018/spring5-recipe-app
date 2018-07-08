package guru.springframework.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;

@Data
@EqualsAndHashCode(exclude = {"recipe"})
@ToString(exclude = {"recipe"})
@Entity
public class Notes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Recipe recipe;

    @Lob
    private String recipeNotes;

    /*   public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public String getRecipeNotes() {
        return recipeNotes;
    }

    public void setRecipeNotes(String recipeNotes) {
        this.recipeNotes = recipeNotes;
    }*/

}

/*
Fix: exclude the bidirectional reference from HashCode implementation by lombok
Problem:  Notes <--> Recipes 1:1 relationship causes circular references!


java.lang.StackOverflowError: null
	at java.util.HashSet.iterator(HashSet.java:173) ~[na:1.8.0_171]
	at java.util.AbstractSet.hashCode(AbstractSet.java:122) ~[na:1.8.0_171]
	at guru.springframework.domain.Recipe.hashCode(Recipe.java:9) ~[classes/:na]
	at guru.springframework.domain.Notes.hashCode(Notes.java:7) ~[classes/:na]
	at guru.springframework.domain.Recipe.hashCode(Recipe.java:9) ~[classes/:na]
	at guru.springframework.domain.Notes.hashCode(Notes.java:7) ~[classes/:na]
	at guru.springframework.domain.Recipe.hashCode(Recipe.java:9) ~[classes/:na]
	at guru.springframework.domain.Notes.hashCode(Notes.java:7) ~[classes/:na]
	at guru.springframework.domain.Recipe.hashCode(Recipe.java:9) ~[classes/:na]
	at guru.springframework.domain.Notes.hashCode(Notes.java:7) ~[classes/:na]
	at guru.springframework.domain.Recipe.hashCode(Recipe.java:9) ~[classes/:na]

*/