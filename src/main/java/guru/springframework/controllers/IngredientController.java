package guru.springframework.controllers;

import guru.springframework.commands.IngredientCommand;
import guru.springframework.commands.RecipeCommand;
import guru.springframework.commands.UnitOfMeasureCommand;
import guru.springframework.exceptions.NotFoundException;
import guru.springframework.services.IngredientService;
import guru.springframework.services.RecipeService;
import guru.springframework.services.UnitOfMeasureService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
public class IngredientController {

    private final String INTREDIENT_INTREDIENTFORM_URL = "recipe/ingredient/ingredientform";
    private final RecipeService recipeService;
    private final IngredientService ingredientService;
    private final UnitOfMeasureService unitOfMeasureService;

    private WebDataBinder webDataBinder;

    public IngredientController(RecipeService recipeService, IngredientService ingredientService, UnitOfMeasureService unitOfMeasureService) {
        this.recipeService = recipeService;
        this.ingredientService = ingredientService;
        this.unitOfMeasureService = unitOfMeasureService;
    }

    @InitBinder("ingredient")
    public void initBinder(WebDataBinder webDataBinder) {
        this.webDataBinder= webDataBinder;
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
        model.addAttribute("ingredient", ingredientService.findByRecipeIdAndIngredientId(recipeId, id));

        return "recipe/ingredient/show";
    }

    @GetMapping("/recipe/{recipeId}/ingredient/{id}/update")
    public String updateRecipeIngredient(@PathVariable String recipeId,
                                         @PathVariable String id, Model model){
        model.addAttribute("ingredient", ingredientService.findByRecipeIdAndIngredientId(recipeId, id));
        return INTREDIENT_INTREDIENTFORM_URL;
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
    public Mono<String> saveOrUpdate(@ModelAttribute("ingredient") IngredientCommand command, Model model) {

        webDataBinder.validate();
        BindingResult bindingResult = webDataBinder.getBindingResult();

        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(objectError -> {log.debug(objectError.toString());});

            //model.addAttribute("uomList", unitOfMeasureService.listAllUoms());
            return Mono.just(INTREDIENT_INTREDIENTFORM_URL);
        }

        Mono<IngredientCommand> ingredientCommandMono = ingredientService.saveIngredientCommand(command);
        return ingredientCommandMono
                .map( cmd -> "redirect:/recipe/" + cmd.getRecipeId() + "/ingredient/" + cmd.getId() + "/show");

//        ingredientCommandMono.subscribe(cmd -> {
//            // set ingredient id
//            command.setId(cmd.getId());
//
//            log.debug("saved recipe id:" + cmd.getRecipeId());
//            log.debug("saved ingredient id:" + cmd.getId());
//
//        });
//        while(command.getId().isEmpty());
//        return "redirect:/recipe/" + command.getRecipeId() + "/ingredient/" + command.getId() + "/show";

    }

    @GetMapping("recipe/{recipeId}/ingredient/new")
    public Mono<String> newIngredient(@PathVariable String recipeId, Model model) {
        return recipeService.findCommandById(recipeId)
                .flatMap(
                    recipeCommand -> {
                        //need to return back parent id for hidden form prperty
                        IngredientCommand ingredientCommand = new IngredientCommand();
                        ingredientCommand.setRecipeId(recipeId);
                        model.addAttribute("ingredient", ingredientCommand);
                        //init uom
                        ingredientCommand.setUom(new UnitOfMeasureCommand());

                        model.addAttribute("recipe", ingredientCommand);
                        return Mono.just(INTREDIENT_INTREDIENTFORM_URL);
                })
                .switchIfEmpty(Mono.error(new NotFoundException("Recipe Id :" + recipeId + " Not Found")));
    }

    @ModelAttribute("uomList")
    public Flux<UnitOfMeasureCommand> populateUomList() {
        return unitOfMeasureService.listAllUoms();
    }

}

/*
Fix: @ModelAttribute("ingredient")
Problem: when validation failed
org.springframework.expression.spel.SpelEvaluationException: EL1011E: Method call: Attempted to call method getRecipeId() on null context object
 */