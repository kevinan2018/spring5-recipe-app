package guru.springframework.services;

import guru.springframework.domain.Recipe;
import org.springframework.http.codec.multipart.FilePart;
//import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

public interface ImageService {
    //Mono<Void> saveImageFile(String recipeId, MultipartFile file);
    //Mono<Recipe> saveImageFile(String recipeId, Mono<FilePart> file);
    Mono<Recipe> saveImageFile(String recipeId, FilePart file);
}
