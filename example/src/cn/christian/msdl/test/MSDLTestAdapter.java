package cn.christian.msdl.test;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.christian.msdl.DownLoadCallback;
import cn.christian.msdl.DownLoadUserTask;
import cn.christian.msdl.DownLoader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created in IntelliJ IDEA.
 * MSDL
 * cn.christian.msdl.test
 *
 * @author Christian_Chen
 * @github freestyletime@foxmail.com
 * @mail mailchristianchen@gmail.com
 * @time 14-9-21 下午8:59
 * @describtion
 */
public class MSDLTestAdapter extends BaseAdapter{

    DownLoader downLoader;

    Context context;
    List<MSDLTestItem> items;
    Map<String, ViewRoot> views = new HashMap<String, ViewRoot>();
    MSDLTestItem item;

    Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    int index = msg.arg1;
                    String id = String.valueOf(index);
                    MSDLTestItem item = getItem(index);
                    if(views.containsKey(id)) {
                        ViewRoot root = views.get(id);
                        root.process.setText(item.process + "/" + item.length);
                        root.bar.setProgress((int) ((item.process * 100) / item.length));
                        root.btn.setText("pause");
                        root.FLAG = 1;
                    }
                    break;
            }
        }
    };

    public MSDLTestAdapter(Context context, List<MSDLTestItem> items, DownLoader downLoader){
        this.context = context;
        this.items = items;
        this.downLoader = downLoader;
        downLoader.register(this);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public MSDLTestItem getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewRoot root;
        item = getItem(i);

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.item, null);
            root = new ViewRoot();
            root.name = (TextView) view.findViewById(R.id.tv_name);
            root.process = (TextView) view.findViewById(R.id.tv_process);
            root.bar = (ProgressBar) view.findViewById(R.id.pb);
            root.btn = (Button) view.findViewById(R.id.btn);
            root.btn.setOnClickListener(new MSDLClickListener(String.valueOf(i)));
        }else{
            root = (ViewRoot) view.getTag();
            if(root == null){
                root = new ViewRoot();
                root.name = (TextView) view.findViewById(R.id.tv_name);
                root.process = (TextView) view.findViewById(R.id.tv_process);
                root.bar = (ProgressBar) view.findViewById(R.id.pb);
                root.btn = (Button) view.findViewById(R.id.btn);
                root.btn.setOnClickListener(new MSDLClickListener(String.valueOf(i)));
            }
        }

        root.btn.setTag(root.FLAG);
        root.name.setText(item.name);
        root.process.setText(item.process + "/" +item.length);
        root.bar.setProgress((int)((item.process * 100) / item.length));
        check(String.valueOf(i), root.btn);
        final ViewRoot viewRoot = root;
        views.put(String.valueOf(i), viewRoot);
        view.setTag(root);

        return view;
    }

    @DownLoadCallback
    void fun(DownLoadUserTask task){
        String id = task.id;
        int index = Integer.parseInt(id);
        MSDLTestItem item = getItem(index);
        switch (task.status){
            case RUNNING:
                item.length = task.length;
                item.process = task.process;
//                notifyDataSetInvalidated();
                Message.obtain(handler, 0, index).sendToTarget();
                break;
            case WATTING:
                break;
            case PAUSE:
                break;
            case CANCLE:
                break;
            case ERROR:
                break;
            case FINISH:
                break;
        }
    }

    void check(String id, Button btn){
        DownLoadUserTask task = downLoader.query(id);
        if(task == null){
            btn.setText("start");
        }else{
            fun(task);
        }
    }

    class ViewRoot{
        int FLAG = 0;//{0 : start, 1 : running, 2 : watting, 3 : continue, 4 : pause, 5 : finish}
        TextView name;
        TextView process;
        ProgressBar bar;
        Button btn;
    }

    private class MSDLClickListener implements View.OnClickListener{

        String id;
        ViewRoot root;

        public MSDLClickListener(String id){
            this.id = id;
        }

        @Override
        public void onClick(View view) {
            root = views.get(id);
            if (root != null) {
                MSDLTestItem item = getItem(Integer.parseInt(id));
                switch (root.FLAG) {
                    case 0:
                        root.FLAG = 2;
                        ((Button)view).setText("watting...");
                        downLoader.add(id, item.url);
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    case 4:
                        break;
                    case 5:
                        break;
                }
            }
        }
    }
}
