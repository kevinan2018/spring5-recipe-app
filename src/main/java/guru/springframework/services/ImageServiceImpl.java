package guru.springframework.services;

import guru.springframework.domain.Recipe;
import guru.springframework.exceptions.BadRequestException;
import guru.springframework.exceptions.NotFoundException;
import guru.springframework.repositories.reactive.RecipeReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.core.io.buffer.DataBuffer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

@Slf4j
@Service
public class ImageServiceImpl implements ImageService {

    private final RecipeReactiveRepository recipeReactiveRepository;

    public ImageServiceImpl(RecipeReactiveRepository recipeRepository) {
        this.recipeReactiveRepository = recipeRepository;
    }

    @Override
    public Mono<Recipe> saveImageFile(String recipeId, FilePart filePart) {
        if (filePart == null) return Mono.empty();

        return recipeReactiveRepository
                .findById(recipeId)
                .flatMap(recipe -> {

                    Vector<Byte> imgData = new Vector<>();


                    return filePart.content()
                                .doOnEach(dataBufferSignal -> {
                                    if (dataBufferSignal.hasValue() && !dataBufferSignal.hasError()) {
                                        DataBuffer dataBuffer = dataBufferSignal.get();

                                        for(byte b : dataBuffer.asByteBuffer().array()) {
                                            imgData.add(b);
                                        }
                                    }
                                })//doOnEach
                                .doOnComplete(()->{
                                    recipe.setImage(imgData.toArray(new Byte[imgData.size()]));
                                })//doOnComplete
                                .then(recipeReactiveRepository.save(recipe))
                                .onErrorMap(e -> {
                                    log.error(e.getMessage());
                                    return new BadRequestException("Image could not be uploaded: " + e.getMessage());
                                });

                });//flatMap

    }//saveImageFile

}//ImageServiceImpl