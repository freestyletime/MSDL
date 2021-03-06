package cn.christian.msdl;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

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
class DownLoadTaskRunnable implements Callable<DownLoadTask> {

    Map<String, String> headers;
    private int TIMEOUT_CONN = 30000;
    private int TIMEOUT_READ = 30000;
    private File file;
    private DownLoadTask task;

    DownLoadTaskRunnable(DownLoadTask task) {
        this.task = task;
    }

    DownLoadTaskRunnable(DownLoadTask task, Map<String, String> headers, int connectTimeout, int readTimeout) {
        this(task);
        this.headers = headers;

        if (connectTimeout > 0)
            this.TIMEOUT_CONN = connectTimeout;
        if (readTimeout > 0)
            this.TIMEOUT_READ = readTimeout;
    }

    @Override
    public DownLoadTask call() throws Exception {
        if (check()) {
            try {
                download(setting());
            } catch (MalformedURLException e) {
                e.printStackTrace();
                task.status = DownLoadTaskStatus.ERROR;
                task.e = new DownLoadException(DownLoadTaskExceptionCode.MSDL_CODE_URL_INVALID);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                task.status = DownLoadTaskStatus.ERROR;
                task.e = new DownLoadException(DownLoadTaskExceptionCode.MSDL_CODE_FILE_DISABLE);
            } catch (IOException e) {
                e.printStackTrace();
                task.status = DownLoadTaskStatus.ERROR;
                task.e = new DownLoadException(e);
            }
        }

        return task;
    }

    //-----------------------------------------

    private boolean check() {

        file = new File(task.path);
        if (!file.exists()) {
            try {
                DownLoadUtils.mkdir(file.getParentFile());
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                file = null;
                task.status = DownLoadTaskStatus.ERROR;
                task.e = new DownLoadException(DownLoadTaskExceptionCode.MSDL_CODE_FILE_DISABLE);
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
            int code = conn.getResponseCode();
            long length = 0;
            if (code == HttpURLConnection.HTTP_OK) length = conn.getContentLength();
            conn.disconnect();

            if (length == -1 || length == 0) {
                task.status = DownLoadTaskStatus.ERROR;
                task.e = new DownLoadException(DownLoadTaskExceptionCode.MSDL_CODE_DOWNLOAD_CANT_READ_FILE_LEN);
            } else if (length != file.length()) {
                conn = (HttpURLConnection) url.openConnection();
                DownLoadTaskMethod method = task.method;
                conn.setRequestMethod(method.getValue());
                conn.setRequestProperty("Charsert", "UTF-8");
                conn.setRequestProperty("Accept-Encoding", "*");
                if (method == DownLoadTaskMethod.GET) conn.setRequestProperty("Content-Type", "application/stream");
//              if(method == DownLoadTaskMethod.POST) conn.setRequestProperty("Content-Type", "application/stream");
                conn.setRequestProperty("Range", "bytes=" + file.length() + "-");
                Set<Map.Entry<String, String>> headerEntrys = headers.entrySet();

                for (Map.Entry<String, String> entry : headerEntrys)
                    conn.setRequestProperty(entry.getKey(), entry.getValue());

                if (TIMEOUT_CONN > 0) conn.setConnectTimeout(TIMEOUT_CONN);
                if (TIMEOUT_READ > 0) conn.setReadTimeout(TIMEOUT_READ);

                conn.connect();

                code = conn.getResponseCode();
                task.status = DownLoadTaskStatus.RUNNING;
                task.length = length;

                if (code == HttpURLConnection.HTTP_OK) {
                    file.deleteOnExit();
                    file.createNewFile();
                } else if (code == HttpURLConnection.HTTP_PARTIAL) {
                    task.process = file.length();
                } else {
                    task.status = DownLoadTaskStatus.ERROR;
                    task.e = new DownLoadException(DownLoadTaskExceptionCode.MSDL_CODE_CONNECTION_FAIL, code);
                }
            } else {
                task.status = DownLoadTaskStatus.FINISH;
                task.length = length;
                task.process = file.length();
            }
        } catch (IOException e) {
            conn.disconnect();
            e.printStackTrace();
            task.status = DownLoadTaskStatus.ERROR;
            task.e = new DownLoadException(e);
        }

        return conn;
    }

    private void download(HttpURLConnection conn) throws IOException {
        if (conn != null && task.status.equals(DownLoadTaskStatus.RUNNING)) {
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            InputStream is = conn.getInputStream();

            raf.seek(file.length());
            int buffer = 2048; // 2KB
            int offset;
            byte[] bytes = new byte[buffer];

            do {
                if (task.isCancel) {
                    close(conn, is, raf);

                    if (task.isCancel) task.status = DownLoadTaskStatus.CANCEL;
                    break;
                } else {
                    if ((offset = is.read(bytes, 0, buffer)) > 0) {
                        if (!file.exists()) {
                            close(conn, is, raf);
                            task.status = DownLoadTaskStatus.ERROR;
                            task.e = new DownLoadException(DownLoadTaskExceptionCode.MSDL_CODE_DOWNLOAD_ABORT);
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
                            task.e = new DownLoadException(DownLoadTaskExceptionCode.MSDL_CODE_DOWNLOAD_FILE_SIZE_INVALID);
                            break;
                        }
                    }
                }
            } while (true);
        }
    }

    private void close(HttpURLConnection conn, InputStream is, RandomAccessFile raf) throws IOException {
        is.close();
        conn.disconnect();
        raf.close();
    }

    //-----------------------------------------
}
