package guru.springframework.controllers;

import guru.springframework.services.ImageService;
import guru.springframework.services.RecipeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@Controller
//@RestController
public class ImageController {
    private final ImageService imageService;
    private final RecipeService recipeService;

    public ImageController(ImageService imageService, RecipeService recipeService) {
        this.imageService = imageService;
        this.recipeService = recipeService;
    }

    @GetMapping("recipe/{id}/image")
    public String showUploadForm(@PathVariable String id, Model model) {
        model.addAttribute("recipe", recipeService.findCommandById(id));//.block()

        return "recipe/imageuploadform";
    }

    @PostMapping(path = "recipe/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<String> handleImagePost(@PathVariable String id, @RequestPart("imagefile") FilePart file) {
        return imageService.saveImageFile(id, file)
                //.then(Mono.just("redirect:/recipe/" + id + "/show"));
                .map(recipe -> "redirect:/recipe/" + recipe.getId() + "/show");
    }

    //NOTE: call inside of /recipe/show.html as of src of the image
    //Solution 1
//    @GetMapping("recipe/{id}/recipeimage")
//    public Mono<ResponseEntity<byte[]>> renderImageFromDB(@PathVariable String id) throws Exception {
//        //NOTE: NumberFormatException if parseUnsignedLong failed
//        log.debug(Long.toHexString(Long.parseUnsignedLong(id.substring(0, id.length()>16? 16 : id.length()),16)));
//
//        return recipeService.findCommandById(id).map(recipeCommand -> {
//
//            //NOTE: recipeCommand cannot be null findCommandById->recipeservice.findById() throw NotFoundException
//            if (recipeCommand.getImage() != null) {
//                byte[] byteArray = new byte[recipeCommand.getImage().length];
//
//                int i = 0;
//                for (Byte wrappedByte : recipeCommand.getImage()) {
//                    byteArray[i++] = wrappedByte; //auto unboxing
//                }
//
//                return ResponseEntity.ok()
//                        .header("contentType", "image/jpg")
//                        .body(byteArray);
//            }
//            return ResponseEntity.ok()
//                    .header("contentType", "image/jpg")
//                    .body(new byte[0]);
//            }).switchIfEmpty(Mono.error(new NotFoundException("Recipe id:" + id + " not found")));
//    }


    //Solution 2
    @GetMapping("recipe/{id}/recipeimage")
    public Mono<Void> renderImageFromDB(@PathVariable String id, ServerHttpResponse response) throws Exception {
        //NOTE: NumberFormatException if parseUnsignedLong failed
        log.debug(Long.toHexString(Long.parseUnsignedLong(id.substring(0, id.length()>16? 16 : id.length()),16)));

        return recipeService.findCommandById(id).flatMap( recipeCommand -> {

            //NOTE: recipeCommand cannot be null findCommandById->recipeservice.findById() throw NotFoundException
            response.getHeaders().setContentType(MediaType.IMAGE_JPEG);
            if (recipeCommand.getImage() != null) {
                byte[] byteArray = new byte[recipeCommand.getImage().length];

                int i = 0;
                for (Byte wrappedByte : recipeCommand.getImage()) {
                    byteArray[i++] = wrappedByte; //auto unboxing
                }

                return response.writeWith(Mono.just(response.bufferFactory().wrap(byteArray)));
            }
            return Mono.empty();//response.writeWith(Mono.create(s->s.success()));
        });
    }

}
