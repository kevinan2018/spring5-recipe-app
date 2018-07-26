package guru.springframework.controllers;

import guru.springframework.commands.RecipeCommand;
import guru.springframework.services.ImageService;
import guru.springframework.services.RecipeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Controller
public class ImageController {
    private final ImageService imageService;
    private final RecipeService recipeService;

    public ImageController(ImageService imageService, RecipeService recipeService) {
        this.imageService = imageService;
        this.recipeService = recipeService;
    }

    @GetMapping("recipe/{id}/image")
    public String showUploadForm(@PathVariable String id, Model model) {
        model.addAttribute("recipe", recipeService.findCommandById(id).block());

        return "recipe/imageuploadform";
    }

    @PostMapping("recipe/{id}/image")
    public String handleImagePost(@PathVariable String id, @RequestParam("imagefile")MultipartFile file) {

        imageService.saveImageFile(id, file);

        return "redirect:/recipe/" + id + "/show";
    }

    //NOTE: call inside of /recipe/show.html as of src of the image
    @GetMapping("recipe/{id}/recipeimage")
    public void renderImageFromDB(@PathVariable String id, HttpServletResponse response) throws Exception {

        //NOTE: NumberFormatException if parseUnsignedLong failed
        log.debug(Long.toHexString(Long.parseUnsignedLong(id.substring(0, id.length()>16? 16 : id.length()),16)));

        RecipeCommand recipeCommand = recipeService.findCommandById(id).block();

        //NOTE: recipeCommand cannot be null findCommandById->recipeservice.findById() throw NotFoundException
        if (recipeCommand.getImage() != null) {
            byte[] byteArray = new byte[recipeCommand.getImage().length];

            int i = 0;
            for (Byte wrappedByte : recipeCommand.getImage()) {
                byteArray[i++] = wrappedByte; //auto unboxing
            }

            response.setContentType("image/jpg");
            InputStream is = new ByteArrayInputStream(byteArray);
            IOUtils.copy(is, response.getOutputStream());
        }
    }

}
