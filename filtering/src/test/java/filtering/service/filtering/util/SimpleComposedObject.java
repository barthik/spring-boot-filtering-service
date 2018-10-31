package filtering.service.filtering.util;

import filtering.annotation.Filterable;
import lombok.Data;

@Data
public class SimpleComposedObject {

    private String id;

    @Filterable(grab = "id")
    private DeepObject deepObject;

    private int ignoredField;
}
