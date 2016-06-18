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
class DownLoadTask extends DownLoadUserTask implements Cloneable {

    public boolean isCancel = false;

    DownLoadTask(String id, String url, String path) {
        super();
        this.id = id;
        this.url = url;
        this.path = path;
    }

    @Override
    protected DownLoadTask clone() {

        DownLoadTask task = new DownLoadTask(this.id, this.url, this.path);
        task.e = this.e;
        task.length = this.length;
        task.status = this.status;
        task.process = this.process;
        task.isCancel = this.isCancel;

        return task;
    }
}
