package charcoal.ehealthinnovation.org.charcoaltextview;

import android.app.Activity;
import android.app.Dialog;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import java.lang.reflect.Field;

import charcoal.ehealthinnovation.org.charcoaltextview.annotation.Essence;
import charcoal.ehealthinnovation.org.charcoaltextview.annotation.Charcoal;
import charcoal.ehealthinnovation.org.charcoaltextview.controller.EssenceController;
import charcoal.ehealthinnovation.org.charcoaltextview.controller.PreferenceController;
import charcoal.ehealthinnovation.org.charcoaltextview.view.CharcoalTextView;

/**
 * This class processes the values set in the annotaions {@link Essence} and {@link Charcoal}.
 * <p>
 * Created by miantorno on 2017-10-16.
 */
public class CharcoalBinder {

    public static final String TAG = CharcoalBinder.class.getSimpleName();

    /**
     * Initializes defaults for all {@link Essence} and {@link Charcoal} annotations.
     *
     * @param target Target activity for view binding.
     */
    public static void burn(@NonNull Activity target) {
        View sourceView = target.getWindow().getDecorView();
        process(target, sourceView);
    }

    /**
     * Initializes defaults for all {@link Essence} and {@link Charcoal} annotations.
     *
     * @param target Target view for view binding.
     */
    public static void burn(@NonNull View target) {
        process(target, target);
    }

    /**
     * Initializes defaults for all {@link Essence} and {@link Charcoal} annotations.
     *
     * @param target Target dialog for view binding.
     */
    public static void burn(@NonNull Dialog target) {
        View sourceView = target.getWindow().getDecorView();
        if (sourceView != null) {
            process(target, sourceView);
        } else {
            Log.e(TAG, "Essence cannot bind, null source view returned from getDecorView()");
        }
    }

    /**
     * Initializes defaults for all {@link Essence} and {@link Charcoal} annotations.
     *
     * @param target Target class for view binding.
     * @param source Activity on which IDs will be looked up.
     */
    public static void burn(@NonNull Object target, @NonNull Activity source) {
        View sourceView = source.getWindow().getDecorView();
        process(target, sourceView);
    }

    /**
     * Initializes defaults for all {@link Essence} and {@link Charcoal} annotations.
     *
     * @param target Target class for view binding.
     * @param source View root on which IDs will be looked up.
     */
    public static void burn(@NonNull Object target, @NonNull View source) {
        process(target, source);
    }

    /**
     * Initializes defaults for all {@link Essence} and {@link Charcoal} annotations.
     *
     * @param target Target class for view binding.
     * @param source Dialog on which IDs will be looked up.
     */
    public static void burn(@NonNull Object target, @NonNull Dialog source) {
        View sourceView = source.getWindow().getDecorView();
        process(target, sourceView);
    }

    /**
     * Internal class method for detecting, and parsing the arguments passed in through the
     * {@link Essence} and {@link Charcoal} annotations.
     *
     * @param target Target class for view binding.
     * @param source Dialog on which IDs will be looked up.
     */
    private static void process(@NonNull Object target, @NonNull View source) {

        Essence classAnnotation = target.getClass().getAnnotation(Essence.class);

        if (classAnnotation != null) {
            String assetFileName = classAnnotation.asset();
            EssenceController.setEssenceFile(assetFileName, source.getContext());
            Log.i(TAG, "Binding UCUM definitions file from asset file -> " + assetFileName);
        }

        Field[] fields = target.getClass().getDeclaredFields();

        for (Field field : fields) {
            Charcoal writer = field.getAnnotation(Charcoal.class);
            if ((writer != null) && (field.getType().isAssignableFrom(CharcoalTextView.class))) {
                try {
                    CharcoalTextView textView = (CharcoalTextView) field.get(target);
                    textView.setProperty(writer.property());

                    String defaultUnit = writer.defaultUnit();
                    textView.setUnitString(defaultUnit);

                    int defaultAccuracy = writer.accuracy();
                    if ((defaultAccuracy == Charcoal.NO_ACCURACY_SET) && (PreferenceController.accuracySetForUnit(source.getContext(), defaultUnit))) {
                        defaultAccuracy = PreferenceController.getAccuracyForUnit(source.getContext(), defaultUnit);
                    } else if ((defaultAccuracy == Charcoal.NO_ACCURACY_SET) && (!PreferenceController.accuracySetForUnit(source.getContext(), defaultUnit))) {
                        defaultAccuracy = 0;
                    }
                    textView.setAccuracy(defaultAccuracy);

                    textView.setFormat(writer.format());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
