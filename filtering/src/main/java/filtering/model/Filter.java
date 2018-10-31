package filtering.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@Document(collection = Filter.COLLECTION)
public class Filter {

    public static final String COLLECTION = "filters";

    @Id
    private FilterKey id;

    private Map<String, Object> filters;
}
