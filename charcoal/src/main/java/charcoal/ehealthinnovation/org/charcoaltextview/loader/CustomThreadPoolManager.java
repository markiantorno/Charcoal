package charcoal.ehealthinnovation.org.charcoaltextview.loader;

import android.os.Process;
import android.util.Log;
import android.util.SparseArray;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CustomThreadPoolManager {

    public static final String TAG = CustomThreadPoolManager.class.getSimpleName();

    private static CustomThreadPoolManager sInstance = null;
    private static final int DEFAULT_THREAD_POOL_SIZE = 4;
    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
    private static final int KEEP_ALIVE_TIME = 5;
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT;

    private final ExecutorService mExecutorService;
    private final BlockingQueue<Runnable> mTaskQueue;
    private SparseArray<Future> mRunningTaskList;

    // The class is used as a singleton
    static {
        KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
        sInstance = new CustomThreadPoolManager();
    }

    // Made constructor private to avoid the class being initiated from outside
    private CustomThreadPoolManager() {
        // initialize a queue for the thread pool. New tasks will be added to this queue
        mTaskQueue = new LinkedBlockingQueue<Runnable>();

        mRunningTaskList = new SparseArray<>();

        /*
            TODO: You can choose between a fixed sized thread pool and a dynamic sized pool
            TODO: Comment one and uncomment another to see the difference.
         */
        //mExecutorService = Executors.newFixedThreadPool(DEFAULT_THREAD_POOL_SIZE, new BackgroundThreadFactory());
        mExecutorService = new ThreadPoolExecutor(NUMBER_OF_CORES,
                NUMBER_OF_CORES * 2,
                KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT,
                mTaskQueue,
                new BackgroundThreadFactory());
    }

    public static CustomThreadPoolManager getsInstance() {
        return sInstance;
    }

    // Add a callable to the queue, which will be executed by the next available thread in the pool
    public void addCallable(ConvertUnitThread callable) {
        cancel(callable);
        Future future = mExecutorService.submit(callable);
        mRunningTaskList.put(callable.getmWeakReference().get().getId(), future);
    }

    public void cancel(ConvertUnitThread callable) {
        if (callable.getmWeakReference().get() != null) {
            int viewId = callable.getmWeakReference().get().getId();
            Future existing = mRunningTaskList.get(viewId);
            if ((existing != null) && !existing.isDone()) {
                existing.cancel(true);
                mTaskQueue.remove(existing);
                mRunningTaskList.remove(viewId);
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
            for(int i = 0; i < mRunningTaskList.size(); i++) {
                int key = mRunningTaskList.keyAt(i);
                // get the object by the key.
                Future task = mRunningTaskList.get(key);
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
            thread.setPriority(Process.THREAD_PRIORITY_BACKGROUND);

            // A exception handler is created to log the exception from threads
            thread.setUncaughtExceptionHandler((thread1, ex) -> Log.e(TAG, thread1.getName() + " encountered an error: " + ex.getMessage()));
            return thread;
        }
    }
}
