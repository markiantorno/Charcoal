package charcoal.ehealthinnovation.org.charcoaltextview.loader;

import android.os.Process;
import android.support.annotation.NonNull;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import charcoal.ehealthinnovation.org.charcoaltextview.pojo.ObservationPair;
import charcoal.ehealthinnovation.org.charcoaltextview.view.CharcoalTextView;

public class CustomThreadPoolManager {

    public static final String TAG = CustomThreadPoolManager.class.getSimpleName();

    private static CustomThreadPoolManager sInstance = null;
    private static int NUMBER_OF_CORES = Math.max(1, Runtime.getRuntime().availableProcessors() - 1);
    private static final int KEEP_ALIVE_TIME = 10;
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT;

    private final ExecutorService mExecutorService;
    private final BlockingQueue<Runnable> mTaskQueue;
    private Map<String, Future> mRunningTaskList;

    // The class is used as a singleton
    static {
        KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
        sInstance = new CustomThreadPoolManager();
    }

    // Made constructor private to avoid the class being initiated from outside
    private CustomThreadPoolManager() {
        // initialize a queue for the thread pool. New tasks will be added to this queue
        mTaskQueue = new LinkedBlockingQueue<Runnable>();

        mRunningTaskList = new HashMap<>();

        mExecutorService = new ThreadPoolExecutor(1,
                NUMBER_OF_CORES,
                KEEP_ALIVE_TIME,
                KEEP_ALIVE_TIME_UNIT,
                mTaskQueue,
                new BackgroundThreadFactory());
    }

    public static CustomThreadPoolManager getsInstance() {
        return sInstance;
    }

    public ConvertUnitThread addUnitConversionTask(ConvertUnitThread callable,
                                                   @NonNull CharcoalTextView textView,
                                                   @NonNull String desiredUnit,
                                                   int desiredAccuracy,
                                                   @NonNull String format,
                                                   ObservationPair obs) {
        if (callable != null) {
            if ((callable.getmWeakReference().get().getUUID().equals(textView.getUUID()))
                    && (callable.getObservationPair().equals(obs))
                    &&(callable.getmDesiredUnit().equals(desiredUnit))) {
                Log.d(TAG, "Same threading request made. Returning thread for UUID :: " + textView.getUUID());
                return callable;
            } else {
                Log.d(TAG, "Current thread not null, cancelling.");
                cancel(callable);
                callable.setWeakReference(new WeakReference<>(textView))
                        .setDesiredUnit(desiredUnit)
                        .setDesiredAccuracy(desiredAccuracy)
                        .setFormat(format)
                        .setObservationPair(obs);
            }
        } else {
            Log.d(TAG, "Current thread is null, creating.");
            callable = new ConvertUnitThread(textView,
                    desiredUnit,
                    desiredAccuracy,
                    format,
                    obs);
        }

        textView.setText("");
        addCallable(callable);
        return callable;
    }

    // Add a callable to the queue, which will be executed by the next available thread in the pool
    private void addCallable(ConvertUnitThread callable) {
        Future future = mExecutorService.submit(callable);
        mRunningTaskList.put(callable.getmWeakReference().get().getUUID(), future);
    }

    public void cancel(ConvertUnitThread callable) {
        synchronized (this) {
            if (callable.getmWeakReference().get() != null) {
                String viewId = callable.getmWeakReference().get().getUUID();
                Log.d(TAG, "Cancelling thread with associated view id :: " + viewId);
                Future existing = mRunningTaskList.get(viewId);
                if ((existing != null) && !existing.isDone()) {
                    existing.cancel(true);
                    mTaskQueue.remove(existing);
                    mRunningTaskList.remove(viewId);
                }
            }
        }
    }

    /*
     * Remove all tasks in the queue and stop all running threads
     * Notify UI thread about the cancellation
     */
    public void cancelAllTasks() {
        synchronized (this) {
            mTaskQueue.clear();
            for (Future task : mRunningTaskList.values()) {
                if (!task.isDone()) {
                    task.cancel(true);
                }
            }
            mRunningTaskList.clear();
        }
    }

    /*
     * A ThreadFactory implementation which create new threads for the thread pool.
     * The threads created is set to background priority, so it does not compete with the UI thread.
     */
    private static class BackgroundThreadFactory implements ThreadFactory {
        private static int sTag = 1;

        @Override
        public Thread newThread(Runnable runnable) {
            Thread thread = new Thread(runnable);
            thread.setName("CustomThread" + sTag);
            sTag++;
            thread.setPriority(Process.THREAD_PRIORITY_BACKGROUND);

            // A exception handler is created to log the exception from threads
            thread.setUncaughtExceptionHandler((thread1, ex) -> Log.e(TAG, thread1.getName() + " encountered an error: " + ex.getMessage()));
            return thread;
        }
    }
}
