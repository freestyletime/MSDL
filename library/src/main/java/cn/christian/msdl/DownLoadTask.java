package cn.christian.msdl;

/**
 * Created in IntelliJ IDEA.
 * MSDL
 * cn.christian.msdl
 *
 * @author Christian_Chen
 * @github freestyletime@foxmail.com
 * @mail mailchristianchen@gmail.com
 * @time 14-9-16 上午11:48
 * @describtion A inner entity
 */
class DownLoadTask extends DownLoadUserTask{

    public boolean isCancel = false;
    public boolean isPause = false;

    DownLoadTask(String id, String url, String path) {
        super();
        this.id = id;
        this.url = url;
        this.path = path;
    }
}
