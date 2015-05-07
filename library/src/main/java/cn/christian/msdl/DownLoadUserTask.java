package cn.christian.msdl;

/**
 * Created in IntelliJ IDEA.
 * MSDL
 * cn.christian.msdl
 *
 * @author Christian_Chen
 * @github freestyletime@foxmail.com
 * @mail mailchristianchen@gmail.com
 * @time 14-9-22 上午9:57
 * @describtion
 */
public class DownLoadUserTask {

    public String id = null;
    public long length = 0L;
    public long process = 0L;
    public String url = null;
    public String path = null;
    public DownLoadException e = null;
    public DownLoadTaskStatus status = DownLoadTaskStatus.WATTING;

    public DownLoadUserTask() {
    }

}
