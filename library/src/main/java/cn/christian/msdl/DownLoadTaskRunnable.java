package cn.christian.msdl;

import android.webkit.URLUtil;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created in IntelliJ IDEA.
 * MSDL
 * cn.christian.msdl
 *
 * @author Christian_Chen
 * @github freestyletime@foxmail.com
 * @mail mailchristianchen@gmail.com
 * @time 14-9-16 下午1:41
 * @describtion A runnable used in download
 */
class DownLoadTaskRunnable implements Runnable {

    private int TIMEOUT_CONN = 30000;
    private int TIMEOUT_READ = 30000;

    private File file;
    private DownLoadTask task;

    DownLoadTaskRunnable(DownLoadTask task){
        this.task = task;
    }

    DownLoadTaskRunnable(DownLoadTask task, int connectTimeout, int readTimeout){
        this(task);

        if(connectTimeout > 0)
            this.TIMEOUT_CONN = connectTimeout;
        if(readTimeout > 0)
            this.TIMEOUT_READ = readTimeout;
    }

    @Override
    public void run() {
        if(check()){
            try {
                download(setting());
            } catch(FileNotFoundException e) {
                task.status = DownLoadTaskStatus.ERROR;
                task.e = new DownLoadException(1003);
            }catch (IOException e) {
                e.printStackTrace();
                task.status = DownLoadTaskStatus.ERROR;
                task.e = new DownLoadException(e);
            }
        }
    }

    //-----------------------------------------

    private boolean check(){

        if(task.url == null || !URLUtil.isValidUrl(task.url)){
            task.status = DownLoadTaskStatus.ERROR;
            task.e = new DownLoadException(1002);
            return false;
        }

        file = new File(task.path);
        if(!file.exists()){
            try {
                DownLoadUtils.mkdir(file.getParentFile());
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                file = null;
                task.status = DownLoadTaskStatus.ERROR;
                task.e = new DownLoadException(1003);
                return false;
            }
        }

        return true;
    }

    private HttpURLConnection setting() throws IOException {
        URL url = new URL(task.url);
        HttpURLConnection conn = null;

        try {
            conn = (HttpURLConnection) url.openConnection();
            long length = conn.getContentLength();
            conn.disconnect();

            if(length != file.length()){
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(false);
                conn.setDoInput(true);
                conn.setUseCaches(false);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                if(TIMEOUT_CONN > 0)
                    conn.setConnectTimeout(TIMEOUT_CONN);
                if(TIMEOUT_READ > 0)
                    conn.setReadTimeout(TIMEOUT_READ);

                conn.setRequestProperty("Accept-Encoding", "musixmatch");
                conn.setRequestProperty("Content-Type", "application/stream");
                conn.setRequestProperty("Range", "bytes=" + file.length() + "-");
                conn.connect();

                int code = conn.getResponseCode();
                task.status = DownLoadTaskStatus.RUNNING;
                task.length = length;

                if (code == 200) {
                    file.deleteOnExit();
                    file.createNewFile();
                } else if (code == 206) {
                    task.process = file.length();
                } else {
                    task.status = DownLoadTaskStatus.ERROR;
                    task.e = new DownLoadException(code);
                }
            }else{
                task.status = DownLoadTaskStatus.FINISH;
                task.length = length;
                task.process = file.length();
            }

        }catch (IOException e){
            conn.disconnect();
            e.printStackTrace();
            task.status = DownLoadTaskStatus.ERROR;
            task.e = new DownLoadException(e);
        }

        return conn;
    }

    private void download(HttpURLConnection conn) throws IOException {
        if(task.status.equals(DownLoadTaskStatus.RUNNING)){
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            InputStream is = conn.getInputStream();

            raf.seek(file.length());
            int buffer = 4096;
            int offset;
            byte[] bytes = new byte[buffer];

            do {
                if(task.isCancel){
                    close(conn, is, raf);
                    task.status = DownLoadTaskStatus.CANCEL;
                    return;
                }else if(task.isPause){
                    close(conn, is, raf);
                    task.status = DownLoadTaskStatus.PAUSE;
                    return;
                }else{
                    if ((offset = is.read(bytes, 0, buffer)) > 0) {
                        if (!file.exists()) {
                            close(conn, is, raf);
                            task.status = DownLoadTaskStatus.ERROR;
                            task.e = new DownLoadException(1005);
                            break;
                        }

                        raf.write(bytes, 0, offset);
                        task.process += offset;
                    } else {
                        close(conn, is, raf);

                        if (task.length == task.process) {
                            task.status = DownLoadTaskStatus.FINISH;
                            break;
                        } else {
                            file.deleteOnExit();
                            task.status = DownLoadTaskStatus.ERROR;
                            task.e = new DownLoadException(1006);
                            break;
                        }
                    }
                }
            }while (true);
        }
    }

    private void close(HttpURLConnection conn, InputStream is, RandomAccessFile raf) throws IOException {
        is.close();
        conn.disconnect();
        raf.close();
    }

    //-----------------------------------------
}
