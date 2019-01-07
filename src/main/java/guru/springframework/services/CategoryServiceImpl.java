package guru.springframework.services;

import guru.springframework.commands.CategoryCommand;
import guru.springframework.converters.CategoryToCategoryCommand;
import guru.springframework.repositories.reactive.CategoryReactiveRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class CategoryServiceImpl implements CategoryService {

    private CategoryReactiveRepository categoryReactiveRepository;
    private CategoryToCategoryCommand categoryToCategoryCommand;

    public CategoryServiceImpl(CategoryReactiveRepository categoryReactiveRepository,
                               CategoryToCategoryCommand categoryToCategoryCommand) {
        this.categoryReactiveRepository = categoryReactiveRepository;
        this.categoryToCategoryCommand = categoryToCategoryCommand;
    }

    @Override
    public Flux<CategoryCommand> populateCategoryList() {
        return categoryReactiveRepository
                .findAll()
                .map(categoryToCategoryCommand::convert);
    }
}
