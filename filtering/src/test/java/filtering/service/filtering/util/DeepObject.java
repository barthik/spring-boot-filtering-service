package filtering.service.filtering.util;

import filtering.annotation.Filterable;
import lombok.Data;

import java.util.Map;

@Data
public class DeepObject {

    @Filterable
    private String id;

    @Filterable
    private String name;

    @Filterable
    private double number;

    @Filterable
    private Map<String, String> setting;

    private String ignoredField;
}
