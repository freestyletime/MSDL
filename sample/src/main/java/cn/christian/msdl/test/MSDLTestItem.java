package cn.christian.msdl.test;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import cn.christian.msdl.*;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created in IntelliJ IDEA.
 * MSDL
 * cn.christian.msdl.test
 *
 * @author Christian_Chen
 * @github freestyletime@foxmail.com
 * @mail mailchristianchen@gmail.com
 * @Time : 5/6/15 11:17 AM
 * @Description :
 */
public class MSDLTestItem implements DownLoadTaskListener{

    Context c;
    DownLoader d;
    MSDLTestBean b;

    MSDLTestItem(Context c, DownLoader d, MSDLTestBean b){
        this.c = c;
        this.d = d;
        this.b = b;
    }

    View root;
    ViewRoot tag;

    /** 0-start 1-watting 2-running 4-cancel 5-error 6-finish*/
    AtomicInteger flag = new AtomicInteger(0);
    AtomicInteger clickFlag = new AtomicInteger(0);

    View getView(){
        if(root == null) {
            root = LayoutInflater.from(c).inflate(R.layout.item, null);

            tag = new ViewRoot();
            tag.name = (TextView) root.findViewById(R.id.name);
            tag.path = (TextView) root.findViewById(R.id.path);
            tag.process = (TextView) root.findViewById(R.id.process);
            tag.bar = (ProgressBar) root.findViewById(R.id.bar);
            tag.btn = (Button) root.findViewById(R.id.btn);
            tag.btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickFlag.set(flag.get());

                    switch (flag.get()) {
                        case 0:
                        case 4:
                        case 5:
                            d.add(b.id, b.url, MSDLTestItem.this);
                            break;
                        case 1:
                        case 2:
                            d.cancel(b.id);
                            break;
                        case 6:
                            installApk(b.path, b.name);
                            break;
                    }

                    view.setEnabled(false);
                    root.invalidate();
                }
            });
        } else {
            tag = (ViewRoot) root.getTag();
        }

        tag.name.setText(b.name);
        tag.path.setText(b.path + "/" + b.name);
        setStatus(tag);
        root.setTag(tag);

        return root;
    }

    private void setStatus(ViewRoot tag){
        DownLoadUserTask task = d.query(b.id);

        //----------------------------------------
        long p = 0;
        File f = new File(b.path, b.name);
        if(f.exists()) p = f.length();
        tag.process.setText(String.format(c.getString(R.string.process), p, b.length));
        tag.bar.setProgress(((int) (p * 100 / b.length)));
        //----------------------------------------

        if(task == null){
            if(p == b.length){
                tag.btn.setText(R.string.finish);
                flag.set(6);
            }else {
                if(p == 0)
                    tag.btn.setText(R.string.start);
                else
                    tag.btn.setText(R.string.resume);

                flag.set(0);
            }
        }else{
            d.setOnDownLoadTaskListener(b.id, this);
            int btnStatusRes;
            switch (task.status){
                case RUNNING:
                    btnStatusRes = R.string.cancle;
                    flag.set(2);
                    break;
                case FINISH:
                    btnStatusRes = R.string.finish;
                    flag.set(6);
                    break;
                case ERROR:
                    flag.set(5);
                    btnStatusRes = R.string.start;
                    break;
                case CANCEL:
                    flag.set(4);
                    btnStatusRes = R.string.start;
                    break;
                default:
                    btnStatusRes = R.string.waiting;
                    flag.set(1);
                    break;
            }

            tag.btn.setText(btnStatusRes);
        }
    }

    @Override
    public void running(String id, long length, long process) {
        flag.set(2);
        int percent = (int)((process * 100) / length);
        Message.obtain(handler, flag.get(), percent, (int)process).sendToTarget();
    }
    @Override
    public void waitting(String id) {
        flag.set(1);
        Message.obtain(handler, flag.get()).sendToTarget();
    }
    @Override
    public void finish(String id, String path) {
        flag.set(6);
        Message.obtain(handler, flag.get()).sendToTarget();
    }
    @Override
    public void cancel(String id) {
        flag.set(4);
        Message.obtain(handler, flag.get()).sendToTarget();
    }
    @Override
    public void error(String id, DownLoadException e) {
        flag.set(5);
        Message.obtain(handler, flag.get(), e.getMessage()).sendToTarget();
    }

    private void installApk(String path, String name) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(path, name)), "application/vnd.android.package-archive");
        c.startActivity(intent);
    }

    private Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            if(tag == null) return;
            int flag = msg.what;
            int clickFlag = MSDLTestItem.this.clickFlag.get();

            switch (flag){
                case 1:
                    tag.btn.setText(R.string.waiting);
                    break;
                case 2:
                    tag.process.setText(String.format(c.getString(R.string.process), msg.arg2, b.length));
                    tag.bar.setProgress(msg.arg1);
                    tag.btn.setText(R.string.cancle);
                    break;
                case 4:
                    tag.btn.setText(R.string.start);
                    Toast.makeText(c, b.name + "\n" + "被从下载队列中移除",Toast.LENGTH_LONG).show();
                    break;
                case 5:
                    tag.btn.setText(R.string.start);
                    Toast.makeText(c, b.name + "\n" + ((String)msg.obj),Toast.LENGTH_LONG).show();
                    break;
                case 6:
                    tag.btn.setText(R.string.finish);
                    tag.bar.setProgress(100);
                    Toast.makeText(c, b.name + "\n" + "下载完成",Toast.LENGTH_LONG).show();
                    break;
            }

            /** 0-start 1-watting 2-running 4-cancel 5-error 6-finish*/
            if((clickFlag == 0 || clickFlag == 4 || clickFlag == 5 && flag == 1 || flag == 2) ||
                    (clickFlag == 1 && flag == 4) || clickFlag == 2 ||
                    (flag == 1 || flag == 2) || flag == 5 || flag == 6) {
                tag.btn.setEnabled(true);
                root.invalidate();
            }
        }
    };

    private static class ViewRoot{
        TextView name;
        TextView path;
        TextView process;
        ProgressBar bar;
        Button btn;
    }
}
