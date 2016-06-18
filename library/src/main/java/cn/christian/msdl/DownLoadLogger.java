package cn.christian.msdl;

import android.util.Log;

import java.util.Calendar;

/**
 * Created in IntelliJ IDEA.
 * MSDL
 * cn.christian.msdl
 *
 * @author Christian_Chen
 * @github freestyletime@foxmail.com
 * @mail mailchristianchen@gmail.com
 * @Time : 5/4/15 10:58 AM
 * @Description : Log frame for msdl
 */
class DownLoadLogger {

    public static boolean DEBUG = true;

    //-----------------------------------------
    private final String TAG = "MSDL";

    public void debug(String message) {
        if (DEBUG) {
            Log.d(TAG, Calendar.getInstance().getTime().toLocaleString() + " >>> " + message);
        }
    }

    public void info(String message) {
        if (DEBUG) {
            Log.i(TAG, Calendar.getInstance().getTime().toLocaleString() + " >>> " + message);
        }
    }

    public void warn(String message) {
        if (DEBUG) {
            Log.w(TAG, Calendar.getInstance().getTime().toLocaleString() + " >>> " + message);
        }
    }

    public void error(String message) {
        if (DEBUG) {
            Log.e(TAG, Calendar.getInstance().getTime().toLocaleString() + " >>> " + message);
        }
    }

    //-----------------------------------------

}
