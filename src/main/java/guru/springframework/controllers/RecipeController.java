package guru.springframework.controllers;

import guru.springframework.commands.RecipeCommand;
import guru.springframework.exceptions.NotFoundException;
import guru.springframework.services.RecipeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.UUID;


@Slf4j
@Controller
public class RecipeController {

    private static final String RECIPE_RECIPEFORM_URL = "recipe/recipeform";
    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @RequestMapping("recipe/{id}/show")
    public String showById(@PathVariable String id, Model model) {

        //NOTE: NumberFormatException if parseUnsignedLong() failed
        //log.debug("input: "  + id);
        //long tmp = Long.parseUnsignedLong(id.substring(0,16),16);
        //log.debug(Long.toHexString(tmp));
        log.debug(Long.toHexString(Long.parseUnsignedLong(id.substring(0, id.length()>16? 16 : id.length()),16)));

        model.addAttribute("recipe", recipeService.findById(id));

        // parent or container represent each child (id)
        return "recipe/show";
    }

    @RequestMapping("recipe/new")
    public String newRecipe(Model model) {
        model.addAttribute("recipe", new RecipeCommand());
        return RECIPE_RECIPEFORM_URL;
    }

    @RequestMapping("recipe/{id}/update")
    public String updateRecipe(@PathVariable String id, Model model) {
        /*
        RecipeCommand command = recipeService.findCommandById(Long.valueOf(id));

        for(IngredientCommand ingredient : command.getIngredients()) {
            String desc = ingredient.getUom().getDescription();
        }
        */
        model.addAttribute("recipe", recipeService.findCommandById(id));
        return "recipe/recipeform";
    }


    @PostMapping("recipe")
    public String saveOrUpdate(@Valid @ModelAttribute("recipe") RecipeCommand command, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(objectError -> {log.debug(objectError.toString());});
            return RECIPE_RECIPEFORM_URL;
        }


        RecipeCommand savedCommand = recipeService.saveRecipeCommand(command);

        return "redirect:/recipe/" + savedCommand.getId() + "/show";
    }


    @GetMapping("recipe/{id}/delete")
    public String deleteById(@PathVariable String id) {
        log.debug("Deleting id: " + id);

        recipeService.deleteById(id);
        return "redirect:/";
    }


    @ResponseStatus(HttpStatus.NOT_FOUND) //send 404 instead of 200
    @ExceptionHandler(NotFoundException.class)
    public ModelAndView handleNotFound(Exception exception) {
        log.error("Handling not found exception");
        log.error(exception.getMessage());

        ModelAndView modelAndView = new ModelAndView();

        modelAndView.setViewName("404error");
        modelAndView.addObject("exception", exception);

        return modelAndView;
    }

}
