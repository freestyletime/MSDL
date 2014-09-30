package cn.christian.msdl.test;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import cn.christian.msdl.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
    ListView lv;
    Button bt;

    List<MSDLTestItem> items = new ArrayList<MSDLTestItem>();
    MSDLTestAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        application = (MSDLTestApplication) getApplication();
        downLoader = application.downLoader;
        downLoader.register(this);

        lv = (ListView) findViewById(R.id.lv);
        bt = (Button) findViewById(R.id.bt);
        bt.setOnClickListener(this);


        if(application.cache.size() != 0){
            if(adapter == null){
                int size = 0;
                String url = null;
                items.addAll(application.cache);

                adapter = new MSDLTestAdapter(this, items);
                lv.setAdapter(adapter);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        downLoader.unRegister(this);
        application.cache.clear();
        application.cache.addAll(items);
    }

    @DownLoadCallback
    void fun(DownLoadUserTask task){

        Loop:for(MSDLTestItem item : items){
            if(item.id.equals(task.id)){
                switch (task.status){
                    case WATTING:
                        showView(item.bar);
                        break Loop;
                    case RUNNING:
                        showView(item.bar);
                        item.length = task.length;
                        item.process = task.process;
                        adapter.notifyDataSetInvalidated();
                        break Loop;
                    case CANCLE:
                        goneView(item.bar);
                        Toast.makeText(this, item.name + " 已取消下载！", Toast.LENGTH_SHORT).show();
                        break Loop ;
                    case FINISH:
                        goneView(item.bar);
                        item.length = task.length;
                        item.process = task.process;
                        Toast.makeText(this, item.name + " 已完成下载！", Toast.LENGTH_SHORT).show();
                        break Loop;
                    case ERROR:
                        goneView(item.bar);
                        Toast.makeText(this, DownLoadException.code2message(task.errorCode), Toast.LENGTH_SHORT).show();
                        break Loop;
                }

            }
        }
    }

    private void showView(View view){
        if(view.getVisibility() == View.GONE){
            view.setVisibility(View.VISIBLE);
        }
    }

    private void goneView(View view){
        if(view.getVisibility() == View.VISIBLE){
            view.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        if(adapter == null){
            int size = 0;
            String url = null;
            while(size < 5){
                switch (size){
                    case 0:
                        url = "http://gdown.baidu.com/data/wisegame/b8c96d6e7e845bc4/baidu_16785677.apk";
                        break;
                    case 1:
                        url = "http://gdown.baidu.com/data/wisegame/09d27de64ab6e57c/baidushipin_1070101366.apk";
                        break;
                    case 2:
                        url = "http://gdown.baidu.com/data/wisegame/c761d2968b478011/baiduliulanqi_46.apk";
                        break;
                    case 3:
                        url = "http://gdown.baidu.com/data/wisegame/d52c3b17d08b2a53/baiduditu_568.apk";
                        break;
                    case 4:
                        url = "http://gdown.baidu.com/data/wisegame/bebf38e4ef8831f3/baidutieba_100860672.apk";
                        break;
                }
                items.add(new MSDLTestItem(downLoader.add(url),"Christian Test Link#" + size));
                ++size;
            }

            adapter = new MSDLTestAdapter(this, items);
            lv.setAdapter(adapter);
        }
    }
}
