package filtering.repository;

import filtering.model.Filter;
import filtering.model.FilterKey;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FilterRepository extends MongoRepository<Filter, FilterKey> {

}
