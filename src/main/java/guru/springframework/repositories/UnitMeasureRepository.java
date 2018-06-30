package guru.springframework.repositories;

import guru.springframework.domain.UnitOfMeasure;
import org.springframework.data.repository.CrudRepository;

public interface UnitMeasureRepository extends CrudRepository<UnitOfMeasure, Long> {
}
