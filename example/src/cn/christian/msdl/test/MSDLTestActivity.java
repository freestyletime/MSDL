package cn.christian.msdl.test;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import cn.christian.msdl.*;

import java.util.ArrayList;
import java.util.List;

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
public class MSDLTestActivity extends Activity{

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

        lv = (ListView) findViewById(R.id.lv);

        if(application.cache.size() != 0){
            if(adapter == null){
                items.addAll(application.cache);

                adapter = new MSDLTestAdapter(this, items, downLoader);
                lv.setAdapter(adapter);
            }
        }else{
            if(adapter == null){
                int size = 0;
                String url = null;
                long length = 0L;
                while(size < 5){
                    switch (size){
                        case 0:
                            url = "http://gdown.baidu.com/data/wisegame/b8c96d6e7e845bc4/baidu_16785677.apk";
                            length = 12556783L;
                            break;
                        case 1:
                            url = "http://gdown.baidu.com/data/wisegame/09d27de64ab6e57c/baidushipin_1070101366.apk";
                            length = 18065278L;
                            break;
                        case 2:
                            url = "http://gdown.baidu.com/data/wisegame/c761d2968b478011/baiduliulanqi_46.apk";
                            length = 14505047L;
                            break;
                        case 3:
                            url = "http://gdown.baidu.com/data/wisegame/d52c3b17d08b2a53/baiduditu_568.apk";
                            length = 30490582L;
                            break;
                        case 4:
                            url = "http://gdown.baidu.com/data/wisegame/bebf38e4ef8831f3/baidutieba_100860672.apk";
                            length = 19137736L;
                            break;
                    }
                    items.add(new MSDLTestItem(url, "Christian Test Link#" + size, length));
                    ++size;
                }
                adapter = new MSDLTestAdapter(this, items, downLoader);
                lv.setAdapter(adapter);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        application.cache.clear();
        application.cache.addAll(items);
    }
}
