package guru.springframework.domain;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.websocket.server.ServerEndpoint;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class Ingredient {

    //@Id won't work since Ingredient is no a document?
    private String id = UUID.randomUUID().toString().replace("-", "");

    private String description;
    private BigDecimal amount;

    @DBRef
    private UnitOfMeasure uom;

    //TODO: To avoid circular dependency
    //private Recipe recipe;

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
        //TODO: To avoid circular dependency
        //this.recipe = recipe;
    }

}
