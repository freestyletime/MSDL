package cn.christian.msdl;

/**
 * Created in IntelliJ IDEA.
 * MSDL
 * cn.christian.msdl
 *
 * @author Christian_Chen
 * @github freestyletime@foxmail.com
 * @mail mailchristianchen@gmail.com
 * @time 14-9-16 下午2:06
 * @describtion A interface for user
 */
public interface DownLoader {
    /**
     * Register a object to find its regular callback function.
     *
     * @param obj The object you want to attach
     * */
    void register(Object obj);
    /**
     * Unregister a object before you attached.
     *
     * @param obj The object you attached before
     * */
    void unRegister(Object obj);
    /**
     * Set the time that will call the callback to the Mainthread you register.
     *
     * @param interval Repeat time (millisecond)
     * */
    DownLoader setRepeatTime(long interval);
    /**
     * Set running task size (just the running, not the waiting).
     *
     * @param size a number(generally , range in 1-3)
     * */
    DownLoader setThreadSize(int size);
    /**
     * Set the download base path(dir) , file will show in this dir when its download is finish.
     *
     * @param path Absolute path(SD or internal)
     * */
    DownLoader setBasePath(String path);
    /**
     * Set the connection timeout.
     *
     * @param timeout Allowed connection time (millisecond)
     * */
    DownLoader setConnectTimeout(int timeout);
    /**
     * Set the reading datas timeout.
     *
     * @param timeout Allowed reading datas time (millisecond)
     * */
    DownLoader setReadTimeout(int timeout);
    /**
     * Add and start a new task to the download queue.
     *
     * @param url A http URL user should be provided
     * @return A unique id that the task belong to
     * */
    String add(String url);
    /**
     * Add and start a new task to the download queue.
     *
     * @param url A http URL user should be provided
     * @param listener callback
     * @return A unique id that the task belong to
     * */
    String add(String url, DownLoadTaskListener listener);
    /**
     * Add and start a new task to the download queue.
     *
     * @param id The task's unique id
     * @param url A http URL user should be provided
     * */
    void add(String id, String url);
    /**
     * Add and start a new task to the download queue.
     *
     * @param id The task's unique id
     * @param url A http URL user should be provided
     * @param listener callback
     * */
    void add(String id, String url, DownLoadTaskListener listener);
    /**
     * Resume a task in the download queue.
     *
     * @param id The task's unique id
     * */
    void resume(String id);
    /**
     * Pause a task in the download queue.
     *
     * @param id The task's unique id
     * */
    void pause(String id);
    /**
     * Remove a running or waitting task in the download queue.
     *
     * @param id The task's unique id
     * */
    void cancel(String id);
    /**
     * Set a callback to listen the tasks status change.
     *
     * @param listener The listener of user set
     * */
    void setOnDownLoadTaskListener(DownLoadTaskListener listener);
    /**
     * Query the managed task by id
     *
     * @param id The task's unique id
     * @return It will return null if the task not exist in queue
     * */
    DownLoadUserTask query(String id);
}
