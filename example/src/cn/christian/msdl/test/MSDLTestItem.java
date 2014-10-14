package cn.christian.msdl.test;

import android.os.Environment;

import java.io.File;

/**
 * Created in IntelliJ IDEA.
 * MSDL
 * cn.christian.msdl.test
 *
 * @author Christian_Chen
 * @github freestyletime@foxmail.com
 * @mail mailchristianchen@gmail.com
 * @time 14-9-21 上午12:40
 * @describtion
 */
public class MSDLTestItem {
    String url;
    String name;
    long length;
    long process = 0;

    public MSDLTestItem(String url, String name, long length) {
        this.url = url;
        this.name = name;
        this.length = length;

        File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), url.substring(url.lastIndexOf("/")));
        if(f.exists()){
            process = f.length();
        }
    }
}
