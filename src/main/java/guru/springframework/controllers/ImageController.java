package guru.springframework.controllers;

import guru.springframework.commands.RecipeCommand;
import guru.springframework.domain.Recipe;
import guru.springframework.exceptions.NotFoundException;
import guru.springframework.services.ImageService;
import guru.springframework.services.RecipeService;
import lombok.extern.slf4j.Slf4j;
//import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

//import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

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

//    @RequestMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//    @PostMapping(path = "recipe/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public String handleImagePost(@PathVariable String id, @RequestPart("imagefile") Mono<FilePart> file) {
//    @PostMapping(path = "recipe/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public String handleImagePost(@PathVariable String id, @RequestPart("imagefile") Mono<MultiValueMap<String, Part>> file) {
//    public String handleImagePost(@PathVariable String id, @RequestPart("imagefile") Mono<FilePart> file) {
//
//        file.subscribe(filePart -> {
//            Set<String> keys =  filePart.keySet();
//            for (String key : keys) {
//                log.debug(key);
//            }
//        });
//        Mono<Recipe> img = imageService.saveImageFile(id, file);
//        img.subscribe(recipe -> {
//            log.debug("Save image for recipe id: " + recipe.getId());
//        });
//        return "redirect:/recipe/" + id + "/show";
//}

//    @PostMapping(path = "recipe/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//      public String handleImagePost(@PathVariable String id, @RequestPart("imagefile") FilePart file) {
//
//        imageService.saveImageFile(id, file).subscribe( recipe -> {
//            log.info("set image for recipe id: " + recipe.getId());
//        });
//        return "redirect:/recipe/" + id + "/show";
//      }

    @PostMapping(path = "recipe/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<String> handleImagePost(@PathVariable String id, @RequestPart("imagefile") FilePart file) {
        return imageService.saveImageFile(id, file)
                //.then(Mono.just("redirect:/recipe/" + id + "/show"));
                .map(recipe -> "redirect:/recipe/" + recipe.getId() + "/show");
    }

    //NOTE: call inside of /recipe/show.html as of src of the image
//    @GetMapping("recipe/{id}/recipeimage")
//    public void renderImageFromDB(@PathVariable String id, HttpServletResponse response) throws Exception {
//        //NOTE: NumberFormatException if parseUnsignedLong failed
//        log.debug(Long.toHexString(Long.parseUnsignedLong(id.substring(0, id.length()>16? 16 : id.length()),16)));
//
//        RecipeCommand recipeCommand = recipeService.findCommandById(id).block();
//
//        //NOTE: recipeCommand cannot be null findCommandById->recipeservice.findById() throw NotFoundException
//        if (recipeCommand.getImage() != null) {
//            byte[] byteArray = new byte[recipeCommand.getImage().length];
//
//            int i = 0;
//            for (Byte wrappedByte : recipeCommand.getImage()) {
//                byteArray[i++] = wrappedByte; //auto unboxing
//            }
//
//            response.setContentType("image/jpg");
//            InputStream is = new ByteArrayInputStream(byteArray);
//            IOUtils.copy(is, response.getOutputStream());
//        }
//    }

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


;//Fix:  USE import org.springframework.http.server.reactive.ServerHttpResponse;
//       NOT import org.springframework.http.server.reactive.ServerHttpResponse;
//
//Problem:
//    Caused by: java.lang.IllegalStateException: No primary or default constructor found for interface org.springframework.http.server.ServerHttpResponse
//        at org.springframework.web.reactive.result.method.annotation.ModelAttributeMethodArgumentResolver.createAttribute(ModelAttributeMethodArgumentResolver.java:213) ~[spring-webflux-5.0.7.RELEASE.jar:5.0.7.RELEASE]
//        at org.springframework.web.reactive.result.method.annotation.ModelAttributeMethodArgumentResolver.prepareAttributeMono(ModelAttributeMethodArgumentResolver.java:163) ~[spring-webflux-5.0.7.RELEASE.jar:5.0.7.RELEASE]
//        at org.springframework.web.reactive.result.method.annotation.ModelAttributeMethodArgumentResolver.resolveArgument(ModelAttributeMethodArgumentResolver.java:117) ~[spring-webflux-5.0.7.RELEASE.jar:5.0.7.RELEASE]
//        at org.springframework.web.reactive.result.method.InvocableHandlerMethod.resolveArg(InvocableHandlerMethod.java:214) ~[spring-webflux-5.0.7.RELEASE.jar:5.0.7.RELEASE]
//        ... 86 common frames omitted
//        Caused by: java.lang.NoSuchMethodException: org.springframework.http.server.ServerHttpResponse.<init>()
//        at java.lang.Class.getConstructor0(Class.java:3082) ~[na:1.8.0_181]
//        at java.lang.Class.getDeclaredConstructor(Class.java:2178) ~[na:1.8.0_181]
//        at org.springframework.web.reactive.result.method.annotation.ModelAttributeMethodArgumentResolver.createAttribute(ModelAttributeMethodArgumentResolver.java:210) ~[spring-webflux-5.0.7.RELEASE.jar:5.0.7.RELEASE]
//        ... 89 common frames omitted