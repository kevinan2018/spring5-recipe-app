package guru.springframework.controllers;

import guru.springframework.commands.IngredientCommand;
import guru.springframework.commands.RecipeCommand;
import guru.springframework.commands.UnitOfMeasureCommand;
import guru.springframework.services.IngredientService;
import guru.springframework.services.RecipeService;
import guru.springframework.services.UnitOfMeasureService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@Controller
public class IngredientController {
    private final RecipeService recipeService;
    private final IngredientService ingredientService;
    private final UnitOfMeasureService unitOfMeasureService;

    public IngredientController(RecipeService recipeService, IngredientService ingredientService, UnitOfMeasureService unitOfMeasureService) {
        this.recipeService = recipeService;
        this.ingredientService = ingredientService;
        this.unitOfMeasureService = unitOfMeasureService;
    }

    @GetMapping("/recipe/{recipeId}/ingredients")
    public String ListIngredients(@PathVariable String recipeId, Model model) {
        log.debug("Getting ingredient list for recipe id: " + recipeId);

        // use command object to avoid lazy load errors in Thymeleaf.
        model.addAttribute("recipe", recipeService.findCommandById(recipeId));//.block()

        return "recipe/ingredient/list";
    }

    @GetMapping("/recipe/{recipeId}/ingredient/{id}/show")
    public String showRecipeIngredient(@PathVariable String recipeId,
                                       @PathVariable String id, Model model) {
        model.addAttribute("ingredient", ingredientService.findByRecipeIdAndIngredientId(recipeId, id));//.block()

        return "recipe/ingredient/show";
    }

    @GetMapping("/recipe/{recipeId}/ingredient/{id}/update")
    public String updateRecipeIngredient(@PathVariable String recipeId,
                                         @PathVariable String id, Model model){
        model.addAttribute("ingredient", ingredientService.findByRecipeIdAndIngredientId(recipeId, id));//.block()
        model.addAttribute("uomList", unitOfMeasureService.listAllUoms());//.collectList().block()
        return "recipe/ingredient/ingredientform";
    }

    @GetMapping("/recipe/{recipeId}/ingredient/{id}/delete")
    public String deleteRecipeIngredient(@PathVariable String recipeId,
                                         @PathVariable String id){
        //delete reference from recipe's ingredient list, the ingredient still exists inside of ingredient repository
        ingredientService.deleteByRecipeIdAndIngredientId(recipeId, id).subscribe(
                recipe -> log.debug("Delete ingredient " + id)
        );

        return "redirect:/recipe/" + recipeId + "/ingredients";
    }

    @PostMapping("/recipe/{recipeId}/ingredient")
    public String saveOrUpdate(@ModelAttribute IngredientCommand command, Model model) {

        // NOTE; Model addAttribute triggers the operations
        model.addAttribute("ingredient",  ingredientService.saveIngredientCommand(command));
        return "recipe/ingredient/show";

//        Mono<IngredientCommand> ingredientCommandMono = ingredientService.saveIngredientCommand(command);
//
//        ingredientCommandMono.subscribe(cmd -> {
//            // set ingredient id
//            command.setId(cmd.getId());
//
//            log.debug("saved recipe id:" + cmd.getRecipeId());
//            log.debug("saved ingredient id:" + cmd.getId());
//
//        });
//        return "redirect:/recipe/" + command.getRecipeId() + "/ingredient/" + command.getId() + "/show";

    }

    @GetMapping("recipe/{recipeId}/ingredient/new")
    public String newRecipe(@PathVariable String recipeId, Model model) {
        //TODO: check reicipe id
        //RecipeCommand recipeCommand = recipeService.findCommandById(recipeId).block();

        //need to return back parent id for hidden form prperty
        IngredientCommand ingredientCommand = new IngredientCommand();
        ingredientCommand.setRecipeId(recipeId);
        model.addAttribute("ingredient", ingredientCommand);
        //init uom
        ingredientCommand.setUom(new UnitOfMeasureCommand());

        model.addAttribute("recipe", ingredientCommand);
        model.addAttribute("uomList", unitOfMeasureService.listAllUoms());//.collectList().block()

        return "recipe/ingredient/ingredientform";
    }
}
