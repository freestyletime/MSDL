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
@Deprecated
public interface DownLoadTaskListener {
    void waitting(String id);
    void running(DownLoadUserTask task);
    void finish(String id);
    void error(int errorCode);
}
