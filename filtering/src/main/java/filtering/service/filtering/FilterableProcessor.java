package filtering.service.filtering;

import filtering.annotation.Filterable;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class FilterableProcessor {

    private static final String KEY_SEPARATOR = ".";

    /**
     * Returns a map of the fields annotated with {@link Filterable} and its values.
     * <ul>
     * <li>If not specified, only the first level of the filter is collected - the entire value of the annotated field
     * is taken no matter the structure; supported complex objects, collections, primitives, etc.</li>
     * <li>In case the specific field in the annotation is given, only this field is taken as a filer.</li>
     * <li>Recursive deep searching is possible with deep dive set to {@code true} - all {@link Filterable} fields
     * will be collected recursively and proceeded.</li>
     * </ul>
     * The key of the filter consists of fields name in camel-case format separated by key separator (dot by default).
     * Note that the root field name is neglected.
     * <p>
     * Fields with null values are ignored and not stored to the filters map.
     *
     * @param entity Entity to discover
     * @param <T>
     * @return Field:value map of the annotated entity fields
     */
    public static <T> Map<String, Object> discover(T entity) {
        if (entity == null) {
            return new HashMap<>();
        }

        Map<String, Object> filters = new HashMap<>();

        discover(entity, filters, null); // set root path to null

        return filters;
    }

    /**
     * Processes an object to the filters map. Only fields annotated with {@link Filterable} are further processed.
     *
     * @param object  Object to discover
     * @param filters Filters map to store discovered filters
     * @param keyPath Key path to concat with separated field name
     */
    private static void discover(Object object, Map<String, Object> filters, String keyPath) {
        for (Field f : object.getClass().getDeclaredFields()) {
            if (f.isAnnotationPresent(Filterable.class)) {
                if (f.getAnnotation(Filterable.class).deepDive()) { // recursive search
                    deepDive(object, filters, f, getKey(keyPath, f));
                } else if (!f.getAnnotation(Filterable.class).grab().equalsIgnoreCase("@empty")) { // specific field is set to collect
                    extractExactField(object, filters, f, getKey(keyPath, f));
                } else { // otherwise
                    extractField(object, filters, f, getKey(keyPath, f));
                }
            }

        }
    }

    private static String getKey(String keyPath, Field field) {
        return keyPath == null ? field.getName() : keyPath.concat(KEY_SEPARATOR).concat(field.getName());
    }

    /**
     * Extracts a value of the field and stores it to the given filters map with key path as key.
     *
     * @param object  Reference object
     * @param filters Filters map
     * @param field   Field to process
     * @param keyPath Key path
     */
    private static void extractField(Object object, Map<String, Object> filters, Field field, String keyPath) {
        try {
            field.setAccessible(true);
            Object value = field.get(object);
            if (value != null) {
                filters.put(keyPath, value);
            }
        } catch (IllegalArgumentException | IllegalAccessException e) {
            log.error("Failed extraction of filterable field", e);
        } finally {
            field.setAccessible(false);
        }
    }

    /**
     * Extracts a value of the specific field specified in the {@link Filterable} annotation and stores it to the
     * filters map with key path as key.
     *
     * @param object  Reference object
     * @param filters Filters map
     * @param field   Field to process
     * @param keyPath Key path
     */
    private static void extractExactField(Object object, Map<String, Object> filters, Field field, String keyPath) {
        Field grabbedField = null;

        try {
            grabbedField = field.getType().getDeclaredField(field.getAnnotation(Filterable.class).grab());

            grabbedField.setAccessible(true);
            field.setAccessible(true);

            Object root = field.get(object);

            if (root == null) {
                return;
            }

            Object grabbedValue = grabbedField.get(root);

            if (grabbedValue != null) {
                filters.put(getKey(keyPath, grabbedField), grabbedValue);
            }
        } catch (NoSuchFieldException | IllegalAccessException | NullPointerException e) {
            log.error("Failed to grab the filterable field", e);
        } finally {
            field.setAccessible(false);
            if (grabbedField != null) {
                grabbedField.setAccessible(false);
            }
        }
    }

    /**
     * Helps to deep recursive search of annotated fields.
     *
     * @param object  Reference object
     * @param filters Filters map
     * @param field   Field to process
     * @param keyPath Key path
     */
    private static void deepDive(Object object, Map<String, Object> filters, Field field, String keyPath) {
        Object grabbedObject;
        try {
            field.setAccessible(true);
            grabbedObject = field.get(object);
            discover(grabbedObject, filters, keyPath);
        } catch (IllegalAccessException | NullPointerException e) {
            log.error("Failed to grab the deep filterable field", e);
        } finally {
            field.setAccessible(false);
        }
    }
}
