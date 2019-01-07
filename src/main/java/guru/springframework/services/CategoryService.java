package guru.springframework.services;

import guru.springframework.commands.CategoryCommand;
import reactor.core.publisher.Flux;

public interface CategoryService {
    Flux<CategoryCommand> populateCategoryList();
}
