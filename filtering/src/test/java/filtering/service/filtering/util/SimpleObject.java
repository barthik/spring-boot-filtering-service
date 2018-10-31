package filtering.service.filtering.util;

import filtering.annotation.Filterable;
import lombok.Data;

@Data
public class SimpleObject {

    private String id;

    @Filterable
    private String name;

    @Filterable
    private Long number;

    private int ignoredField;
}
