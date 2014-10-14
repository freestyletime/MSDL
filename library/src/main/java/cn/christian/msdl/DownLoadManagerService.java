package cn.christian.msdl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.*;

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
 *              by system service
 */
public class DownLoadManagerService {

    static boolean NET_STATE;
    private static DownLoadManagerService service = new DownLoadManagerService();
    private static Context context;

    private volatile String ACTION_ANDROID_INFORM_TASK = "action_android_inform_task";

    //min API level is 8 at least.
    private final String defaultBasePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
    private final int defaultThreadSize = 1;
    private final long defaultRepeatTime = 1000;

    private ExecutorService threadPool;
    private Queue<DownLoadTask> wattingTasks = new LinkedList<DownLoadTask>();
    private Queue<DownLoadTask> runningTasks = new LinkedList<DownLoadTask>();
    private Queue<DownLoadTask> pauseTasks = new LinkedList<DownLoadTask>();
    private List<DownLoadTaskListener> listeners = new LinkedList<DownLoadTaskListener>();
    private Map<String, DownLoadTaskListener> listeners2 = new ConcurrentHashMap<String, DownLoadTaskListener>();
    private Map<String, DownLoadTask> allTasks = new ConcurrentHashMap<String, DownLoadTask>();
    private Map<Object, Method> callbacks = new ConcurrentHashMap<Object, Method>();

    private Intent intent = new Intent(ACTION_ANDROID_INFORM_TASK);
    private PowerManager.WakeLock wakeLock;
    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(new DownLoadThreadFactory());
    private class DownLoadScheduledTask implements Runnable{

        @Override
        public void run() {
            context.sendBroadcast(intent);
        }
    }
    private final BroadcastReceiver receiver1 = new BroadcastReceiver() {

        private Lock lock = new ReentrantLock();

        @Override
        public void onReceive(Context context, Intent intent) {
            lock.lock();
            Set<Map.Entry<Object, Method>> callbackEntrys = DownLoadManagerService.this.callbacks.entrySet();
            Set<Map.Entry<String, DownLoadTaskListener>> listenersEntrys = DownLoadManagerService.this.listeners2.entrySet();
            Collection<DownLoadTask> tasks = DownLoadManagerService.this.allTasks.values();

            Object obj;
            Method callback;
            DownLoadUserTask userTask = new DownLoadUserTask();

            try{
                for(DownLoadTask task : tasks) {
                    if(DownLoadManagerService.this.wattingTasks.contains(task) && task.isCancel)
                        task.status = DownLoadTaskStatus.CANCEL;

                    for (Map.Entry<Object, Method> entry : callbackEntrys) {
                        obj = entry.getKey();
                        callback = entry.getValue();
                        callback.invoke(obj, task);
                    }

                    for(DownLoadTaskListener listener : listeners){
                        switch (task.status){
                            case RUNNING:
                                listener.running(task.id, task.length, task.process);
                                break;
                            case WATTING:
                                listener.waitting(task.id);
                                break;
                            case PAUSE:
                                listener.pause(task.id);
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

                    for (Map.Entry<String, DownLoadTaskListener> entry : listenersEntrys) {
                        if(entry.getKey().equals(task.id)){
                            DownLoadTaskListener listener = entry.getValue();
                            switch (task.status){
                                case RUNNING:
                                    listener.running(task.id, task.length, task.process);
                                    break;
                                case WATTING:
                                    listener.waitting(task.id);
                                    break;
                                case PAUSE:
                                    listener.pause(task.id);
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

                    switch (task.status){
                        case CANCEL:
                        case FINISH:
                        case ERROR:
                            remove(task.id);
                    }
                }
            }catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }finally {
                lock.unlock();
            }
        }
    };
    private final BroadcastReceiver receiver2 = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            networkJudge();
        }
    };

    private DownLoadManagerService(){
        // ---  default setting  ---
        this.basePath = this.defaultBasePath;
        this.threadSize = this.defaultThreadSize;
        this.repeatTime = this.defaultRepeatTime;
        // ---  default setting  ---
    }

    private void startWork(){
        if(threadPool == null)
            threadPool = Executors.newFixedThreadPool(this.threadSize, new DownLoadThreadFactory());

        networkJudge();
        context.registerReceiver(receiver1, new IntentFilter(ACTION_ANDROID_INFORM_TASK));
        context.registerReceiver(receiver2, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        scheduler.scheduleWithFixedDelay(new DownLoadScheduledTask(), 0, repeatTime, TimeUnit.MILLISECONDS);
        acquireWakeLock(context, wakeLock);
    }

    private void stopWork(){
        if(!scheduler.isShutdown()) {
            scheduler.shutdown();
            context.unregisterReceiver(receiver1);
            context.unregisterReceiver(receiver2);
            releaseWakeLock(wakeLock);
        }
    }

    private boolean loop(){
        if(wattingTasks.size() > 0){
            add(wattingTasks.poll());
            return true;
        }
        return false;
    }

    private void remove(String id) {
        DownLoadTask task;

        if(allTasks.containsKey(id)){
            task = allTasks.get(id);

            if(runningTasks.contains(task))
                runningTasks.remove(task);
            if(pauseTasks.contains(task))
                pauseTasks.remove(task);
            if(wattingTasks.contains(task))
                wattingTasks.remove(task);

            allTasks.remove(id);
        }

        if(allTasks.size() == 0){
            stopWork();
        }else if(runningTasks.size() < threadSize && loop());

    }

    private void acquireWakeLock(Context context, PowerManager.WakeLock wakeLock) {
        if (wakeLock == null) {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, this.getClass().getCanonicalName());
            wakeLock.acquire();
        }
    }

    private void releaseWakeLock(PowerManager.WakeLock wakeLock) {
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
            wakeLock = null;
        }
    }

    private void networkJudge(){
        if(context != null)
            NET_STATE = isNetworkConnected(context);
    }

    private boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }

    //-----------------------------------------

    static DownLoadManagerService getInstance(Context context){
        DownLoadManagerService.context = context;
        return service;
    }

    //-----------------------------------------

    String basePath;
    int threadSize;
    long repeatTime;
    int connectionTimeout = 0;
    int readTimeout = 0;

    void setBasePath(String basePath){
        this.basePath = basePath;
    }

    void setThreadSize(int threadSize){
        this.threadSize = threadSize;
    }

    void setRepeatTime(long repeatTime){
        this.repeatTime = repeatTime;
    }

    void setConnectTimeout(int timeout){
        connectionTimeout = timeout;
    }

    void setReadTimeout(int timeout){
        readTimeout = timeout;
    }

    void bind(Object obj, Method method){
        if(obj != null && method != null && !callbacks.containsKey(obj))
            callbacks.put(obj, method);
    }

    void unBind(Object obj){
        if(callbacks.containsKey(obj))
            callbacks.remove(obj);
    }

    void setListener(DownLoadTaskListener listener){
        if(listener != null && !listeners.contains(listener))
            listeners.add(listener);
    }

    void add(DownLoadTask task){
        if(task == null)
            return;

        boolean isExecute = false;

        if(runningTasks.size() >= threadSize){
            wattingTasks.offer(task);
        }else{
          if(allTasks.size() == 0) startWork();
          isExecute = true;
        }

        if(isExecute) {
            threadPool.submit(new DownLoadTaskRunnable(task, connectionTimeout, readTimeout));
            runningTasks.offer(task);
        }

        if(!allTasks.containsKey(task.id))
            allTasks.put(task.id, task);
    }

    void add(DownLoadTask task, DownLoadTaskListener listener){
        add(task);
        listeners2.put(task.id, listener);
    }

    void resume(String id) {
        DownLoadTask task;

        if(allTasks.containsKey(id)){
            task = allTasks.get(id);
            if(pauseTasks.contains(task)) {
                if(task.isPause) task.isPause = false;

                pauseTasks.remove(task);
                add(task);
            }
        }
    }

    void pause(String id) {
        DownLoadTask task;

        if(allTasks.containsKey(id)){
            task = allTasks.get(id);
            if (runningTasks.contains(task)) {
                if(!task.isPause) task.isPause = true;

                runningTasks.remove(task);
                pauseTasks.offer(task);
                loop();
            }
        }
    }

    void cancel(String id){
        DownLoadTask task;

        if(allTasks.containsKey(id)){
            task = allTasks.get(id);
            if(task.isCancel = false)
                task.isCancel = true;
        }
    }

    DownLoadUserTask query(String id){
        if(allTasks.containsKey(id)){
            return allTasks.get(id);
        }else{
            return null;
        }
    }
}
