package guru.springframework.controllers;

import guru.springframework.commands.RecipeCommand;
import guru.springframework.services.ImageService;
import guru.springframework.services.RecipeService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ImageControllerTest {
    @Mock
    ImageService imageService;

    @Mock
    RecipeService recipeService;

    ImageController controller;

    MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        controller = new ImageController(imageService, recipeService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new ControllerExceptionHandler()) //wire the controller advice
                .build();
    }

    @Ignore
    @Test
    public void getImageFormTest() throws Exception {
        //given
        RecipeCommand command = new RecipeCommand();
        command.setId("1");

        when(recipeService.findCommandById(anyString())).thenReturn(Mono.just(command));

        //when
        ResultActions resultActions = mockMvc.perform(get("/recipe/1/image"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("recipe"));

        verify(recipeService, times(1)).findCommandById(anyString());
    }

    //TODO
    @Ignore
    @Test
    public void handleImagePostTest() throws Exception {
//        MockMultipartFile multipartFile = new MockMultipartFile("imagefile", "testing.txt", "text/plain", "Spring Framework Guru".getBytes());
//
//        when(imageService.saveImageFile(anyString(), Mockito.<MultipartFile>any())).thenReturn(Mono.empty());
//
//        mockMvc.perform(multipart("/recipe/1/image").file(multipartFile))//upload file request
//                .andExpect(status().is3xxRedirection())
//                .andExpect(header().string("Location", "/recipe/1/show"));
//
//        verify(imageService, times(1)).saveImageFile(anyString(), any());
    }

    //TODO
    @Ignore
    @Test
    public void renderImageFromDBTest() throws Exception {

//        //given
//        RecipeCommand command = new RecipeCommand();
//        command.setId("1");
//
//        String s = "fake image text";
//        Byte[] bytesBoxed = new Byte[s.getBytes().length];
//
//        int i = 0;
//        for (byte primByte : s.getBytes()) {
//            bytesBoxed[i++] = primByte;
//        }
//
//        command.setImage(bytesBoxed);
//
//        when(recipeService.findCommandById(anyString())).thenReturn(Mono.just(command));
//
//        //when
//        MockHttpServletResponse response = mockMvc.perform(get("/recipe/1/recipeimage"))
//                .andExpect(status().isOk())
//                .andReturn().getResponse();//return response object
//
//        byte[] responseBytes = response.getContentAsByteArray();
//
//        //then
//        assertEquals(s.getBytes().length, responseBytes.length);

    }

    @Ignore
    @Test
    public void getImageNumberFormatException() throws Exception {
        mockMvc.perform(get("/recipe/asdf/recipeimage"))
                .andExpect(status().isBadRequest())
                .andExpect(view().name("400error"));
    }

}
