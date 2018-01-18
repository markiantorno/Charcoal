package com.markiantorno.charcoal;

import android.app.Activity;
import android.app.Dialog;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.markiantorno.charcoal.annotation.Charcoal;
import com.markiantorno.charcoal.annotation.Essence;
import com.markiantorno.charcoal.controller.EssenceController;
import com.markiantorno.charcoal.controller.PreferenceController;
import com.markiantorno.charcoal.view.CharcoalTextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

        List<Field> fields = getAllFields(target.getClass());

        for (Field field : fields) {
            field.setAccessible(true); // To enable access to private/protected fields
            Charcoal writer = field.getAnnotation(Charcoal.class);
            if ((writer != null) && (field.getType().isAssignableFrom(CharcoalTextView.class))) {
                try {
                    CharcoalTextView textView = (CharcoalTextView) field.get(target);
                    String property = writer.property();
                    textView.setProperty(property);

                    String defaultUnit = writer.defaultUnit();
                    textView.setUnitString(defaultUnit);

                    int defaultAccuracy = writer.accuracy();
                    if ((defaultAccuracy == Charcoal.NO_ACCURACY_SET) && (PreferenceController.accuracySetForUnit(source.getContext(), defaultUnit))) {
                        Log.d(TAG, "No default accuracy passed into annotation, however, accuracy for unit set it preferences -> " +
                                PreferenceController.getAccuracyForUnit(source.getContext(), defaultUnit));
                        defaultAccuracy = PreferenceController.getAccuracyForUnit(source.getContext(), defaultUnit);
                    } else if ((defaultAccuracy == Charcoal.NO_ACCURACY_SET) && (!PreferenceController.accuracySetForUnit(source.getContext(), defaultUnit))) {
                        Log.d(TAG, "No default accuracy passed into annotation, and no accuracy for unit set it preferences.");
                        defaultAccuracy = 0;
                    }
                    textView.setAccuracy(defaultAccuracy);

                    String format = writer.format();
                    textView.setFormat(format);

                    Log.d(TAG, "Charcoal text view initialized -> " +
                            "\nproperty :: " + property +
                            "\ndefault unit :: " + defaultUnit +
                            "\ndefault accuracy :: " + defaultAccuracy +
                            "\nformat :: " + format);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Get all fields of a given class, even of the super class
     *
     * @param classToGet Class to get the fields from
     * @return A {@link List} of {@link Field}s
     */
    public static List<Field> getAllFields(Class<?> classToGet) {
        List<Field> fields = new ArrayList<>();
        for (Class<?> c = classToGet; c != null; c = c.getSuperclass()) {
            fields.addAll(Arrays.asList(c.getDeclaredFields()));
        }
        return fields;
    }
}
