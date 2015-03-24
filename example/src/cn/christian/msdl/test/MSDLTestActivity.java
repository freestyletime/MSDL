package cn.christian.msdl.test;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import cn.christian.msdl.*;

import java.io.File;

/**
 * Created in IntelliJ IDEA.
 * MSDL
 * cn.christian.msdl.test
 *
 * @author Christian_Chen
 * @github freestyletime@foxmail.com
 * @mail mailchristianchen@gmail.com
 * @time 14-9-21 上午12:06
 * @describtion Test Activity
 */
public class MSDLTestActivity extends Activity implements View.OnClickListener{

    MSDLTestApplication application;
    DownLoader downLoader;

    TextView name;
    TextView process;
    ProgressBar bar;
    Button bt;

    DownLoadTaskStatus status = null;
    String task_id = "0";
    String basePath = Environment.getExternalStorageDirectory().getAbsolutePath();
    long progress = 0L;
    long length = 30490582L;
    String url = "http://gdown.baidu.com/data/wisegame/d52c3b17d08b2a53/baiduditu_568.apk";
    File apk = new File(basePath, url.substring(url.lastIndexOf("/")));


    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.obj instanceof DownLoadUserTask){
                DownLoadUserTask task = (DownLoadUserTask) msg.obj;
                dealTaskStatus(task);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        application = (MSDLTestApplication) getApplication();
        downLoader = application.downLoader.setBasePath(basePath);
        downLoader.register(this);

        name = (TextView) findViewById(R.id.tv_name);
        process = (TextView) findViewById(R.id.tv_process);
        bar = (ProgressBar) findViewById(R.id.pb);
        bt = (Button) findViewById(R.id.btn);
        bt.setOnClickListener(this);

        findViewById(R.id.btn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downLoader.cancel(task_id);
            }
        });

        name.setText("Task#"+task_id);

        if(apk.exists()){
            progress = apk.length();

            if(progress == length){
                bt.setText("finish:" + apk.getAbsolutePath());
                bt.setEnabled(false);
                status = DownLoadTaskStatus.FINISH;
            }
        }

        process.setText(progress + "/" + length);
        bar.setProgress(((int)(progress*100/length)));
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(status == null){
            DownLoadUserTask task = null;
            status = (task = downLoader.query(task_id)) == null ? null : task.status;
            changeBtn(status);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        downLoader.unRegister(this);
    }

    @DownLoadCallback
    public void fun(DownLoadUserTask task){
        Message.obtain(handler, 0, task).sendToTarget();
    }

    @Override
    public void onClick(View view) {
        if(status == null){
            downLoader.add(task_id, url);
        }else
            switch (status){
                case RUNNING:
                    downLoader.pause(task_id);
                    break;
                case WATTING:
                    downLoader.cancel(task_id);
                    break;
                case PAUSE:
                    downLoader.resume(task_id);
                    break;
                case CANCEL:
                    downLoader.add(task_id, url);
                    break;
                case FINISH:
                    downLoader.cancel(task_id);
                    break;
                case ERROR:
                    downLoader.add(task_id, url);
                    break;
            }
    }

    private void dealTaskStatus(DownLoadUserTask task){
        if(!task.id.equals(task_id))return;

        status = task.status;
        progress = task.process;
        changeBtn(task.status);

        switch (task.status){
            case RUNNING:
                if(task.length != length) {
                    Toast.makeText(this, "length error!", Toast.LENGTH_LONG).show();
                    return;
                }
            case PAUSE:
            case CANCEL:
            case FINISH:
                process.setText(progress + "/" + length);
                bar.setProgress(((int)(progress*100/length)));
                break;
            case ERROR:
                process.setText(progress + "/" + length);
                bar.setProgress(((int)(progress*100/length)));
                Toast.makeText(this, task.e.getMessage(), Toast.LENGTH_LONG).show();
                break;
            case WATTING:
                break;
        }
    }

    private void changeBtn(DownLoadTaskStatus status){
           if(status != null)
               switch (status){
                   case RUNNING:
                       bt.setText("pause");
                       if(!bt.isEnabled()) bt.setEnabled(true);

                       break;
                   case WATTING:
                       bt.setText("cancel");
                       if(bt.isEnabled()) bt.setEnabled(true);

                       break;
                   case PAUSE:
                       bt.setText("continue");
                       if(!bt.isEnabled()) bt.setEnabled(true);

                       break;
                   case CANCEL:
                       bt.setText("start");
                       if(bt.isEnabled()) bt.setEnabled(true);

                       break;
                   case FINISH:
                       bt.setText("finish:" + apk.getAbsolutePath());
                       if(bt.isEnabled()) bt.setEnabled(false);

                       break;
                   case ERROR:
                       bt.setText("start");
                       if(bt.isEnabled()) bt.setEnabled(true);
                       break;
               }
    }
}
