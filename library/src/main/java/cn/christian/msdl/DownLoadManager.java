package cn.christian.msdl;

import android.content.Context;
import android.text.TextUtils;

import java.lang.reflect.Method;

/**
 * Created in IntelliJ IDEA.
 * MSDL
 * cn.christian.msdl
 *
 * @author Christian_Chen
 * @github freestyletime@foxmail.com
 * @mail mailchristianchen@gmail.com
 * @time 14-9-16 上午11:38
 * @describtion A manager that can manage all user request and operate owned service.
 */
public class DownLoadManager implements DownLoader{

    DownLoadManagerService service;

    //-----------------------------------------
    private DownLoadManagerService getService(Context context){
        return  service == null ? service = DownLoadManagerService.getInstance(context) : service;
    }
    //-----------------------------------------

    public DownLoadManager(Context context){
        super();
        getService(context);
    }

    @Override
    public void register(Object obj) {
        Method method;

        if(obj == null)
            throw new DownLoadException("DownLoadManager::register user must send a initialized object.");
        if((method = DownLoadUtils.inject(obj)) == null)
            throw new DownLoadException("DownLoadManager::register the object you inject must have a method like " +
                    " void fun(DownLoadUserTask task)");

        service.bind(obj, method);
    }

    @Override
    public void unRegister(Object obj) {
        if(obj != null)
            service.unBind(obj);
    }

    @Override
    public DownLoader setRepeatTime(long interval) {
        if(interval > 0 )
            service.setRepeatTime(interval);
        return this;
    }

    @Override
    public DownLoader setThreadSize(int size) {
        if(size > 0)
            service.setThreadSize(size);
        return this;
    }

    @Override
    public DownLoader setBasePath(String path) {
        if(path != null)
            service.setBasePath(path);
        return this;
    }

    @Override
    public DownLoader setConnectTimeout(int timeout) {
        if(timeout > 0)
            service.setConnectTimeout(timeout);
        return this;
    }

    @Override
    public DownLoader setReadTimeout(int timeout) {
        if(timeout > 0)
            service.setReadTimeout(timeout);
        return this;
    }

    @Override
    public String add(String url) {
        if(TextUtils.isEmpty(url)) return null;

        DownLoadTask task = new DownLoadTask(DownLoadUtils.uniqueId(), url, DownLoadUtils.makePath(service.basePath, url));
        service.add(task);
        return task.id;
    }

    @Override
    public void add(String id, String url) {
        if(TextUtils.isEmpty(url)||TextUtils.isEmpty(id)) return;
        service.add(new DownLoadTask(id, url, DownLoadUtils.makePath(service.basePath, url)));
    }

    @Override
    public void resume(String id) {
        service.resume(id);
    }

    @Override
    public void pause(String id) {
        service.pause(id);
    }

    @Override
    public void cancle(String id) {
        service.cancle(id);
    }

    @Override
    public DownLoadTaskStatus query(String id) {
        return service.query(id);
    }
}
