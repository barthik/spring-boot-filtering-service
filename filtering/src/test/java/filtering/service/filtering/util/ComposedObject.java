package filtering.service.filtering.util;

import filtering.annotation.Filterable;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ComposedObject {

    private String id;

    @Filterable
    private String name;

    @Filterable
    private Long number;

    @Filterable
    private List<String> attributes;

    @Filterable
    private Map<String, Object> objects;

    @Filterable(deepDive = true)
    private DeepObject deepObject;

    private int ignoredField;
}
