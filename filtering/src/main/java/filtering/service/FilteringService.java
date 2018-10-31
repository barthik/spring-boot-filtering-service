package filtering.service;

import filtering.model.Filter;
import filtering.model.FilterKey;
import filtering.repository.FilterRepository;
import filtering.service.filtering.FilterableProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class FilteringService {
    private final FilterRepository filterRepository;

    @Autowired
    public FilteringService(FilterRepository filterRepository) {
        this.filterRepository = filterRepository;
    }

    public Filter findOne(String identifier, String className) {
        return filterRepository.findById(new FilterKey(identifier, className))
                .orElse(new Filter(new FilterKey(identifier, className), new HashMap<>()));
    }

    public void updateFilters(String identifier, String className, Map<String, Object> filters) {
        final Filter filter = findOne(identifier, className);
        filter.getFilters().putAll(filters);
        filterRepository.save(filter);
    }

    public <T> void updateFilters(T entity, String identifier) {
        String className = entity.getClass().getName();

        Filter filter = findOne(identifier, className);
        filter.setFilters(FilterableProcessor.discover(entity));

        filterRepository.save(filter);
    }
}
