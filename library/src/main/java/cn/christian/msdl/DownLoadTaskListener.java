package cn.christian.msdl;

/**
 * Created in IntelliJ IDEA.
 * MSDL
 * cn.christian.msdl
 *
 * @author Christian_Chen
 * @github freestyletime@foxmail.com
 * @mail mailchristianchen@gmail.com
 * @time 14-9-16 下午1:43
 * @describtion User asynchronous callback
 */
public interface DownLoadTaskListener {
    void running(String id, long length, long process);

    void waitting(String id);

    void finish(String id, String path);

    void cancel(String id);

    void error(String id, DownLoadException e);
}
