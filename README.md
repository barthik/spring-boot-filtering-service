# Spring Boot 2 + MongoDB + Filtering Service

Example of collecting filter structures for the filtering service.

The code provides discovery and processing of fields annotated with `@Filterable`. Annotated field will proceed for filters gathering. Annotation can be used on primitives, complex objects, collections, etc - filter value is structureless. By default, only the first level of filters are collected (only fields of the top class).

Object structures are represented by the key consists of fields name in camel-case format separated by key separator (dot by default).

Fields with `null` values are ignored.

The filter document to store filters in MongoDB:

```java
@Data
@Document
public class Filter {
    @Id
    private FilterKey id;
    private Map<String, Object> filters;
}

@Value
public class FilterKey {
    private String identifier;
    private String className;
}
```

To discover fields use `<T> Map<String, Object> discover(T entity)` method in the `FilterableProcessor` class. You can simply define a service to handle filters update:

```java
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
```
Consider some messaging broker to update precise filters.

---

Example (note that object is usually DB entity - in this case, simple java object):
```java
@Data
public class Foo {
    private String id;
    @Filterable
    private String name;
    @Filterable
    private Double number;
    private int ignoredField;
}
```

The example above will result in:
```json
{
    "_id" : {
        "identifier" : "91ed8b26-021b-4701-aaef-3ca43d8b3cd6",
        "className" : "filtering.Foo"
    },
    "filters" : {
        "name" : "some value",
        "number" : 123.0
    },
    "_class" : "filtering.model.Filter"
}
```

Use grab value to collect only specific field from the nested object - usually the id field:
```java
@Data
public class AnotherFoo {
    private String id;
    @Filterable(grab = "id")
    private Foo foo;
}
```

Results in:
```json
{
    "_id" : {
        "identifier" : "6539b44e-287d-4002-89b2-a9fdf874e93a",
        "className" : "filtering.AnotherFoo"
    },
    "filters" : {
        "foo.id" : "91ed8b26-021b-4701-aaef-3ca43d8b3cd6",
    },
    "_class" : "filtering.model.Filter"
}
```

The recursive search is available, just set the deep dive to `true`. If field referencing another entity is annotated with deep dive, all annotated fields from this entity will be collected:

```java
@Data
public class ComplexFoo {
    private String id;
    @Filterable
    private String name;
    @Filterable(deepDive = true)
    private Foo foo;
}
```

Results in:
```json
{
     "_id" : {
        "identifier" : "91ed8b26-021b-4701-aaef-3ca43d8b3cd6",
        "className" : "filtering.Foo"
    },
    "filters" : {
        "name" : "another value",
        "foo.name" : "some value",
        "foo.number" : 123.0
    },
    "_class" : "filtering.model.Filter"
}
```
