package cn.christian.msdl.test;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.Environment;
import cn.christian.msdl.DownLoader;

import java.util.LinkedList;

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
public class MSDLTestActivity extends ListActivity {

    DownLoader downLoader;

    LinkedList<MSDLTestItem> beans = new LinkedList<MSDLTestItem>();
    MSDLTestAdapter adapter = new MSDLTestAdapter(beans);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        downLoader = MSDLTestApplication.downLoader;
        setListAdapter(adapter);

        //----------------config-----------------
        String basePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        downLoader
                .setBasePath(basePath)
                .setRepeatTime(1000)
                .debug(true)
                .setThreadSize(3);

        setData(basePath);
    }

    private void setData(String basePath) {
        MSDLTestBean bean1 = new MSDLTestBean(
                "0",
                "http://gdown.baidu.com/data/wisegame/d52c3b17d08b2a53/baiduditu_568.apk",
                "baiduditu_568.apk",
                basePath,
                30490582);

        MSDLTestBean bean2 = new MSDLTestBean(
                "1",
                "http://gdown.baidu.com/data/wisegame/6fe7456c3e3c7afb/shoujibaidu_16787209.apk",
                "shoujibaidu_16787209.apk",
                basePath,
                24338211);

        MSDLTestBean bean3 = new MSDLTestBean(
                "2",
                "http://gdown.baidu.com/data/wisegame/fb35bba8f4a4e728/baiduliulanqi_71.apk",
                "baiduliulanqi_71.apk",
                basePath,
                17198854);

        MSDLTestBean bean4 = new MSDLTestBean(
                "3",
                "http://gdown.baidu.com/data/wisegame/01b8d9054ff3210b/baidushoujizhushou_16785259.apk",
                "baidushoujizhushou_16785259.apk",
                basePath,
                6297500);

        MSDLTestBean bean5 = new MSDLTestBean(
                "4",
                "http://gdown.baidu.com/data/wisegame/0d332439f0cb4962/baidushipin_1071101820.apk",
                "baidushipin_1071101820.apk",
                basePath,
                23130569);

        MSDLTestBean bean6 = new MSDLTestBean(
                "5",
                "http://gdown.baidu.com/data/wisegame/f54b8d57b3bdaddb/kaixinbuyu_15.apk",
                "kaixinbuyu_15.apk",
                basePath,
                15689347);

        MSDLTestBean bean7 = new MSDLTestBean(
                "6",
                "http://gdown.baidu.com/data/wisegame/82e5813de6e901af/UCliulanqi_165.apk",
                "UCliulanqi_165.apk",
                basePath,
                19475231);

        MSDLTestBean bean8 = new MSDLTestBean(
                "7",
                "http://gdown.baidu.com/data/wisegame/17e6ef4d841b0a96/YY_30045.apk",
                "YY_30045.apk",
                basePath,
                27147972);

        MSDLTestBean bean9 = new MSDLTestBean(
                "8",
                "http://gdown.baidu.com/data/wisegame/9f919014f6acc26b/duoshuoyingyu_4.apk",
                "duoshuoyingyu_4.apk",
                basePath,
                16139878);

        MSDLTestBean bean10 = new MSDLTestBean(
                "9",
                "http://gdown.baidu.com/data/wisegame/9a012dfd36a7dfdb/gushicidian_32.apk",
                "gushicidian_32.apk",
                basePath,
                24575788);

        beans.add(new MSDLTestItem(this, downLoader, bean1));
        beans.add(new MSDLTestItem(this, downLoader, bean2));
        beans.add(new MSDLTestItem(this, downLoader, bean3));
        beans.add(new MSDLTestItem(this, downLoader, bean4));
        beans.add(new MSDLTestItem(this, downLoader, bean5));
        beans.add(new MSDLTestItem(this, downLoader, bean6));
        beans.add(new MSDLTestItem(this, downLoader, bean7));
        beans.add(new MSDLTestItem(this, downLoader, bean8));
        beans.add(new MSDLTestItem(this, downLoader, bean9));
        beans.add(new MSDLTestItem(this, downLoader, bean10));

        adapter.notifyDataSetChanged();
    }
}
