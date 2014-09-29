package cn.christian.msdl.test;

import android.app.Application;
import cn.christian.msdl.DownLoadManager;
import cn.christian.msdl.DownLoader;

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
 * @time 14-9-21 上午12:16
 * @describtion
 */
public class MSDLTestApplication extends Application {

    public static DownLoader downLoader;
    public static List<MSDLTestItem> cache = new ArrayList<MSDLTestItem>();

    @Override
    public void onCreate() {
        super.onCreate();
        downLoader = new DownLoadManager(this);
    }
}
