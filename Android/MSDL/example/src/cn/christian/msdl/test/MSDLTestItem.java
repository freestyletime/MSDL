package cn.christian.msdl.test;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

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

    String id;
    String name;
    long length = 0L;
    long process = 0L;

    View root;
    TextView size;
    ProgressBar bar;

    public MSDLTestItem(String id, String name) {
        this.id = id;
        this.name = name;
    }


    public View getView(Context context){
        if(root == null){
            root = LayoutInflater.from(context).inflate(R.layout.item, null);
            TextView name = (TextView) root.findViewById(R.id.tv_name);
            size = (TextView) root.findViewById(R.id.tv_process);
            bar = (ProgressBar) root.findViewById(R.id.pb);

            name.setText(this.name);
            bar.setProgress(0);
            bar.setMax(100);

            bar.setTag(bar);
        }else {
            size.setText(process + " / " + length);
            if(length != 0L)
                bar.setProgress((int)((process*100)/length));
        }

        return root;
    }

}
