package charcoal.ehealthinnovation.org.charcoaltextview.annotation;

import org.hl7.fhir.dstu3.model.Observation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import charcoal.ehealthinnovation.org.charcoaltextview.view.CharcoalTextView;

/**
 * Annotation for {@link CharcoalTextView}.
 * <p>
 * Created by miantorno on 2017-10-05.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface CharcoalWriter {

    /**
     * Property that this measurement represents, ie 'blood_glucose', 'weight', etc.
     * These codes are internal to your own application, and have no relation to UCUM fields or
     * unit properties.
     */
    String property();

    /**
     * Default unit to set for this field. If any error with conversion or preferences occurs, the
     * field will default to this.
     */
    String defaultUnit() default "";

    /**
     * The decimal place accuracy of a number is the number of digits to the right of the decimal point.
     */
    int accuracy() default 2;

    /**
     * String format for displaying the {@link Observation} value, and unit.
     */
    String format() default "%1$s %2$s";
}
