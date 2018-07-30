package guru.springframework.controllers;

import guru.springframework.commands.RecipeCommand;
import guru.springframework.exceptions.NotFoundException;
import guru.springframework.services.RecipeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.exceptions.TemplateInputException;
import reactor.core.publisher.Mono;
//import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.UUID;


@Slf4j
@Controller
public class RecipeController {

    private static final String RECIPE_RECIPEFORM_URL = "recipe/recipeform";
    private final RecipeService recipeService;

    // Fix the validation problem for webflux
    private WebDataBinder webDataBinder;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder){
        this.webDataBinder = webDataBinder;
    }

    @RequestMapping("recipe/{id}/show")
    public String showById(@PathVariable String id, Model model) {

        //NOTE: NumberFormatException if parseUnsignedLong() failed
        //log.debug("input: "  + id);
        //long tmp = Long.parseUnsignedLong(id.substring(0,16),16);
        //log.debug(Long.toHexString(tmp));
        //log.debug(Long.toHexString(Long.parseUnsignedLong(id.substring(0, id.length()>16? 16 : id.length()),16)));

        model.addAttribute("recipe", recipeService.findById(id));//.block()

        // parent or container represent each child (id)
        return "recipe/show";
    }

    //TODO: implement this?
    @RequestMapping("recipe/new")
    public String newRecipe(Model model) {

        model.addAttribute("recipe", recipeService.newRecipeCommand());
        return RECIPE_RECIPEFORM_URL;
    }

    @RequestMapping("recipe/{id}/update")
    public String updateRecipe(@PathVariable String id, Model model) {

        model.addAttribute("recipe", recipeService.findCommandById(id));//findCommandById(id).block()
        return RECIPE_RECIPEFORM_URL;
    }

    //NOTE: @Valid bail out before return RECIPE_RECIPEFORM_URL, and send the error message directly to user

    @PostMapping("recipe")
    public String saveOrUpdate(/*@Valid */@ModelAttribute("recipe") RecipeCommand command) {

        //Fix the validation problem for webflux
        webDataBinder.validate();
        BindingResult bindingResult = webDataBinder.getBindingResult();

        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(objectError -> {log.debug(objectError.toString());});

            return RECIPE_RECIPEFORM_URL;
        }

        Mono<RecipeCommand> savedCommand = recipeService.saveRecipeCommand(command);

        savedCommand.subscribe(cmd->{
            log.debug("Saved Recipe Id: " + cmd.getId());
            command.setId(cmd.getId());
        });

        return "redirect:/recipe/" + command.getId() + "/show";
    }


    @GetMapping("recipe/{id}/delete")
    public String deleteById(@PathVariable String id) {
        log.debug("Deleting id: " + id);

        recipeService.deleteById(id);
        return "redirect:/";
    }


    @ResponseStatus(HttpStatus.NOT_FOUND) //send 404 instead of 200
    @ExceptionHandler({NotFoundException.class, TemplateInputException.class})
    public String handleNotFound(Exception exception, Model model) {
        log.error("Handling not found exception");
        log.error(exception.getMessage());

        model.addAttribute("exception", exception);

        return "404error";
    }

}

/*
Fix: WebDataBinder webDataBinder;
Problem:
Java.lang.IllegalStateException: Failed to resolve argument 1 of type 'org.springframework.validation.BindingResult' on public java.lang.String guru.springframework.controllers.RecipeController.saveOrUpdate(guru.springframework.commands.RecipeCommand,org.springframework.validation.BindingResult)
 */