package charcoal.ehealthinnovation.org.charcoaltextview;

import android.app.Activity;
import android.app.Dialog;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import java.lang.reflect.Field;

import charcoal.ehealthinnovation.org.charcoaltextview.annotation.Charcoal;
import charcoal.ehealthinnovation.org.charcoaltextview.annotation.CharcoalWriter;
import charcoal.ehealthinnovation.org.charcoaltextview.preferences.EssenceController;
import charcoal.ehealthinnovation.org.charcoaltextview.view.CharcoalTextView;

/**
 * This class processes the values set in the annotaions {@link Charcoal} and {@link CharcoalWriter}.
 * <p>
 * Created by miantorno on 2017-10-16.
 */
public class CharcoalBinder {

    public static final String TAG = CharcoalBinder.class.getSimpleName();

    /**
     * Initializes defaults for all {@link Charcoal} and {@link CharcoalWriter} annotations.
     *
     * @param target Target activity for view binding.
     */
    public static void burn(@NonNull Activity target) {
        View sourceView = target.getWindow().getDecorView();
        process(target, sourceView);
    }

    /**
     * Initializes defaults for all {@link Charcoal} and {@link CharcoalWriter} annotations.
     *
     * @param target Target view for view binding.
     */
    public static void burn(@NonNull View target) {
        process(target, target);
    }

    /**
     * Initializes defaults for all {@link Charcoal} and {@link CharcoalWriter} annotations.
     *
     * @param target Target dialog for view binding.
     */
    public static void burn(@NonNull Dialog target) {
        View sourceView = target.getWindow().getDecorView();
        if (sourceView != null) {
            process(target, sourceView);
        } else {
            Log.e(TAG, "Charcoal cannot bind, null source view returned from getDecorView()");
        }
    }

    /**
     * Initializes defaults for all {@link Charcoal} and {@link CharcoalWriter} annotations.
     *
     * @param target Target class for view binding.
     * @param source Activity on which IDs will be looked up.
     */
    public static void burn(@NonNull Object target, @NonNull Activity source) {
        View sourceView = source.getWindow().getDecorView();
        process(target, sourceView);
    }

    /**
     * Initializes defaults for all {@link Charcoal} and {@link CharcoalWriter} annotations.
     *
     * @param target Target class for view binding.
     * @param source View root on which IDs will be looked up.
     */
    public static void burn(@NonNull Object target, @NonNull View source) {
        process(target, source);
    }

    /**
     * Initializes defaults for all {@link Charcoal} and {@link CharcoalWriter} annotations.
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
     * {@link Charcoal} and {@link CharcoalWriter} annotations.
     *
     * @param target Target class for view binding.
     * @param source Dialog on which IDs will be looked up.
     */
    private static void process(@NonNull Object target, @NonNull View source) {

        Charcoal classAnnotation = target.getClass().getAnnotation(Charcoal.class);

        if (classAnnotation != null) {
            String assetFileName = classAnnotation.asset();
            EssenceController.setEssenceFile(assetFileName, source.getContext());
            Log.i(TAG, "Binding UCUM definitions file from asset file -> " + assetFileName);
        }

        Field[] fields = target.getClass().getDeclaredFields();

        for (Field field : fields) {
            CharcoalWriter writer = field.getAnnotation(CharcoalWriter.class);
            if ((writer != null) && (field.getType().isAssignableFrom(CharcoalTextView.class))) {
                try {
                    CharcoalTextView textView = (CharcoalTextView) field.get(target);
                    textView.setProperty(writer.property());
                    textView.setUnitString(writer.defaultUnit());
                    textView.setAccuracy(writer.accuracy());
                    textView.setFormat(writer.format());
                    textView.relight();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
