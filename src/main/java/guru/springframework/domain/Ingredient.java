package guru.springframework.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(exclude = {"recipe"})
@ToString(exclude = {"recipe"})
@Entity
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;
    private BigDecimal amount;

    @OneToOne(fetch = FetchType.EAGER)
    private UnitOfMeasure uom;

    @ManyToOne
    private Recipe recipe;

    public Ingredient(){}

    public Ingredient(String description, BigDecimal amount, UnitOfMeasure uom) {
        this.description = description;
        this.amount = amount;
        this.uom = uom;
    }

    public Ingredient(String description, BigDecimal amount, UnitOfMeasure uom, Recipe recipe) {
        this.description = description;
        this.amount = amount;
        this.uom = uom;
        this.recipe = recipe;
    }

/*
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }*/

}

/*
Fix: exclude the bidirectional reference from HashCode implementation by lombok
Problem:  Recipe <--> Ingredient 1:Many relationship causes circular references!

java.lang.StackOverflowError: null
	at guru.springframework.domain.UnitOfMeasure.hashCode(UnitOfMeasure.java:10) ~[classes/:na]
	at guru.springframework.domain.Ingredient.hashCode(Ingredient.java:9) ~[classes/:na]
	at java.util.AbstractSet.hashCode(AbstractSet.java:126) ~[na:1.8.0_171]
	at guru.springframework.domain.Recipe.hashCode(Recipe.java:9) ~[classes/:na]
	at guru.springframework.domain.Ingredient.hashCode(Ingredient.java:9) ~[classes/:na]
	at java.util.AbstractSet.hashCode(AbstractSet.java:126) ~[na:1.8.0_171]
	at guru.springframework.domain.Recipe.hashCode(Recipe.java:9) ~[classes/:na]
	at guru.springframework.domain.Ingredient.hashCode(Ingredient.java:9) ~[classes/:na]
	at java.util.AbstractSet.hashCode(AbstractSet.java:126) ~[na:1.8.0_171]
	at guru.springframework.domain.Recipe.hashCode(Recipe.java:9) ~[classes/:na]
	at guru.springframework.domain.Ingredient.hashCode(Ingredient.java:9) ~[classes/:na]
	at java.util.AbstractSet.hashCode(AbstractSet.java:126) ~[na:1.8.0_171]
	at guru.springframework.domain.Recipe.hashCode(Recipe.java:9) ~[classes/:na]
	at guru.springframework.domain.Ingredient.hashCode(Ingredient.java:9) ~[classes/:na]
	at java.util.AbstractSet.hashCode(AbstractSet.java:126) ~[na:1.8.0_171]
	at guru.springframework.domain.Recipe.hashCode(Recipe.java:9) ~[classes/:na]
	at guru.springframework.domain.Ingredient.hashCode(Ingredient.java:9) ~[classes/:na]
	at java.util.AbstractSet.hashCode(AbstractSet.java:126) ~[na:1.8.0_171]
	at guru.springframework.domain.Recipe.hashCode(Recipe.java:9) ~[classes/:na]

	...
 */