package guru.springframework.services;

import guru.springframework.domain.Recipe;
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
import java.util.Vector;

@Slf4j
@Service
public class ImageServiceImpl implements ImageService {

    private final RecipeReactiveRepository recipeReactiveRepository;

    public ImageServiceImpl(RecipeReactiveRepository recipeRepository) {
        this.recipeReactiveRepository = recipeRepository;
    }

    //@Transactional
    //@Override
//    //public Mono<Void> saveImageFile(String recipeId, MultipartFile file) {
//
//        Mono<Recipe> recipeMono = recipeReactiveRepository.findById(recipeId)
//                .flatMap(recipe -> {
//                    try {
//                        Byte[] byteObjects = new Byte[file..getBytes().length];
//
//                        int i = 0;
//
//                        for (byte b : file.getBytes()) {
//                            byteObjects[i++] = b;
//                        }
//
//                        recipe.setImage(byteObjects);
//
//                        return recipe;
//
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                        throw new RuntimeException(e);
//                    }
//
//                });
//        // wait forever
//        //.publish(recipeMono ->recipeReactiveRepository.save(recipeMono.block())).block();
//
//        recipeReactiveRepository.save(recipeMono.block()).block();
//
//        return Mono.empty();
//    }

    @Override
    public Mono<Recipe> saveImageFile(String recipeId, Mono<FilePart> file) {

        Mono<Recipe> recipeMono = recipeReactiveRepository
                .findById(recipeId)
                .flatMap(recipe -> {
                    Mono<Recipe> recipeImage = file
                    //.filter(filePart -> filePart instanceof FilePart)
                    .flatMap( filePart -> {
                        Vector<Byte> imgData = new Vector<>();
                        try {
                            filePart.content()
                                    .doOnEach(dataBufferSignal -> {
                                        if (dataBufferSignal.hasValue() && !dataBufferSignal.hasError()) {
                                            DataBuffer dataBuffer = dataBufferSignal.get();
                                            byte[] bytes = new byte[dataBuffer.readableByteCount()];
                                            dataBuffer.read(bytes);
                                            for(byte b : bytes) {
                                                imgData.add(b);
                                            }
                                        }
                                    })//doOnEach
                                    .doOnComplete(()->{
                                        Byte[] ts = new Byte[imgData.size()];
                                        imgData.toArray(ts);
                                        recipe.setImage(ts);
                                    });//doOnComplete

                        } catch (Exception e) {

                            e.printStackTrace();
                            throw new RuntimeException(e);
                        }
                        return recipeReactiveRepository.save(recipe);
                    })//file.map
                    .switchIfEmpty(Mono.error(new NotFoundException("Image File Part Not Found")));

                    return recipeImage;

                });//flatMap

        return recipeMono;
    }

}