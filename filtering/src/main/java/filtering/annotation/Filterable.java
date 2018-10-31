
package filtering.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotated field will be proceed for filters gathering. Annotation can be used on primitives, complex objects,
 * collections, etc - filter value is structureless. By default only first level of filters are collected
 * (only fields of top class).
 * <p>
 * Use grab value to collect only specific field from the nested object - usually the id field.
 * <p>
 * The recursive search is available by set the deep dive to {@code true}. If field referencing another entity is
 * annotated with deep dive, all annotated fields from this entity will be collected.
 * <p>
 * An object structures are represented by the key consists of fields name in camel-case format separated by key
 * separator (dot by default).
 * <p>
 * Fields with {@code null} values are ignored.
 */
@Documented
@Retention(RUNTIME)
@Target(FIELD)
public @interface Filterable {
    /**
     * If specified on the complex object, only the field with the given field name will be considered to process for filtering.
     */
    String grab() default "@empty";

    /**
     * If set to <code>true</code>, all fields of the complex object annotated with {@link Filterable} will be proceeded.
     */
    boolean deepDive() default false;
}
