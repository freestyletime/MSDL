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
    public DownLoadTaskStatus status = DownLoadTaskStatus.WATTING;
    public long length = 0L;
    public long process = 0L;
    public DownLoadException e = null;
    public String url = null;
    public String path = null;

    public DownLoadUserTask() {
    }

    public DownLoadUserTask(String id, DownLoadTaskStatus status, long length, long process, DownLoadException e, String url, String path) {
        this.id = id;
        this.status = status;
        this.length = length;
        this.process = process;
        this.e = e;
        this.url = url;
        this.path = path;
    }
}
