package guru.springframework.controllers;

import guru.springframework.domain.Recipe;
import guru.springframework.services.RecipeService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

//@RunWith(SpringRunner.class)
//@WebFluxTest(IndexController.class)
public class IndexControllerTest {

    IndexController indexController;

    @Mock
    RecipeService recipeService;

    @Mock
    Model model;

    //@Autowired
    //WebTestClient webTestClient;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        indexController = new IndexController(recipeService);
    }

    @Ignore
    @Test
    public void testMocMVC() throws Exception {

//        webTestClient.get()
//                .uri("/")
//                .header(HttpHeaders.CONTENT_TYPE, "text/html")
//                .exchange()
//                .expectStatus().isOk();

//        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(indexController).build();
//
//        // ReactiveRecipeRepository return empty flux when no recipe
//        when(recipeService.getRecipes()).thenReturn(Flux.empty());
//
//        mockMvc.perform(get("/"))
//                .andExpect(status().isOk())
//                .andExpect(view().name("index"));
    }

    @Test
    public void getIndexPageTest() {
        //given
        List<Recipe> recipes = new ArrayList<>();
        recipes.add(new Recipe());
        Recipe recipe = new Recipe();
        recipe.setId("100");
        recipes.add(recipe);

        when(recipeService.getRecipes()).thenReturn(Flux.fromIterable(recipes));

        // argument captor of the 2nd argument of model.addAttribute("recipes", recipeService.getRecipes());
        //ArgumentCaptor<List<Recipe>> argumentCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<Flux<Recipe>> argumentCaptor = ArgumentCaptor.forClass(Flux.class);

        //when
        String viewName = indexController.getIndexPage(model);

        //then
        assertEquals("index", viewName);

        verify(recipeService, times(1)).getRecipes();

        //default argument matcher anySet()
        //verify(model, times(1)).addAttribute(eq("recipes"), anySet());
        verify(model, times(1)).addAttribute(eq("recipes"), argumentCaptor.capture());

        //List<Recipe> setInController = argumentCaptor.getValue();
        //assertEquals(2L, setInController.size());
        assertEquals(2, argumentCaptor.getValue().collectList().block().size());
    }

    @Test
    public void getIndexPageEmptyFluxTest() {
        //given
        when(recipeService.getRecipes()).thenReturn(Flux.empty());

        ArgumentCaptor<List<Recipe>> argumentCaptor = ArgumentCaptor.forClass(List.class);

        //when
        String viewName = indexController.getIndexPage(model);

        //then
        assertEquals("index", viewName);

        verify(recipeService, times(1)).getRecipes();

        verify(model, times(1)).addAttribute(eq("recipes"), argumentCaptor.capture());

        //List<Recipe> setInController = argumentCaptor.getValue();
        //assertEquals(0L, setInController.size());

        assertEquals(Flux.empty(), argumentCaptor.getValue());
    }

    @Test
    public void getIndexPageEmptyLisTest() {
        //given
        List<Recipe> recipes = new ArrayList<>();

        when(recipeService.getRecipes()).thenReturn(Flux.fromIterable(recipes));

        //ArgumentCaptor<List<Recipe>> argumentCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<Flux<Recipe>> argumentCaptor = ArgumentCaptor.forClass(Flux.class);

        //when
        String viewName = indexController.getIndexPage(model);

        //then
        assertEquals("index", viewName);

        verify(recipeService, times(1)).getRecipes();

        verify(model, times(1)).addAttribute(eq("recipes"), argumentCaptor.capture());

        //List<Recipe> setInController = argumentCaptor.getValue();
        //assertEquals(0L, setInController.size());
        assertEquals(0,  argumentCaptor.getValue().collectList().block().size());
    }
}

