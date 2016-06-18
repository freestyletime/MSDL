package cn.christian.msdl.test;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created in IntelliJ IDEA.
 * MSDL
 * cn.christian.msdl.test
 *
 * @author Christian_Chen
 * @github freestyletime@foxmail.com
 * @mail mailchristianchen@gmail.com
 * @Time : 5/6/15 11:16 AM
 * @Description :
 */
public class MSDLTestAdapter extends BaseAdapter {

    List<MSDLTestItem> beans;

    MSDLTestAdapter(List<MSDLTestItem> beans) {
        this.beans = beans;
    }

    @Override
    public int getCount() {
        return beans.size();
    }

    @Override
    public MSDLTestItem getItem(int i) {
        return beans.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return getItem(i).getView();
    }
}
