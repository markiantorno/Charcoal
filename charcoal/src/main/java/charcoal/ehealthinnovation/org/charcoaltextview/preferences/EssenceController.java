package charcoal.ehealthinnovation.org.charcoaltextview.preferences;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import org.fhir.ucum.UcumEssenceService;
import org.fhir.ucum.UcumException;

import java.io.IOException;

/**
 * Controller for loading and accessing the {@link EssenceController} and {@link org.fhir.ucum.UcumModel}
 * derived from the UCUM xml definition file provided in the projects assets directory.
 * <p>
 * Created by mark on 2017-10-18.
 */
public class EssenceController {

    public static final String TAG = EssenceController.class.getSimpleName();

    /**
     * Name of the UCUM xml definition file within the projects assets folder.
     */
    private static String mEssenceFileName;

    /**
     * Singleton reference to the loaded {@link UcumEssenceService}
     */
    private static UcumEssenceService mUcumService;

    /**
     * Loads the essence file provided.
     *
     * @param fileName {@link String} name of UCUM xml definition file in the project assets directory to load.
     * @param ctx      {@link Context} used to load assets.
     * @return {@link UcumEssenceService}
     */
    public static UcumEssenceService setEssenceFile(@NonNull String fileName, @NonNull Context ctx) {
        if (shouldLoadModel(fileName)) {
            try {
                mUcumService = new UcumEssenceService(ctx.getAssets().open(fileName));
                mEssenceFileName = fileName;
            } catch (UcumException e) {
                Log.e(TAG, "Cannot load your provided UCUM xml definitions file, " + e.getMessage());
            } catch (IOException e) {
                Log.e(TAG, "Cannot load your provided UCUM xml definitions file, " + e.getMessage());
            }
        }
        return getUcumService();
    }

    /**
     * Checks if the controller should load/re-load the given file, based on the name, and currently
     * loaded {@link UcumEssenceService}, if any.
     *
     * @param fileName {@link String} name of file to load.
     * @return {@link Boolean#TRUE} if the file should be loaded.
     */
    protected static boolean shouldLoadModel(String fileName) {
        return ((mEssenceFileName == null) ||
                (!mEssenceFileName.equals(fileName)) ||
                (mUcumService == null));
    }

    /**
     * Gets the current {@link UcumEssenceService}, or null, if no such service if loaded.
     *
     * @return {@link UcumEssenceService}
     */
    public static UcumEssenceService getUcumService() {
        if (mUcumService != null) {
            return mUcumService;
        } else {
            Log.e(TAG, "No UCUM model loaded.");
            return null;
        }
    }
}
