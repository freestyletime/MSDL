package cn.christian.msdl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created in IntelliJ IDEA.
 * MSDL
 * cn.christian.msdl
 *
 * @author Christian_Chen
 * @github freestyletime@foxmail.com
 * @mail mailchristianchen@gmail.com
 * @time 14-9-16 上午11:43
 * @describtion A pretended service about download and that is a real handler to deal the download
 * by system service
 */
public class DownLoadManagerService {

    private static DownLoadManagerService service = new DownLoadManagerService();
    String basePath;
    int threadSize;
    long repeatTime;
    int connectionTimeout = 0;
    int readTimeout = 0;
    private Lock lock = new ReentrantLock();
    private DownLoadLogger logger = new DownLoadLogger();
    private ExecutorService threadPool;
    private Queue<DownLoadTask> wattingTasks = new LinkedList<>();
    private Queue<DownLoadTask> runningTasks = new LinkedList<>();
    private Map<String, DownLoadTaskListener> listeners = new ConcurrentHashMap<>();
    private Map<String, DownLoadTask> allTasks = new ConcurrentHashMap<>();
    private Map<Object, Method> callbacks = new ConcurrentHashMap<>();
    private Map<String, String> headers = new ConcurrentHashMap<>();
    private ScheduledExecutorService scheduler;

    private DownLoadManagerService() {
        // ---  default setting  ---
        String defaultBasePath = "/sdcard/download/";
        long defaultRepeatTime = 1000;
        int defaultThreadSize = 1;

        this.basePath = defaultBasePath;
        this.threadSize = defaultThreadSize;
        this.repeatTime = defaultRepeatTime;
        // ---  default setting  ---
    }

    static DownLoadManagerService getInstance() {
        return service;
    }

    //-----------------------------------------

    private void call() {
        lock.lock();
        StringBuffer logs = new StringBuffer();
        DownLoadTask task = null;

        try {
            Set<Map.Entry<Object, Method>> callbackEntrys = DownLoadManagerService.this.callbacks.entrySet();
            Set<Map.Entry<String, DownLoadTaskListener>> listenersEntrys = DownLoadManagerService.this.listeners.entrySet();
            Collection<DownLoadTask> tasks = DownLoadManagerService.this.allTasks.values();

            Object obj;
            Method callback;

            for (DownLoadTask tmp : tasks) {
                task = tmp.clone();
                //log collection
                logs.append("\n---------------Task # " + task.id + " # Start---------------\n");
                logs.append("| STATUS: " + task.status.toString() + "\n");
                logs.append("| URL: " + task.url + "\n");
                logs.append("| PATH: " + task.path + "\n");
                logs.append("| LEN: " + task.length + "\n");
                logs.append("| PROCESS: ").append(task.process).append("\n");
                if (task.length != 0) {
                    logs.append("| PERCENT: " + ((int) ((task.process * 100) / task.length)) + "\n");
                }
                if (task.e != null) {
                    logs.append("| ECODE: " + task.e.eCode + "\n");
                    logs.append("| ERROR: " + task.e.getMessage() + "\n");
                }
                logs.append("---------------Task # " + task.id + " # End---------------\n");

                for (Map.Entry<Object, Method> entry : callbackEntrys) {
                    obj = entry.getKey();
                    callback = entry.getValue();
                    callback.invoke(obj, task);
                }

                for (Map.Entry<String, DownLoadTaskListener> entry : listenersEntrys) {
                    if (entry.getKey().equals(task.id)) {
                        DownLoadTaskListener listener = entry.getValue();
                        switch (task.status) {
                            case RUNNING:
                                listener.running(task.id, task.length, task.process);
                                break;
                            case WATTING:
                                listener.waitting(task.id);
                                break;
                            case CANCEL:
                                listener.cancel(task.id);
                                break;
                            case FINISH:
                                listener.finish(task.id, task.path);
                                break;
                            case ERROR:
                                listener.error(task.id, task.e);
                                break;
                        }
                    }
                }

                switch (task.status) {
                    case CANCEL:
                    case FINISH:
                    case ERROR:
                        remove(task.id);
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
            logger.debug(logs.toString());
            logs = null;
            task = null;
        }
    }

    //-----------------------------------------

    private void startWork() {
        lock.lock();
        try {
            if (threadPool == null)
                threadPool = Executors.newFixedThreadPool(this.threadSize, new DownLoadThreadFactory());

            scheduler = Executors.newSingleThreadScheduledExecutor(new DownLoadThreadFactory());
            scheduler.scheduleWithFixedDelay(new DownLoadScheduledTask(), 0, repeatTime, TimeUnit.MILLISECONDS);
        } finally {
            lock.unlock();
        }
    }

    private void stopWork() {
        lock.lock();
        try {
            if (!scheduler.isShutdown()) {
                scheduler.shutdown();
                scheduler = null;
            }
        } finally {
            lock.unlock();
        }
    }

    private boolean loop() {
        if (wattingTasks.size() > 0) {
            add(wattingTasks.poll());
            return true;
        }
        return false;
    }

    private void remove(String id) {
        DownLoadTask task;

        if (allTasks.containsKey(id)) {
            task = allTasks.get(id);

            if (runningTasks.contains(task))
                runningTasks.remove(task);
            if (wattingTasks.contains(task))
                wattingTasks.remove(task);

            allTasks.remove(id);
        }

        if (allTasks.size() == 0) {
            stopWork();
        } else if (runningTasks.size() < threadSize && loop()) ;

    }

    void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    void setThreadSize(int threadSize) {
        this.threadSize = threadSize;
    }

    void setRepeatTime(long repeatTime) {
        this.repeatTime = repeatTime;
    }

    void setConnectTimeout(int timeout) {
        connectionTimeout = timeout;
    }

    void setReadTimeout(int timeout) {
        readTimeout = timeout;
    }

    void addHeader(String key, String value) {
        if (null == key || "" == key || null == value || "" == value) return;
        headers.put(key, value);
    }

    void bind(Object obj, Method method) {
        if (obj != null && method != null && !callbacks.containsKey(obj))
            callbacks.put(obj, method);
    }

    void unBind(Object obj) {
        if (callbacks.containsKey(obj))
            callbacks.remove(obj);
    }

    void setListener(String id, DownLoadTaskListener listener) {
        if (id != null && listener != null) {
            if (listeners.containsKey(id)) listeners.put(id, listener);
        }
    }

    void add(DownLoadTask task) {
        if (task == null) return;

        boolean isExecute = false;

        if (runningTasks.size() >= threadSize) {
            wattingTasks.offer(task);
        } else {
            if (allTasks.size() == 0) startWork();
            isExecute = true;
        }

        if (isExecute) {
            threadPool.submit(new DownLoadTaskRunnable(task, headers, connectionTimeout, readTimeout));
            runningTasks.offer(task);
        }

        if (!allTasks.containsKey(task.id))
            allTasks.put(task.id, task);
    }

    void add(DownLoadTask task, DownLoadTaskListener listener) {
        if (listener == null) return;

        add(task);
        listeners.put(task.id, listener);
    }

    void cancel(String id) {
        DownLoadTask task;

        if (allTasks.containsKey(id)) {
            task = allTasks.get(id);
            if (!task.isCancel) task.isCancel = true;
            if (wattingTasks.contains(task)) task.status = DownLoadTaskStatus.CANCEL;
        }
    }

    DownLoadUserTask query(String id) {
        if (allTasks.containsKey(id)) {
            return allTasks.get(id);
        } else {
            return null;
        }
    }

    private class DownLoadScheduledTask implements Runnable {
        @Override
        public void run() {
            call();
        }
    }
}
